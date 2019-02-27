package edu.ncsu.NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class proj1 {
    public static int STARTING_PORT = 8000;

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
            Peer.start(regServerIP, STARTING_PORT, true, true);
            Peer.start(regServerIP, STARTING_PORT + 1, true, true);
        } else {
            System.out.println("How many peers do you wish to run on this machine?");
            int numPeers = getInt(999);
            System.out.println("How should each peer behave?");
            System.out.println("1) \"Best Case\": Randomize download order so no one peer is overwhelmed");
            System.out.println("2) \"Worst Case\": All peers download in the same order, which overwhelms one peer at a time");
            int caseToUse = getInt(2);
            for (int i = 0; i < numPeers; i++) {
                // Pass the peer a unique port number
                if (caseToUse == 1) {
                    Peer.start(regServerIP, STARTING_PORT + i, false, true);
                } else if (caseToUse == 2) {
                    Peer.start(regServerIP, STARTING_PORT + i, false, false);
                } else {
                    throw new NumberFormatException("Please choose 1 or 2");
                }
            }
        }
    }

    private static int getInt (int max) {
        System.out.print("> ");
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            int response = Integer.parseInt(input.readLine());
            if (response < 1 || response > max) {
                throw new NumberFormatException();
            }
            return response;
        } catch (IOException | NumberFormatException e) {
            System.out.println("Please enter a number between 1 and " + max);
            return getInt(max);
        }
    }

    private static String getString () {
        System.out.print("> ");
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            return input.readLine();
        } catch (IOException e) {
            System.out.println("Please enter a valid string");
            return getString();
        }
    }

}
