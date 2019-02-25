package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.P2PResponse;
import edu.ncsu.NetworkingProject.protocol.ProtocolException;
import edu.ncsu.NetworkingProject.protocol.Status;
import edu.ncsu.NetworkingProject.protocol.messages.KeepAliveMessage;
import edu.ncsu.NetworkingProject.protocol.messages.LeaveMessage;
import edu.ncsu.NetworkingProject.protocol.messages.PQueryMessage;
import edu.ncsu.NetworkingProject.protocol.messages.RegisterMessage;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;

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
     * TODO: This list is always empty since each thread creates a new peerList.
     * Should be created in RegServer, passed in, and put in synchronized blocks.
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
        P2PMessage message = (P2PMessage) connection.waitForNextCommunication();
        if (message == null) {
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
            P2PResponse response = new P2PResponse(Status.SUCCESS, currentCookie);
            connection.send( response );
            System.out.println("Registered a new peer with port " + portNumber + " and cookie " + cookie);
        }
        else if ( message instanceof LeaveMessage ) {
            LeaveMessage request = (LeaveMessage) message;
            for ( PeerList peer : peerList ) {
                if ( peer.getCookie() == request.getCookie() ) {
                    peer.setTTL( 0 );
                    break;
                }
            }

            P2PResponse response = new P2PResponse(Status.SUCCESS, request.getCookie());
            connection.send( response );
            System.out.println("LeaveMessage received from peer " + request.getCookie());
        }
        else if ( message instanceof PQueryMessage ) {
            LinkedList<PeerList> activePeers = new LinkedList<PeerList>();

            for ( PeerList peer : peerList ) {
                // Only add active peers to the list
                if (peer.isActive()) {
                    activePeers.add( peer );
                }
            }

            P2PResponse response = new P2PResponse(Status.SUCCESS, Utils.objectToByteArray(activePeers));
            connection.send( response );
            System.out.println("PQueryMessage received");
        }
        else if ( message instanceof KeepAliveMessage ) {
            KeepAliveMessage request = (KeepAliveMessage) message;
            for ( PeerList peer : peerList ) {
                if ( peer.getCookie() == request.getCookie() ) {
                    peer.setTTL( 7200 );
                    break;
                }
            }

            P2PResponse response = new P2PResponse(Status.SUCCESS, request.getCookie());
            connection.send( response );
            System.out.println("KeepAliveMessage received from peer " + request.getCookie());
        }
        else {
            throw new ProtocolException.NoSuchMessageType(message.getClass().toString());
        }
        // Close TCP connection
        connection.close();
    }
}
