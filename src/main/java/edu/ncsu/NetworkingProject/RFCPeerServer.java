package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.*;
import edu.ncsu.NetworkingProject.protocol.messages.GetRFCMessage;
import edu.ncsu.NetworkingProject.protocol.messages.RFCQueryMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RFCPeerServer implements Runnable {

    private final RFCIndex rfcIndex;
    private final ServerSocket socket;
    private final File rfcFolder;
    private final boolean isTestingScenario;

    public RFCPeerServer(int port, RFCIndex rfcIndex, boolean isTestingScenario) {
        this.rfcIndex = rfcIndex;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Unable to start RFC server!", e);
        }
        this.rfcFolder = new File("./rfcs/" + port);
        this.rfcFolder.mkdir();
        this.isTestingScenario = isTestingScenario;
        addLocalRFCFiles(port);
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

    private void addLocalRFCFiles(int port) {
        for (File file : rfcFolder.listFiles()) {
            if (!file.getName().startsWith("rfc")) continue;
            RFCFile rfcFile = new RFCFile(file);
            rfcIndex.index.add(new RFCIndexEntry(rfcFile.id, rfcFile.title, P2PCommunication.getHostname(), port));
        }
    }

    @Override
    public void run() {
        while (true) {
            final Connection connection = waitForNewConnection();
            new Thread(() -> {
                while (true) {
                    P2PMessage message = (P2PMessage) connection.waitForNextCommunication();

                    if (message == null) break;

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
                            connection.send(new P2PResponse(Status.NOT_FOUND));
                            continue;
                        } catch (IOException e) {
                            throw new RuntimeException("Trouble reading file", e);
                        }
                        connection.send(new P2PResponse(Status.SUCCESS, rfcFileAsBytes));
                        if (isTestingScenario) {
                            // Signal to the Client to send a LeaveMessage
                            Peer.stopSignal.countDown();
                            return;
                        }
                    } else {
                        throw new ProtocolException.UnexpectedMessageException(message);
                    }
                }
            }, "PeerServerConnectionHandler").start();
        }
    }

}
