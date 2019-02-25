package edu.ncsu.NetworkingProject;

import java.io.File;

/**
 * Runs RFCPeerClient and RFCPeerServer in separate threads
 */
public class Peer {

    private static RFCIndex rfcIndex = new RFCIndex();

    private static int portNumber;

    public static void main(String[] args) {
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Peers must have their port number in args[0]");
            throw e;
        }
        startRFCPeerServer();
        startRFCPeerClient();
    }

    private static void startRFCPeerClient () {
        RFCPeerClient client = new RFCPeerClient(portNumber, rfcIndex);
        Thread clientThread = new Thread(client);
        clientThread.setName("RFC client thread " + portNumber);
        clientThread.start();
    }

    private static void startRFCPeerServer () {
        RFCPeerServer server = new RFCPeerServer(portNumber, rfcIndex);
        Thread serverThread = new Thread(server);
        serverThread.setName("RFC server thread " + portNumber);
        serverThread.start();
    }

}
