package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegServer {
    public final static int REGSERVER_PORT = 65423;

    @SuppressWarnings ( "unused" )
    public static void main ( String[] args ) {
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
