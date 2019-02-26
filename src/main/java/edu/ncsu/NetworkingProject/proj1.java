package edu.ncsu.NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class proj1 {
    public static int STARTING_IP = 8000;

    public static void main(String[] args) {
        System.out.println("========NOTE: Make sure your /rfcs/ folder is set up according to the README========");
        System.out.println("What is the IP of the RegServer? (or enter 'localhost' if you wish to run it locally)");
        String regServerIP = getString();
        if (regServerIP.equals("localhost")) {
            new Thread(new RegServer()).start();
            System.out.println("Registration server started successfully.");
        }

        System.out.println("Are you running the \"Testing Scenario\"? (y/n)");
        String testing = getString().toLowerCase();
        while (!testing.startsWith("y") && !testing.startsWith("n")) {
            System.out.println("Please enter either \"y\" or \"n\"");
            testing = getString().toLowerCase();
        }

        if (testing.startsWith("y")) {
            Peer.start(regServerIP, STARTING_IP, true );
            Peer.start(regServerIP, STARTING_IP + 1, true );
        } else {
            System.out.println("How many peers do you wish to run on this machine?");
            int response = getInt();
            for (int i = 0; i < response; i++) {
                // Pass the peer a unique port number
                Peer.start(regServerIP, STARTING_IP + i, false );
            }
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

    private static String getString () {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            return input.readLine();
        } catch (IOException e) {
            System.out.println("Please enter a valid string");
            return getString();
        }
    }

}
