package edu.ncsu.NetworkingProject.protocol.messages;

import edu.ncsu.NetworkingProject.Utils;
import edu.ncsu.NetworkingProject.protocol.P2PHeader;
import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.ProtocolException;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Object that represents Register request message
 *
 * @author jnguyen8
 */
public class RegisterMessage extends P2PMessage {

    // The Register request message should look like the following:
    //
    // Register PORT 111111 P2P-DI/1.0
    // Host: somehost.csc.ncsu.edu
    // Cookie: 12390 (OPTIONAL HEADER)

    /**
     * Port number of the RFC server that the peer is listening to;
     */
    private final int portNumber;
    private String host = null;

    /**
     * Cookie if this is not the first time registering
     */
    private int       cookie = -1;

    /**
     * Construct the request message
     *
     * @param argument
     *            the argument(s) pertaining to the request method
     * @param headers
     *            list of headers associated with the request method
     */
    public RegisterMessage (String argument, List<P2PHeader> headers) {
        if ( argument.isEmpty() )
            throw new ProtocolException.MissingArgumentException();

        // Grab port number from argument: "PORT 111111"
        portNumber = Integer.parseInt( argument.split( " " )[1] );

        // Grab cookie from request if he has registered already in the past
        try {
            this.cookie = Utils.getCookieFromHeaders(headers);
        } catch (NoSuchElementException e) {
            // Leave it as -1
        }
        try {
            this.host = Utils.getHostFromHeaders(headers);
        } catch (NoSuchElementException e) {
            // Leave blank to allow construction without a hostname
        }

    }

    /**
     * Get the port number argument of this request
     *
     * @return the port number
     */
    public int getPortNumber () {
        return this.portNumber;
    }

    /**
     * Get the cookie of this request
     *
     * @return the cookie
     */
    public int getCookie () {
        return this.cookie;
    }

    public String getHost() {
        return host;
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
    protected void addHeaders ( final LinkedList<P2PHeader> headers ) {
        headers.add( new P2PHeader( "Cookie", String.valueOf( cookie ) ) );
    }

}
