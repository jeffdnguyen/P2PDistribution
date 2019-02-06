package edu.ncsu.NetworkingProject.protocol;

import edu.ncsu.NetworkingProject.Pair;

import java.lang.reflect.InvocationTargetException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class P2PMessage {

    /**
     * The current protocol version.
     */
    private final String VERSION = "1.0";

    /**
     * The name of the protocol.
     */
    private final String PROTOCOL_NAME = "P2P-DI";

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
    }

    /**
     * Turns a string representation of a message into the class itself.
     *
     * @param messageAsString the string representation.
     * @return the message object.
     */
    public static P2PMessage constructMessageFromString(String messageAsString) {
        String[] lines = messageAsString.split("\n");
        String[] firstLineTokens = lines[0].split(" ");

        StringBuilder argument = new StringBuilder();
        for (int i = 1; i < firstLineTokens.length - 1; i++) {
            if (!argument.toString().isEmpty()) argument.append(" ");
            argument.append(firstLineTokens[i]);
        }

        List<P2PHeader> headers = Arrays.stream(lines)
                .skip(1)
                .map(line -> new P2PHeader(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1)))
                .collect(Collectors.toList());

        P2PMessage message = null;
        for (Pair<String, Class<? extends P2PMessage>> messageType : messageTypes) {
            if (firstLineTokens[0].equals(messageType.first)) {
                try {
                    message = messageType.second.getDeclaredConstructor(String.class, List.class).newInstance(argument.toString(), headers);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
        if (message == null) {
            throw new ProtocolException.NoSuchMessageType(firstLineTokens[0]);
        }

        return message;
    }

    /**
     * Gets the argument for this message.
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

    private String getHostname() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        String methodName = messageTypes.stream()
                .filter(messageType -> messageType.second == this.getClass())
                .findAny()
                .orElseThrow(IllegalStateException::new)
                .first;
        output.append(methodName);
        output.append(" ");
        if (getMethodArgument() != null) {
            output.append(getMethodArgument());
            output.append(" ");
        }
        output.append(PROTOCOL_NAME);
        output.append("/");
        output.append(VERSION);

        output.append("\n");

        LinkedList<P2PHeader> headers = new LinkedList<>();
        headers.add(new P2PHeader("Host", getHostname()));
        addHeaders(headers);

        for (P2PHeader header : headers) {
            output.append(header.name);
            output.append(": ");
            output.append(header.value);
            output.append("\n");
        }
        return output.toString();
    }
}
