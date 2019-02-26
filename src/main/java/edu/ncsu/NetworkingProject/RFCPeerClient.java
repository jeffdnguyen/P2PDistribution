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
    private int portNumber;
    private int cookie;
    private final RFCIndex index;
    private final File rfcFolder;
    public static final int RFCS_TO_DOWNLOAD = 60;
    public static final int KEEP_ALIVE_TIMER = 60000;

    public RFCPeerClient(String regServerIP, int portNumber, RFCIndex index) {
        this.regServerIP = regServerIP;
        this.portNumber = portNumber;
        this.index = index;
        this.rfcFolder = new File("./rfcs/" + portNumber + "/");
        this.rfcFolder.mkdir();
    }

    @Override public void run () {
        Connection conn = openNewConnection(regServerIP, RegServer.REGSERVER_PORT);
        registerWithRegServer(conn);
        conn.close();

        Timer timer = new Timer( true );
        timer.schedule( new KeepAliveThread(this.cookie), KEEP_ALIVE_TIMER );

        while(rfcFolder.listFiles().length < RFCS_TO_DOWNLOAD) {
            conn = openNewConnection(regServerIP, RegServer.REGSERVER_PORT);
            LinkedList<PeerListEntry> peerList = getPeerList(conn);
            conn.close();

            for(PeerListEntry peer : peerList) {
                conn = openNewConnection(peer.getHostname(), peer.getPortNumber());
                getRFCIndex(conn);
                conn.close();
            }

            synchronized (index) {
                for (RFCIndexEntry entry : index.index) {
                    if (!entry.getHostname().equals(P2PCommunication.getHostname()) || entry.getPort() != portNumber) {
                        conn = openNewConnection(entry.getHostname(), entry.getPort());
                        downloadRFC(conn, entry);
                        conn.close();
                    }
                }
            }
        }

//        conn = openNewConnection(regServerIP, RegServer.REGSERVER_PORT);
//        leaveRegServer(conn);
//        conn.close();
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
    private void registerWithRegServer(Connection conn) {
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
                P2PResponse leaveResponse = (P2PResponse) response;
                if (!leaveResponse.getStatus().equals(Status.SUCCESS)) {
                    System.out.println("Failed to send KeepAlive: " + leaveResponse);
                }
            } else {
                throw new UnexpectedMessageException(response);
            }
            conn.close();
        }
    }

    /**
     * Ask the RegServer for a list of all peers
     * @return the LinkedList of peers
     */
    private LinkedList<PeerListEntry> getPeerList(Connection conn) {
        PQueryMessage message = new PQueryMessage(
                "activePeerList",
                new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse ) {
            P2PResponse pQueryResponse = (P2PResponse) response;
            return Utils.byteArrayToObject(pQueryResponse.getData());
        } else {
            throw new UnexpectedMessageException(response);
        }
    }

    private void getRFCIndex(Connection conn) {
        RFCQueryMessage message = new RFCQueryMessage(
                "RFCPeerServer",
                new ArrayList<>()
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse ) {
            P2PResponse pQueryResponse = (P2PResponse) response;
            RFCIndex otherIndex = Utils.byteArrayToObject(pQueryResponse.getData());
            synchronized (index) {
                index.mergeWith(otherIndex);
                System.out.println(portNumber + ": Got an RFCIndex of size " + otherIndex.index.size());
            }
        } else {
            throw new UnexpectedMessageException(response);
        }
    }

    private void downloadRFC(Connection conn, RFCIndexEntry entry) {
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
            throw new UnexpectedMessageException(response);
        }
    }

    private void leaveRegServer(Connection conn) {
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
                leaveRegServer(conn);
            }
        } else {
            throw new UnexpectedMessageException(response);
        }
        System.out.println(portNumber + ": Left RegServer");
    }
}
