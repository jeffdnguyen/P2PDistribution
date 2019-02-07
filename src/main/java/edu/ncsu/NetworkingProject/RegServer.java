package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegServer {

    @SuppressWarnings ( "unused" )
    public static void main ( final String[] args ) {
        ServerSocket welcomeSocket;
        try {
            welcomeSocket = new ServerSocket( 65423 );
            while ( true ) {
                final Socket connectionSocket = welcomeSocket.accept();
                final ConnectionHandler connection = new ConnectionHandler( connectionSocket );
            }
        }
        catch ( final IOException e ) {
            e.printStackTrace();

        }
    }
}
