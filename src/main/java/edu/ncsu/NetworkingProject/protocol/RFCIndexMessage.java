package edu.ncsu.NetworkingProject.protocol;

import edu.ncsu.NetworkingProject.RFCIndex;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class RFCIndexMessage extends P2PMessage {

    private final RFCIndex index;

    RFCIndexMessage(String argument, List<P2PHeader> headers, byte[] data) {
        if (!argument.equals(getMethodArgument())) throw new ProtocolException.UnexpectedArgumentException();

        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(data));
            index = (RFCIndex) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Unable to instantiate RFCIndex from data given", e);
        }
    }

    RFCIndexMessage(RFCIndex index) {
        this.index = index;
    }
    
    @Override
    protected String getMethodArgument() {
        return "RFC-Index";
    }

    @Override
    protected void addHeaders(LinkedList<P2PHeader> headers) { }

    @Override
    protected byte[] getMessageData() {
        try {
            ByteArrayOutputStream result;
            ObjectOutputStream output = new ObjectOutputStream(result = new ByteArrayOutputStream());
            output.writeObject(index);
            output.close();
            return result.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
