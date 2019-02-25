package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Connection {

    private final Socket socket;
    private final DataOutputStream output;
    private final DataInputStream input;

    public Connection(Socket socket) {
        this.socket = socket;
        try {
            this.output = new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to set up connection", e);
        }
    }

    public void send(P2PCommunication message) {
        byte[] data = message.toByteArray();
        try {
            output.writeInt(data.length);
            output.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Unable to send data", e);
        }
    }

    public P2PCommunication waitForNextCommunication() {
        try {
            int size = input.readInt();
            byte[] data = new byte[size];
            int sucBytes = input.read(data);
            if (sucBytes != size) throw new RuntimeException("Failed to read all bytes");
            return P2PCommunication.constructCommunicationFromBytes(data);
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data", e);
        }
    }

    public void close() {
        try {
            this.socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
