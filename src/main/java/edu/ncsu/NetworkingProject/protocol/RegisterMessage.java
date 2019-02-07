package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;
import java.util.List;

/**
 * Object that represents Register request message
 * 
 * @author jnguyen8
 */
public class RegisterMessage extends P2PMessage {
    
    
    //The Register request message should look like the following:
    //
    //Register PORT 111111 P2P-DI/1.0
    //Host: somehost.csc.ncsu.edu
    //Cookie: 12390 (OPTIONAL HEADER)
    
    
    // Port number of the RFC server that the peer is listening to;
    private final int portNumber;
    
    // Cookie if this is not the first time registering
    private int cookie = -1;
    
    /**
     * Construct the request message
     * @param argument the argument(s) pertaining to the request method
     * @param headers list of headers associated with the request method
     * @param data any data the request method may hold
     */
    public RegisterMessage(String argument, List<P2PHeader> headers, byte[] data) {
        if (argument.isEmpty()) throw new ProtocolException.MissingArgumentException();
        
        portNumber = Integer.parseInt(argument.split(" ")[1]);
        
        // Grab cookie from peer if he has registered already in the past
        if (headers.size() > 1) cookie = Integer.parseInt(headers.get( 1 ).value);
    }
    
    /**
     * Get the port number argument of this request
     * @return the port number
     */
    public int getPortNumber() {
        return this.portNumber;
    }

    /**
     * Get the cookie of this request
     * @return the cookie
     */
    public int getCookie() {
        return this.cookie;
    }
    
    /**
     * Get the full method argument string
     */
    @Override
    protected String getMethodArgument () {
        return "PORT " + portNumber;
    }

    /**
     * Add additional headers to request method
     */
    @Override
    protected void addHeaders ( final LinkedList<P2PHeader> headers ) { }

    /**
     * Register request has no data, always return null
     */
    @Override
    protected byte[] getMessageData () {
        return null;
    }

}