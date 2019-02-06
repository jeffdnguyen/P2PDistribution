package edu.ncsu.NetworkingProject;

import java.net.Socket;
import java.util.LinkedList;

class ConnectionHandler implements Runnable {

    Socket               connectionSocket;
    Thread               clientThread;

    LinkedList<PeerList> peerlist = new LinkedList<PeerList>();

    ConnectionHandler ( final Socket socket ) {
        connectionSocket = socket;
        clientThread = new Thread( this );
        clientThread.start();
    }

    @Override
    public void run () {
        // TODO Register the new peer
    }

}
