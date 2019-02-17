package edu.ncsu.NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class proj1 {

    public static void main(String[] args) {
        System.out.println("Which service would you like to run?");
        System.out.println("1) Registration Server");
        System.out.println("2) Peer(s)");
        int response = getInt();

        if (response == 1) {
            RegServer.main(new String[0]);
        } else if (response == 2) {
            System.out.println("How many peers do you wish to run?");
            response = getInt();
            for (int i = 0; i < response; i++) {
                // Pass the peer a unique port number
                Peer.main(new String[] { Integer.toString(8000 + i) });
            }
        } else {
            System.out.println("Please enter 1 or 2");
            System.exit(-1);
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
