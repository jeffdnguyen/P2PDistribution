package edu.ncsu.NetworkingProject;

/**
 * Runs RFCPeerClient and RFCPeerServer in separate threads
 */
public class Peer {

    public static void start(String regServerIP, int portNumber, boolean isTestingScenario) {
        RFCIndex rfcIndex = new RFCIndex();
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
