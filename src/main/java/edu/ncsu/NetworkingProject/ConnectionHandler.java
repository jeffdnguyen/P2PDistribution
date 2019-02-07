package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;

import edu.ncsu.NetworkingProject.protocol.LeaveMessage;
import edu.ncsu.NetworkingProject.protocol.LeaveResponseMessage;
import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.RegisterMessage;
import edu.ncsu.NetworkingProject.protocol.RegisterResponseMessage;

class ConnectionHandler implements Runnable {

    /**
     * Socket peer is connected to
     */
    Socket               connectionSocket;

    /**
     * Thread that this connection is running on
     */
    Thread               clientThread;

    /**
     * Unique cookie of the peer currently connected
     */
    int                  cookie   = 0;

    /**
     * List of peers that are registered with the RegServer
     */
    LinkedList<PeerList> peerList = new LinkedList<PeerList>();

    /**
     * Creates new thread to handle new connection at the passed in socket
     * 
     * @param socket
     *            the socket with the new connection
     */
    ConnectionHandler ( final Socket socket ) {
        connectionSocket = socket;
        clientThread = new Thread( this );
        clientThread.start();
    }

    /**
     * Handles sequence of messages between the peer and RegServer
     */
    @Override
    public void run () {
        Connection connection = new Connection( connectionSocket );

        // Grab the incoming message
        P2PMessage message = connection.waitForNextMessage();

        if ( message instanceof RegisterMessage ) {
            RegisterMessage request = (RegisterMessage) message;

            int portNumber = request.getPortNumber();
            int currentCookie = request.getCookie();
            
            // If cookie is -1, then this is a new peer
            if ( currentCookie == -1 ) {
                PeerList newPeer = new PeerList();
                cookie += 1;
                currentCookie = cookie;

                newPeer.setHostname( connectionSocket.getInetAddress().getHostName() );
                newPeer.setCookie( currentCookie );
                newPeer.setActive( true );
                newPeer.setTTL( 7200 );
                newPeer.setPortNumber( portNumber );
                newPeer.setNumberOfTimesActive( 1 );
                newPeer.setLastActive( LocalDateTime.now() );

                peerList.add( newPeer );
            }
            else {
                // Find the existing peer and update it
                for ( PeerList peer : peerList ) {
                    if ( peer.getCookie() == currentCookie ) {
                        peer.setActive( true );
                        peer.setTTL( 7200 );
                        peer.setNumberOfTimesActive( peer.getNumberOfTimesActive() + 1 );
                        break;
                    }
                }
            }

            // Send response back to the peer
            RegisterResponseMessage response = new RegisterResponseMessage( 100, "(Success)", currentCookie );
            connection.send( response );
        }
        else if ( message instanceof LeaveMessage ) {
            LeaveMessage request = (LeaveMessage) message;
            for ( PeerList peer : peerList ) {
                if ( peer.getCookie() == request.getCookie() ) {
                    peer.setTTL( 0 );
                    break;
                }
            }

            LeaveResponseMessage response = new LeaveResponseMessage( 100, "(Success)", request.getCookie() );
            connection.send( response );

        }
        
        // Close TCP connection
        try {
            connectionSocket.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
