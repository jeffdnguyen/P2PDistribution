package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;
import java.util.List;

public class PQueryMessage extends P2PMessage {

    // The PQuery request message should look like the following:
    //
    // PQuery activePeerList P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu

    /**
     * Port number of the RFC server that the peer is listening to;
     */
    private final String peerListName;

    /**
     * Construct the request message
     * 
     * @param argument
     *            the argument(s) pertaining to the request method
     * @param headers
     *            list of headers associated with the request method
     * @param data
     *            any data the request method may hold
     */
    public PQueryMessage ( String argument, List<P2PHeader> headers, byte[] data ) {
        if ( argument.isEmpty() )
            throw new ProtocolException.MissingArgumentException();

        // Grab name of peerList
        peerListName = argument;
    }
    @Override
    protected String getMethodArgument () {
        return peerListName;
    }

    @Override
    protected void addHeaders ( LinkedList<P2PHeader> headers ) {
    }

    @Override
    protected byte[] getMessageData () {
        return null;
    }

}
