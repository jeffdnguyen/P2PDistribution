package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;

import edu.ncsu.NetworkingProject.PeerList;

public class PQueryResponseMessage extends P2PMessage {

    // The PQuery response message should look like the following:
    //
    // PQueryResponse Status:100 (Success) P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    //
    // data
    
    private int    statusCode = 0;
    private String phrase     = "";
    LinkedList<PeerList> activePeers;
    
    public PQueryResponseMessage(int statusCode, String phrase, LinkedList<PeerList> activePeers) {
        this.statusCode = statusCode;
        this.phrase = phrase;
        this.activePeers = activePeers;
    }
    @Override
    protected String getMethodArgument () {
        return "Status:" + statusCode + " " + phrase;
    }

    @Override
    protected void addHeaders ( LinkedList<P2PHeader> headers ) {
    }

    @Override
    protected byte[] getMessageData () {
        return activePeers.toString().getBytes();
    }

}
