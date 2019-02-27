package edu.ncsu.NetworkingProject;

import java.util.concurrent.CountDownLatch;

/**
 * Runs RFCPeerClient and RFCPeerServer in separate threads
 */
public class Peer {

    // When this hits 0, it signals the Server to stop and the Client to send a LeaveMessage
    public static final CountDownLatch stopSignal = new CountDownLatch(1);

    public static void start(String regServerIP, int portNumber, boolean isTestingScenario, boolean isBestCase) {
        RFCIndex rfcIndex = new RFCIndex();
        RFCPeerClient client = new RFCPeerClient(regServerIP, portNumber, rfcIndex, isTestingScenario, isBestCase);
        Thread clientThread = new Thread(client);
        clientThread.setName("RFC client thread " + portNumber);
        clientThread.start();

        RFCPeerServer server = new RFCPeerServer(portNumber, rfcIndex, isTestingScenario);
        Thread serverThread = new Thread(server);
        serverThread.setName("RFC server thread " + portNumber);
        serverThread.start();
    }

}
