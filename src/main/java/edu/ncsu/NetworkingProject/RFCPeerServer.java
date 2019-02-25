package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.P2PResponse;
import edu.ncsu.NetworkingProject.protocol.ProtocolException;
import edu.ncsu.NetworkingProject.protocol.Status;
import edu.ncsu.NetworkingProject.protocol.messages.GetRFCMessage;
import edu.ncsu.NetworkingProject.protocol.messages.RFCQueryMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RFCPeerServer implements Runnable {

    private final RFCIndex rfcIndex;
    private final ServerSocket socket;
    private final File rfcFolder;


    public RFCPeerServer(int port, RFCIndex rfcIndex) {
        this.rfcIndex = rfcIndex;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Unable to start RFC server!", e);
        }
        this.rfcFolder = new File("./rfcs/" + port);
        this.rfcFolder.mkdir();
        addLocalRFCFiles();
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

    private void addLocalRFCFiles() {
        for (File file : rfcFolder.listFiles()) {
            if (!file.getName().startsWith("rfc")) continue;
            RFCFile rfcFile = new RFCFile(file);
            rfcIndex.index.add(new RFCIndexEntry(rfcFile.id, rfcFile.title));
        }
    }

    @Override
    public void run() {
        Connection connection = waitForNewConnection();

        while (true) {
            P2PMessage message;
            try {
                message = (P2PMessage) connection.waitForNextCommunication();
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof EOFException)) throw e;

                try {
                    connection = new Connection(socket.accept());
                    message = (P2PMessage) connection.waitForNextCommunication();
                } catch (IOException e1) {
                    throw new RuntimeException("Unable to set up new connection", e1);
                }
            }

            if (message instanceof RFCQueryMessage) {
                byte[] indexAsArray;
                synchronized (rfcIndex) {
                    indexAsArray = Utils.objectToByteArray(rfcIndex);
                }
                connection.send(new P2PResponse(Status.SUCCESS, indexAsArray));
            } else if (message instanceof GetRFCMessage) {
                byte[] rfcFileAsBytes;
                try {
                    FileInputStream fileIn = new FileInputStream(new File(rfcFolder, "rfc" + ((GetRFCMessage) message).getRFCID() + ".txt"));

                    rfcFileAsBytes = fileIn.readAllBytes();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("File could not be found!", e);
                } catch (IOException e) {
                    throw new RuntimeException("Trouble reading file", e);
                }
                connection.send(new P2PResponse(Status.SUCCESS, rfcFileAsBytes));
            } else {
                throw new ProtocolException.UnexpectedMessageException(message);
            }
        }
    }

}
