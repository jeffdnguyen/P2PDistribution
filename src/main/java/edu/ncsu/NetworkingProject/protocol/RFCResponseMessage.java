package edu.ncsu.NetworkingProject.protocol;

import edu.ncsu.NetworkingProject.Peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RFCResponseMessage extends P2PMessage {

    private final int rfcId;

    private byte[] fileData = null;

    @SuppressWarnings("unused")
    RFCResponseMessage(String argument, List<P2PHeader> headers, byte[] data) {
        rfcId = Integer.parseInt(argument);

        fileData = data;
    }

    public RFCResponseMessage(int rfcId) {
        this.rfcId = rfcId;
    }

    public byte[] getFileData() {
        if (fileData == null) throw new IllegalStateException("The RFC file is local.");

        return fileData;
    }

    @Override
    protected String getMethodArgument() {
        return String.valueOf(rfcId);
    }

    @Override
    protected void addHeaders(LinkedList<P2PHeader> headers) { }

    @Override
    protected byte[] getMessageData() {
        try {
            FileInputStream fileIn = new FileInputStream(new File(Peer.rfcRoot, "rfc" + rfcId + ".txt"));

            return fileIn.readAllBytes();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File could not be found!", e);
        } catch (IOException e) {
            throw new RuntimeException("Trouble reading file", e);
        }
    }

}
