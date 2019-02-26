package edu.ncsu.NetworkingProject.protocol;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

public abstract class P2PCommunication {

    /**
     * The current protocol version.
     */
    protected final String VERSION = "1.0";

    /**
     * The name of the protocol.
     */
    protected final String PROTOCOL_NAME = "P2P-DI";

    public static String getHostname() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendHeaders(StringBuilder stringBuilder, List<P2PHeader> headers) {
        
        boolean noHostHeader = true;
        for (P2PHeader header : headers) {
            if (header.equals(new P2PHeader( "Host", getHostname() ) )) {
                noHostHeader = false;
                break;
            }
        }
        
        if (noHostHeader) {
            headers.add( new P2PHeader( "Host", getHostname() ) );
        }
        
        for (P2PHeader header : headers) {
            stringBuilder.append(header.name);
            stringBuilder.append(": ");
            stringBuilder.append(header.value);
            stringBuilder.append("\n");
        }
    }

    public abstract byte[] toByteArray();

    public static P2PCommunication constructCommunicationFromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte type = buffer.get();
        if (type == 0) {
          P2PMessage message = P2PMessage.constructMessageFromByteBuffer(buffer);
          System.out.println(message.toString());
          return message;
        } else if (type == 1) {
            P2PResponse response = P2PResponse.constructResponseFromByteBuffer(buffer);
            System.out.println(response.toString());
            return response;
        } else {
            throw new RuntimeException();
        }
    }

}
