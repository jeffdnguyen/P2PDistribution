package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PCommunication;
import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PResponse;
import edu.ncsu.NetworkingProject.protocol.ProtocolException.UnexpectedMessageException;
import edu.ncsu.NetworkingProject.protocol.Status;
import edu.ncsu.NetworkingProject.protocol.messages.LeaveMessage;
import edu.ncsu.NetworkingProject.protocol.messages.PQueryMessage;
import edu.ncsu.NetworkingProject.protocol.messages.RegisterMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The part of the Peer that registers with the RegServer,
 * queries other peers, and downloads RFCs
 */
public class RFCPeerClient implements Runnable {
    private int portNumber;
    private int cookie;
    private final RFCIndex index;

    public RFCPeerClient (int portNumber, RFCIndex index) {
        this.portNumber = portNumber;
        this.index = index;
    }

    @Override public void run () {
        Connection conn = openNewConnection(RegServer.REGSERVER_PORT);
        registerWithRegServer(conn);
        conn.close();

        conn = openNewConnection(RegServer.REGSERVER_PORT);
        LinkedList<PeerList> peerList = getPeerList(conn);
        conn.close();
        while (peerList.size() == 0) {
            conn = openNewConnection(RegServer.REGSERVER_PORT);
            keepAlive(conn);
            conn.close();

            try { Thread.sleep(1000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            conn = openNewConnection(RegServer.REGSERVER_PORT);
            peerList = getPeerList(conn);
            conn.close();
        }

        for(PeerList peer : peerList) {
            conn = openNewConnection(peer.getPortNumber());
            RFCIndex otherIndex = getRFCIndex(conn);
            conn.close();

            conn = openNewConnection(peer.getPortNumber());
            downloadRFCs(conn, otherIndex);
            conn.close();
        }
        conn = openNewConnection(RegServer.REGSERVER_PORT);
        leaveRegServer(conn);
        conn.close();
    }

    private void keepAlive (Connection conn) {

    }

    /**
     * Connects to the peer list.
     * Put in a method since the client has to open separate, new connections
     * for joining, querying, downloading, and exiting
     */
    private Connection openNewConnection (int remotePort) {
        try {
            Socket socket = new Socket(
                    InetAddress.getLocalHost(),
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
    private void registerWithRegServer (Connection conn) {
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
     * Ask the RegServer for a list of all peers
     * @return the LinkedList of peers
     */
    private LinkedList<PeerList> getPeerList(Connection conn) {
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

    private void downloadRFCs (Connection conn, RFCIndex otherIndex) {
        // TODO
    }

    private RFCIndex getRFCIndex (Connection conn) {
        // TODO
        synchronized (index) {
            System.out.println(portNumber + ": Got the RFCIndex");
        }
        return null;
    }

    private void leaveRegServer (Connection conn) {
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
