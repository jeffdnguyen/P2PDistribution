package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PCommunication;
import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PResponse;
import edu.ncsu.NetworkingProject.protocol.ProtocolException.UnexpectedMessageException;
import edu.ncsu.NetworkingProject.protocol.Status;
import edu.ncsu.NetworkingProject.protocol.messages.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * The part of the Peer that registers with the RegServer,
 * queries other peers, and downloads RFCs
 */
public class RFCPeerClient implements Runnable {
    private final String regServerIP;
    private final boolean isTestingScenario;
    private int portNumber;
    private int cookie;
    private final RFCIndex index;
    private final File rfcFolder;
    private ArrayList<Long> downloadTimes = new ArrayList<>();
    public static final int RFCS_TO_DOWNLOAD = 60;
    public static final int KEEP_ALIVE_TIMER = 60000;

    public RFCPeerClient(String regServerIP, int portNumber, RFCIndex index, boolean isTestingScenario) {
        this.regServerIP = regServerIP;
        this.portNumber = portNumber;
        this.index = index;
        this.rfcFolder = new File("./rfcs/" + portNumber + "/");
        this.rfcFolder.mkdir();
        this.isTestingScenario = isTestingScenario;
    }

    @Override public void run () {
        registerWithRegServer(regServerIP, RegServer.REGSERVER_PORT);

        if (isTestingScenario) {
            runTestingScenario();
        } else {
            runTask1or2();
        }
    }

