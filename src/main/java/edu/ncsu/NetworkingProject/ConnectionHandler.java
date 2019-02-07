package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;
import edu.ncsu.NetworkingProject.protocol.RegisterMessage;
import edu.ncsu.NetworkingProject.protocol.RegisterResponseMessage;

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
            RegisterMessage request = (RegisterMessage) message;

            int portNumber = request.getPortNumber();

            // If cookie is -1, then this is a new peer
            if ( request.getCookie() == -1 ) {
                PeerList newPeer = new PeerList();
                cookie += 1;

                newPeer.setHostname( connectionSocket.getInetAddress().getHostName() );
                newPeer.setCookie( cookie );
                newPeer.setActive( true );
                newPeer.setTTL( 7200 );
                newPeer.setPortNumber( portNumber );
                newPeer.setNumberOfTimesActive( 1 );
                newPeer.setLastActive( LocalDateTime.now() );

                peerList.add( newPeer );
            }
            else {
                for(PeerList peer : peerList) {
                    if (peer.getCookie() == request.getCookie()) {
                        peer.setActive( true );
                        peer.setTTL( 7200 );
                        peer.setNumberOfTimesActive( peer.getNumberOfTimesActive() + 1 );
                        break;
                    }
                }
            }
            
            RegisterResponseMessage response = new RegisterResponseMessage( 100, "(Success)", cookie );
            connection.send( response );

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
