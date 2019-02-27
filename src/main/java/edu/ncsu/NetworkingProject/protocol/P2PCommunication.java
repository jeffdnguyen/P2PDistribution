package edu.ncsu.NetworkingProject.protocol;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
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
        try(final Socket socket = new Socket()){
            socket.connect(new InetSocketAddress("google.com", 80));
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendHeaders(StringBuilder stringBuilder, List<P2PHeader> headers) {
        if (headers.stream().noneMatch(header -> header.name.equals("Host"))) {
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
            return P2PMessage.constructMessageFromByteBuffer(buffer);
        } else if (type == 1) {
            return P2PResponse.constructResponseFromByteBuffer(buffer);
        } else {
            throw new RuntimeException();
        }
    }

}
