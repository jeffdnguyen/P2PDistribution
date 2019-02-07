package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;
import java.util.List;

public class RFCQueryMessage extends P2PMessage {

    @SuppressWarnings("unused")
    RFCQueryMessage(String argument, List<P2PHeader> headers, byte[] data) {  }

    public RFCQueryMessage() { }

    @Override
    protected String getMethodArgument() {
        return null;
    }

    @Override
    protected void addHeaders(LinkedList<P2PHeader> headers) { }

    @Override
    protected byte[] getMessageData() {
        return null;
    }

}
