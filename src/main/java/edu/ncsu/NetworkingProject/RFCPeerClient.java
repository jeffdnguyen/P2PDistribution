package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.*;
import edu.ncsu.NetworkingProject.protocol.ProtocolException.UnexpectedMessageException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

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
                    remotePort,
                    InetAddress.getLocalHost(),
                    portNumber
            );
            socket.setKeepAlive(true);
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
                "Register PORT " + portNumber,
                new ArrayList<>(),
                new byte[0]
        );
        conn.send(message);
        P2PMessage response = conn.waitForNextMessage();
        if ( response instanceof RegisterResponseMessage ) {
            RegisterResponseMessage registerResponse = (RegisterResponseMessage) response;
            this.cookie = registerResponse.getCookie();
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
                "PQuery activePeerList",
                Collections.singletonList(new P2PHeader("Cookie", Integer.toString(this.cookie))),
                new byte[0]
        );
        conn.send(message);
        P2PMessage response = conn.waitForNextMessage();
        // TODO: get a P2PResponse, convert the data into a list of peers, and return it
        if ( response instanceof PQueryResponseMessage ) {
            PQueryResponseMessage pQueryResponse = (PQueryResponseMessage) response;
            return new LinkedList<PeerList>();
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
