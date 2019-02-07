package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;

public class LeaveResponseMessage extends P2PMessage {

    // The Register response message should look like the following:
    //
    // RegisterResponse Status:100 (Success) P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    // Cookie: 14234

    private int    statusCode = 0;
    private String phrase     = "";
    private int    cookie     = -1;

    public LeaveResponseMessage ( int statusCode, String phrase, int cookie ) {
        this.statusCode = statusCode;
        this.phrase = phrase;
        this.cookie = cookie;
    }

    @Override
    protected String getMethodArgument () {
        return "Status:" + statusCode + " " + phrase;
    }

    @Override
    protected void addHeaders ( LinkedList<P2PHeader> headers ) {
        headers.add( new P2PHeader( "Cookie", String.valueOf( cookie ) ) );
    }

    /**
     * Register response has no data, always return null
     */
    @Override
    protected byte[] getMessageData () {
        return null;
    }

}
