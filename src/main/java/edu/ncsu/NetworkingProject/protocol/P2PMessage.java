package edu.ncsu.NetworkingProject.protocol;

import edu.ncsu.NetworkingProject.Pair;
import edu.ncsu.NetworkingProject.protocol.messages.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class P2PMessage extends P2PCommunication {

    /**
     * A list of all the message types and their classes.
     */
    private static final ArrayList<Pair<String, Class<? extends P2PMessage>>> messageTypes = new ArrayList<>();

    /**
     * Registers the message type.
     *
     * @param name the name of the message type.
     * @param messageClass the class which represents this message type.
     */
    private static void registerMessageType(String name, Class<? extends P2PMessage> messageClass) {
        messageTypes.add(new Pair<>(name, messageClass));
    }

    /*
     * You must register every message type here.
     *
     * The first parameter is the message type name and the second is the class which handles it.
     */
    static {
        registerMessageType("GetRFC", GetRFCMessage.class);
        registerMessageType("Register", RegisterMessage.class);
        registerMessageType("Leave", LeaveMessage.class);
        registerMessageType("PQuery", PQueryMessage.class);
        registerMessageType("KeepAlive", PQueryMessage.class);
        registerMessageType("RFCQuery", RFCQueryMessage.class);
    }

    private static P2PMessage findAndInvokeConstructorForMessage(String messageName, String argument, List<P2PHeader> headers) {
        for (Pair<String, Class<? extends P2PMessage>> messageType : messageTypes) {
            if (messageName.equals(messageType.first)) {
                try {
                    return messageType.second.getDeclaredConstructor(String.class, List.class).newInstance(argument, headers);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
        throw new ProtocolException.NoSuchMessageType(messageName);
    }

    /**
     * Turns a string representation of a message into the class itself.
     *
     * @param messageAsBytes the string representation.
     * @return the message object.
     */
    protected static P2PMessage constructMessageFromByteBuffer(ByteBuffer messageAsBytes) {
        String messageAsString = Charset.defaultCharset().decode(messageAsBytes).toString();
        String[] lines = messageAsString.split("\n");
        String[] firstLineTokens = lines[0].split(" ");

        StringBuilder argument = new StringBuilder();
        for (int i = 1; i < firstLineTokens.length - 1; i++) {
            if (!argument.toString().isEmpty()) argument.append(" ");
            argument.append(firstLineTokens[i]);
        }

        List<P2PHeader> headers = Arrays.stream(lines)
                .skip(1)
                .map(line -> new P2PHeader(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 2)))
                .collect(Collectors.toList());

        return findAndInvokeConstructorForMessage(firstLineTokens[0], argument.toString(), headers);
    }

    /**
     * Gets the argument for this message. Null if no argument.
     *
     * @return the said argument.
     */
    protected abstract String getMethodArgument();

    /**
     * The message object should add in any headers
     * which should appear as part of this object.
     *
     * @param headers the list of headers to be included in the message.
     */
    protected abstract void addHeaders(LinkedList<P2PHeader> headers);

    private String getTextComponent() {
        StringBuilder outputText = new StringBuilder();

        String methodName = messageTypes.stream()
                .filter(messageType -> messageType.second == this.getClass())
                .findAny()
                .orElseThrow(IllegalStateException::new)
                .first;
        outputText.append(methodName);
        outputText.append(" ");
        if (getMethodArgument() != null) {
            outputText.append(getMethodArgument());
            outputText.append(" ");
        }
        outputText.append(PROTOCOL_NAME);
        outputText.append("/");
        outputText.append(VERSION);

        outputText.append("\n");

        LinkedList<P2PHeader> headers = new LinkedList<>();
        addHeaders(headers);

        appendHeaders(outputText, headers);

        return outputText.toString();
    }

    @Override
    public byte[] toByteArray() {
        byte[] textAsBytes = getTextComponent().getBytes();
        // Add a 0 to identify this as a P2PMessage
        return ByteBuffer.allocate(1 + textAsBytes.length).put((byte) 0).put(textAsBytes).array();
    }

    @Override
    public String toString() {
        return getTextComponent();
    }

}
