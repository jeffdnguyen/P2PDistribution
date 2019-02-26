package edu.ncsu.NetworkingProject;

import java.io.File;

/**
 * Runs RFCPeerClient and RFCPeerServer in separate threads
 */
public class Peer {

    private static RFCIndex rfcIndex = new RFCIndex();

    private static int portNumber;
    private static String regServerIP;

    public static void start(String regServerIP, int portNumber, boolean isTestingScenario) {
        RFCPeerClient client = new RFCPeerClient(regServerIP, portNumber, rfcIndex, isTestingScenario);
        Thread clientThread = new Thread(client);
        clientThread.setName("RFC client thread " + portNumber);
        clientThread.start();

        RFCPeerServer server = new RFCPeerServer(portNumber, rfcIndex);
        Thread serverThread = new Thread(server);
        serverThread.setName("RFC server thread " + portNumber);
        serverThread.start();
    }

}
