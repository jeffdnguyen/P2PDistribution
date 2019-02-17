package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.Socket;

/**
 * The part of the Peer that registers with the RegServer,
 * queries other peers, and downloads RFCs
 */
public class RFCPeerClient implements Runnable {
    private int portNumber;
    private RFCIndex index;

    public RFCPeerClient (int portNumber, RFCIndex index) {
        this.portNumber = portNumber;
        this.index = index;
    }

    @Override public void run () {
        registerWithRegServer();
        System.out.println(portNumber + ": Getting peer list, RFC index, and downloading RFCs...");
        // LinkedList<PeerListRecord> peerList = getPeerList();
        // for(PeerListRecord : peerList) {
        //      RFCIndex otherIndex = getRFCIndex();
        //      downloadRFCs();
        // }

    }

    private void downloadRFCs () {

    }

    private RFCIndex getRFCIndex () {
        return null;
    }

    private void registerWithRegServer () {
//        try {
//            Socket socket = new Socket("localhost", portNumber);
//            Connection conn = new Connection(socket);
//            RegisterMessage message = new RegisterMessage();
//        }
//        catch ( final IOException e ) {
//            e.printStackTrace();
//        }
        System.out.println(portNumber + ": Registering with RegServer...");
    }


}
