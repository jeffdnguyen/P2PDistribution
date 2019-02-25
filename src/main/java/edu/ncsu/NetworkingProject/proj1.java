package edu.ncsu.NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class proj1 {

    public static void main(String[] args) {
        Thread regServer = new Thread(new RegServer());
        regServer.start();
        System.out.println("Registration server started successfully.");
        System.out.println("How many peers do you wish to run?");
        int response = getInt();
        for (int i = 0; i < response; i++) {
            // Pass the peer a unique port number
            Peer.main(new String[] { Integer.toString(8000 + i) });
        }
    }

    private static int getInt () {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            int response = Integer.parseInt(input.readLine());
            if (response < 1) {
                throw new NumberFormatException();
            }
            return response;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Please enter a number > 0");
            return getInt();
        }
    }

}
