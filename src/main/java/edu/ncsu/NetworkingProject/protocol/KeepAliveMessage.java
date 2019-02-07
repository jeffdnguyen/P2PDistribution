package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;
import java.util.List;

public class KeepAliveMessage extends P2PMessage {

    // The KeepAlive request message should look like the following:
    //
    // KeepAlive RegServer P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    // Cookie: 12390
    
    private String serverName = "";
    
    /**
     * Cookie if this is not the first time registering
     */
    private int cookie = -1;
    
    public KeepAliveMessage ( String argument, List<P2PHeader> headers, byte[] data ) {
        if ( argument.isEmpty() )
            throw new ProtocolException.MissingArgumentException();

        // Grab server name from argument
        serverName = argument;
        
        // Grab cookie from request
        if ( headers.size() > 1 )
            cookie = Integer.parseInt( headers.get( 1 ).value );
    }
    
    /**
     * Get the cookie of this request
     * 
     * @return the cookie
     */
    public int getCookie () {
        return this.cookie;
    }
    
    /**
     * Get the server name of this request
     * 
     * @return the server name
     */
    public String getServerName () {
        return this.serverName;
    }
    
    @Override
    protected String getMethodArgument () {
        return serverName;
    }

    @Override
    protected void addHeaders ( LinkedList<P2PHeader> headers ) {
    }

    @Override
    protected byte[] getMessageData () {
        return null;
    }

}
