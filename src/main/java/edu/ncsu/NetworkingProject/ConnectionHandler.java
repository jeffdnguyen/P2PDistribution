package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.RegisterMessage;

class ConnectionHandler implements Runnable {

    Socket               connectionSocket;
    Thread               clientThread;
    int                  cookie   = 0;

    LinkedList<PeerList> peerList = new LinkedList<PeerList>();

    ConnectionHandler ( final Socket socket ) {
        connectionSocket = socket;
        clientThread = new Thread( this );
        clientThread.start();
    }

    @Override
    public void run () {
        Connection connection = new Connection( connectionSocket );

        P2PMessage message = connection.waitForNextMessage();

        if ( message instanceof RegisterMessage ) {
            try {
                RegisterMessage recievedRegisterMessage = (RegisterMessage) message;

                int portNumber = recievedRegisterMessage.getPortNumber();

                PeerList newPeer = new PeerList();
                cookie += 1;

                newPeer.setHostname( connectionSocket.getInetAddress().getHostName() );
                newPeer.setCookie( cookie );
                newPeer.setActive( true );
                newPeer.setTTL( 7200 );
                newPeer.setPortNumber( portNumber );
                newPeer.setNumberOfTimesActive( 0 );
                newPeer.setLastActive( LocalDateTime.now() );

                peerList.add( newPeer );

                
                // Commented out code is attempt to add headers to the response message. Not sure how to do that??  
                // Passing in a whole LinkedList instead of an individual P2PHeader doesn't make any sense to me 
                // when looking at the toByteArray() method.
                
                /**
                 * LinkedList<P2PHeader> headerList = new LinkedList<P2PHeader>();
                 * P2PHeader portNumberHeader = new P2PHeader("PortNumber", "" + portNumber);
                 * 
                 * headerList.add( portNumberHeader );
                 */

                RegisterMessage response = new RegisterMessage( portNumber, 100, "Peer successfully registered" );
                connection.send( response );

            }
            catch ( IllegalArgumentException e ) {
                RegisterMessage response = new RegisterMessage( -1, 200, "Peer was not registered" );
                connection.send( response );
            }
            
            try {
                connectionSocket.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }

        }
        else {

        }

    }

}
