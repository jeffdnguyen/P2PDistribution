package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.*;
import edu.ncsu.NetworkingProject.protocol.messages.KeepAliveMessage;
import edu.ncsu.NetworkingProject.protocol.messages.LeaveMessage;
import edu.ncsu.NetworkingProject.protocol.messages.PQueryMessage;
import edu.ncsu.NetworkingProject.protocol.messages.RegisterMessage;

import java.net.Socket;

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
     * List of peers that are registered with the RegServer
     */
    static final PeerList peerList = PeerList.getINSTANCE();

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
        P2PMessage message = (P2PMessage) connection.waitForNextCommunication();
        if ( message == null ) {
            // The peer closed their end of the connection
            connection.close();
            return;
        }

        if ( message instanceof RegisterMessage ) {
            RegisterMessage request = (RegisterMessage) message;

            int portNumber = request.getPortNumber();
            int currentCookie = request.getCookie();

            // If cookie is -1, then this is a new peer
            if ( currentCookie == -1 ) {
                PeerListEntry newPeer = new PeerListEntry();

                newPeer.setHostname( P2PCommunication.getHostname() );
                newPeer.setActive( true );
                newPeer.setTTL( 7200 );
                newPeer.setPortNumber( portNumber );
                newPeer.setNumberOfTimesActive( 1 );

                synchronized ( peerList ) {
                    currentCookie = peerList.getCopy().size();
                    newPeer.setCookie(currentCookie);
                    peerList.add( newPeer );
                    System.out.println( "Registered a new peer with port " + portNumber + " and cookie " + currentCookie );
                }
            }
            else {
                // Find the existing peer and update it
                synchronized ( peerList ) {
                    int finalCurrentCookie = currentCookie;
                    peerList.forEachActivePeer(peer -> {
                        if ( peer.getCookie() == finalCurrentCookie) {
                            peer.setActive( true );
                            peer.setTTL( 7200 );
                            peer.setNumberOfTimesActive( peer.getNumberOfTimesActive() + 1 );
                            return false;
                        }
                        return true;
                    });
                }
            }

            // Send response back to the peer
            P2PResponse response = new P2PResponse( Status.SUCCESS, currentCookie );
            connection.send( response );
        }
        else if ( message instanceof LeaveMessage ) {
            LeaveMessage request = (LeaveMessage) message;
            synchronized ( peerList ) {
                peerList.forEachActivePeer(peer -> {
                    if ( peer.getCookie() == request.getCookie() ) {
                        peer.setTTL( 0 );
                        return false;
                    }
                    return true;
                });
            }

            P2PResponse response = new P2PResponse( Status.SUCCESS, request.getCookie() );
            connection.send( response );
            System.out.println( "LeaveMessage received from peer " + request.getCookie() );
        }
        else if ( message instanceof PQueryMessage ) {
            P2PResponse response;
            synchronized (peerList) {
                peerList.cleanList();
                response = new P2PResponse( Status.SUCCESS, Utils.objectToByteArray( peerList.getCopy() ) );
            }
            connection.send( response );
            System.out.println( "PQueryMessage received" );
        }
        else if ( message instanceof KeepAliveMessage ) {
            KeepAliveMessage request = (KeepAliveMessage) message;
            synchronized ( peerList ) {
                peerList.forEachActivePeer(peer -> {
                    if ( peer.getCookie() == request.getCookie() ) {
                        peer.setTTL( 7200 );
                        return false;
                    }
                    return true;
                });
            }

            P2PResponse response = new P2PResponse( Status.SUCCESS, request.getCookie() );
            connection.send( response );
            System.out.println( "KeepAliveMessage received from peer " + request.getCookie() );
        }
        else {
            throw new ProtocolException.NoSuchMessageType( message.getClass().toString() );
        }
        // Close TCP connection
        connection.close();
    }
}
