package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {

    private final DataOutputStream output;
    private final DataInputStream input;

    public Connection(Socket socket) {
        try {
            this.output = new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to set up connection", e);
        }
    }

    public void send(P2PMessage message) {
        byte[] data = message.toByteArray();
        try {
            output.writeInt(data.length);
            output.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to send data", e);
        }
    }

    public P2PMessage waitForNextMessage() {
        try {
            int size = input.readInt();
            byte[] data = new byte[size];
            int sucBytes = input.read(data);
            if (sucBytes != size) throw new RuntimeException("Failed to read all bytes");
            return P2PMessage.constructMessageFromBytes(data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data", e);
        }
    }

}
