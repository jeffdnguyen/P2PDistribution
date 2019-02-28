package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegServer implements Runnable {
    public final static int REGSERVER_PORT = 65243;

    @SuppressWarnings ( "unused" )
    public void run () {
        ServerSocket welcomeSocket;
        try {
            welcomeSocket = new ServerSocket( REGSERVER_PORT );
            while ( true ) {
                Socket connectionSocket = welcomeSocket.accept();
                ConnectionHandler connection = new ConnectionHandler( connectionSocket );
            }
        }
        catch ( final IOException e ) {
            e.printStackTrace();

        }
    }
}
