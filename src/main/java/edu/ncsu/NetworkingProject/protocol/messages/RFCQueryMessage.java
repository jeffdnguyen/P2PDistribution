package edu.ncsu.NetworkingProject.protocol.messages;

import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PMessage;

import java.util.LinkedList;
import java.util.List;

public class RFCQueryMessage extends P2PMessage {

    // The RFCQueryMessage should look like the following:
    //
    // RFCQuery RFCPeerServer P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    public RFCQueryMessage(String argument, List<P2PHeader> headers) {  }

    public RFCQueryMessage() { }

    @Override
    protected String getMethodArgument() {
        return null;
    }

    @Override
    protected void addHeaders(LinkedList<P2PHeader> headers) { }

}
