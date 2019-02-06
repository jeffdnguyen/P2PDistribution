package edu.ncsu.NetworkingProject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RegServer {

    public static void main ( final String[] args ) {
        ServerSocket welcomeSocket;
        try {
            welcomeSocket = new ServerSocket( 65423 );
            final Socket connectionSocket = welcomeSocket.accept();
        }
        catch ( final IOException e ) {
            e.printStackTrace();

        }
    }
}
