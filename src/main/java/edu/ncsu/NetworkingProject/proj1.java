package edu.ncsu.NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class proj1 {

    public static void main(String[] args) {
        System.out.println("Is this host the registration server? (y/n)");
        String response;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            response = input.readLine();        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (response.toLowerCase().equals("y")) {
            RegServer.main(new String[0]);
        } else if (response.toLowerCase().equals("n")) {
            Peer.main(new String[0]);
        } else {
            System.out.println("Please put y or n");
            System.exit(-1);
        }
    }

}