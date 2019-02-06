package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.GetRFCMessage;
import edu.ncsu.NetworkingProject.protocol.P2PMessage;

public class Peer {

    public static void main(String[] args) {
        GetRFCMessage message = new GetRFCMessage(143);
        P2PMessage.constructMessageFromBytes(message.toByteArray());
    }

}