    private void runTestingScenario () {
        // "There are two peers, A and B, initialized such that B has two RFCs and A has none"
        int numFiles = rfcFolder.listFiles().length;
        if (numFiles == 0) {
            // This is peer A
            LinkedList<PeerListEntry> peerList = getPeerList(regServerIP, RegServer.REGSERVER_PORT);

            // Find peerB in the PeerList and connect
            PeerListEntry peerB = peerList.stream()
                    .filter(entry -> entry.getCookie() != this.cookie)
                    .findFirst().orElseThrow();
            getRFCIndex(peerB.getHostname(), peerB.getPortNumber());

            // Download the first RFC
            downloadRFC(peerB.getHostname(), peerB.getPortNumber(), index.index.get(0));

            // Wait for the other peer to leave the server
            try { Thread.sleep(1000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Re-check the PeerList
            peerList = getPeerList(regServerIP, RegServer.REGSERVER_PORT);

            peerList.stream()
                    .filter(entry -> entry.getCookie() != this.cookie)
                    .findFirst()
                    .ifPresent(entry -> { throw new RuntimeException("Peer B should have unregistered itself"); });
            leaveRegServer(regServerIP, RegServer.REGSERVER_PORT);

        } else if (numFiles == 2) {
            // This is peer B, so just send KeepAlives until the server indicates to leave
            Timer keepAliveTimer = new Timer( true );
            keepAliveTimer.schedule( new KeepAliveThread(this.cookie), KEEP_ALIVE_TIMER );
            try {
                // Wait until the Server receives a single GetRFCMessage and signals the Client to leave.
                Peer.stopSignal.await();
                keepAliveTimer.cancel();
                leaveRegServer(regServerIP, RegServer.REGSERVER_PORT);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void runTask1or2 () {
        long startTime = System.currentTimeMillis();

        new Timer( true ).schedule( new KeepAliveThread(this.cookie), KEEP_ALIVE_TIMER );

        while(rfcFolder.listFiles().length < RFCS_TO_DOWNLOAD) {
            LinkedList<PeerListEntry> peerList = getPeerList(regServerIP, RegServer.REGSERVER_PORT);

            for(PeerListEntry peer : peerList) {
                getRFCIndex(peer.getHostname(), peer.getPortNumber());
                if (index.index.size() == RFCS_TO_DOWNLOAD) {
                    // This peer now holds the full RFCIndex
                    break;
                }
            }
            // Copy the current RFCIndex so we don't lock it while downloading
            LinkedList<RFCIndexEntry> indexToLoopOver;
            synchronized (index) {
                indexToLoopOver = new LinkedList<>(index.index);
            }
            // Uncomment this line to simulate the "best case" (all peers evenly download from all peers)
            // Collections.shuffle(indexToLoopOver);
            // Or uncomment this line to simulate the "worst case" (all peers crowd one peer at a time)
            indexToLoopOver.sort(Comparator.comparingInt(RFCIndexEntry::getNumber));
            for (RFCIndexEntry entry : indexToLoopOver) {
                if (!entry.getHostname().equals(P2PCommunication.getHostname()) || entry.getPort() != portNumber) {
                    downloadRFC(entry.getHostname(), entry.getPort(), entry);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long cumulativeTime = endTime - startTime;
        System.out.println(portNumber + ": Times for each download (ms):\n" + downloadTimes.toString());
        System.out.println(portNumber + ": Total time:\n" + cumulativeTime + " ms");
    }

    /**
     * Connects to the peer list.
     * Put in a method since the client has to open separate, new connections
     * for joining, querying, downloading, and exiting
     */
    private Connection openNewConnection(String ip, int remotePort) {
        try {
            Socket socket = new Socket(
                    InetAddress.getByName(ip),
                    remotePort
            );
            return new Connection(socket);
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException("Could not create a connection");
        }
    }

    /**
     * Send a RegisterMessage to the RegServer with the port number.
     * The server replies with a cookie, which we save for all future messages.
     */
    private void registerWithRegServer(String ip, int port) {
        Connection conn = openNewConnection(ip, port);
        RegisterMessage message = new RegisterMessage(
                "PORT " + portNumber,
                new ArrayList<>()
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse) {
            P2PResponse registerResponse = (P2PResponse) response;
            this.cookie = Utils.getCookieFromHeaders(registerResponse.getHeaders());
        } else {
            throw new UnexpectedMessageException(response);
        }
        System.out.println(portNumber + ": Registered with RegServer");
        conn.close();
    }

    /**
     * Make sure the RegServer doesn't mark the current Peer as inactive
     */
    private class KeepAliveThread extends TimerTask {
        private int cookie;

        public KeepAliveThread(int cookie) {
            this.cookie = cookie;
        }

        @Override
        public void run() {
            Connection conn = openNewConnection(regServerIP, RegServer.REGSERVER_PORT);
            KeepAliveMessage message = new KeepAliveMessage(
                    "RegServer",
                    new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
            );
            conn.send(message);
            P2PCommunication response = conn.waitForNextCommunication();
            if ( response instanceof P2PResponse) {
                P2PResponse keepAliveResponse = (P2PResponse) response;
                if (!keepAliveResponse.getStatus().equals(Status.SUCCESS)) {
                    System.out.println("Failed to send KeepAlive: " + keepAliveResponse);
                }
            } else {
                conn.close();
                throw new UnexpectedMessageException(response);
            }
            conn.close();
        }
    }

    /**
     * Ask the RegServer for a list of all peers
     * @return the LinkedList of peers
     */
    private LinkedList<PeerListEntry> getPeerList(String ip, int port) {
        Connection conn = openNewConnection(ip, port);
        PQueryMessage message = new PQueryMessage(
                "activePeerList",
                new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse ) {
            P2PResponse pQueryResponse = (P2PResponse) response;
            conn.close();
            return Utils.byteArrayToObject(pQueryResponse.getData());
        } else {
            conn.close();
            throw new UnexpectedMessageException(response);
        }

    }

    private void getRFCIndex(String ip, int port) {
        Connection conn = openNewConnection(ip, port);
        RFCQueryMessage message = new RFCQueryMessage(
                "RFCPeerServer",
                new ArrayList<>()
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse ) {
            P2PResponse rfcIndexResponse = (P2PResponse) response;
            RFCIndex otherIndex = Utils.byteArrayToObject(rfcIndexResponse.getData());
            synchronized (index) {
                index.mergeWith(otherIndex);
                System.out.println(portNumber + ": Got an RFCIndex of size " + otherIndex.index.size());
            }
        } else {
            conn.close();
            throw new UnexpectedMessageException(response);
        }
        conn.close();
    }

    private void downloadRFC(String ip, int port, RFCIndexEntry entry) {
        long startTime = System.currentTimeMillis();
        Connection conn = openNewConnection(ip, port);
        GetRFCMessage message = new GetRFCMessage(
                "RFC " + entry.getNumber(),
                new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse) {
            P2PResponse rfcResponse = (P2PResponse) response;
            if (!rfcResponse.getStatus().equals(Status.SUCCESS)) {
                System.out.println("Failed to get RFC " + entry.getNumber());
            } else {
                try {
                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(rfcFolder.getPath() + "/rfc" + entry.getNumber() + ".txt")
                    );
                    out.write(rfcResponse.getData());
                    out.close();
                    System.out.println("Successfully downloaded RFC " + entry.getNumber());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            conn.close();
            throw new UnexpectedMessageException(response);
        }
        long endTime = System.currentTimeMillis();
        conn.close();
        downloadTimes.add(endTime - startTime);
    }

    private void leaveRegServer(String ip, int port) {
        Connection conn = openNewConnection(ip, port);
        LeaveMessage message = new LeaveMessage(
                "RegServer",
                new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse) {
            P2PResponse leaveResponse = (P2PResponse) response;
            if (!leaveResponse.getStatus().equals(Status.SUCCESS)) {
                System.out.println("Failed to leave RegServer. Retrying...");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                conn.close();
                leaveRegServer(ip, port);
            }
        } else {
            conn.close();
            throw new UnexpectedMessageException(response);
        }
        System.out.println(portNumber + ": Left RegServer");
        conn.close();
    }
}
