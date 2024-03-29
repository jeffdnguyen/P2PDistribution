package edu.ncsu.NetworkingProject.protocol.messages;

import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.ProtocolException;

import java.util.LinkedList;
import java.util.List;

public class GetRFCMessage extends P2PMessage {

    private int RFCID;

    // The GetRFCMessage should look like the following:
    //
    // GetRFC RFC 1234 P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    public GetRFCMessage(String argument, List<P2PHeader> headers) {
        if (argument.isEmpty()) throw new ProtocolException.MissingArgumentException();

        RFCID = Integer.parseInt(argument.split(" ")[1]);
    }

    public GetRFCMessage(int RFCID) {
        this.RFCID = RFCID;
    }

    public int getRFCID() {
        return RFCID;
    }

    @Override
    protected String getMethodArgument() {
        return "RFC " + this.RFCID;
    }

    @Override
    protected void addHeaders(LinkedList<P2PHeader> headers) { }

}
