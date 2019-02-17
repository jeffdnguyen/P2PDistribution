package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RFCPeerServer implements Runnable {

    private final RFCIndex rfcIndex;
    private final ServerSocket socket;


    public RFCPeerServer(int port, RFCIndex rfcIndex) {
        this.rfcIndex = rfcIndex;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Unable to start RFC server!", e);
        }
    }

    private Connection waitForNewConnection() {
        Socket connectionSocket;
        try {
            connectionSocket = socket.accept();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start RFC server!", e);
        }
        return new Connection(connectionSocket);
    }

    @Override
    public void run() {
        Connection connection = waitForNewConnection();

        while (true) {
            P2PMessage message;
            try {
                message = connection.waitForNextMessage();
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof EOFException)) throw e;

                try {
                    connection = new Connection(socket.accept());
                    message = connection.waitForNextMessage();
                } catch (IOException e1) {
                    throw new RuntimeException("Unable to set up new connection", e1);
                }
            }

            if (message instanceof RFCQueryMessage) {
                synchronized (rfcIndex) {
                    connection.send(new RFCIndexMessage(rfcIndex));
                }
            } else if (message instanceof GetRFCMessage) {
                connection.send(new RFCResponseMessage(((GetRFCMessage) message).getRFCID()));
            } else {
                throw new ProtocolException.UnexpectedMessageException(message);
            }
        }
    }

}
