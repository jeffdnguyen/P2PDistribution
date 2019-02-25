package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PCommunication;
import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PResponse;
import edu.ncsu.NetworkingProject.protocol.ProtocolException.UnexpectedMessageException;
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
    private Connection conn;

    public RFCPeerClient (int portNumber, RFCIndex index) {
        this.portNumber = portNumber;
        this.index = index;
    }

    @Override public void run () {
        openNewConnection(RegServer.REGSERVER_PORT);
        registerWithRegServer();
        openNewConnection(RegServer.REGSERVER_PORT);
        LinkedList<PeerList> peerList = getPeerList();
        for(PeerList peer : peerList) {
            openNewConnection(peer.getPortNumber());
            RFCIndex otherIndex = getRFCIndex();
            downloadRFCs(otherIndex);
        }
        openNewConnection(RegServer.REGSERVER_PORT);
        leaveRegServer();
    }

    /**
     * Connects to the peer list.
     * Put in a method since the client has to open separate, new connections
     * for joining, querying, downloading, and exiting
     */
    private void openNewConnection (int remotePort) {
        try {
            Socket socket = new Socket(
                    InetAddress.getLocalHost(),
                    remotePort
            );
            this.conn = new Connection(socket);
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
    private void registerWithRegServer () {
        RegisterMessage message = new RegisterMessage(
                "PORT " + portNumber,
                new ArrayList<>()
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse) {
            P2PResponse registerResponse = (P2PResponse) response;
            P2PHeader cookieHeader = registerResponse.getHeaders()
                    .stream()
                    .filter(header -> header.name.equals("Cookie"))
                    .findFirst()
                    .orElseThrow();
            this.cookie = Integer.parseInt(cookieHeader.value);
        } else {
            throw new UnexpectedMessageException(response);
        }
        System.out.println(portNumber + ": Registered with RegServer");
    }

    /**
     * Ask the RegServer for a list of all peers
     * @return the LinkedList of peers
     */
    private LinkedList<PeerList> getPeerList() {
        PQueryMessage message = new PQueryMessage(
                "activePeerList",
                new ArrayList<>( List.of(new P2PHeader("Cookie", Integer.toString(this.cookie))) )
        );
        conn.send(message);
        P2PCommunication response = conn.waitForNextCommunication();
        if ( response instanceof P2PResponse ) {
            P2PResponse pQueryResponse = (P2PResponse) response;
            return Utils.byteArrayToObject(pQueryResponse.toByteArray());
        } else {
            throw new UnexpectedMessageException(response);
        }
    }

    private void downloadRFCs (RFCIndex otherIndex) {
        // TODO
    }

    private RFCIndex getRFCIndex () {
        // TODO
        synchronized (index) {
            System.out.println(portNumber + ": Got the RFCIndex");
        }
        return null;
    }

    private void leaveRegServer () {
        // TODO
    }
}
