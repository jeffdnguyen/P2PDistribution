package edu.ncsu.NetworkingProject.protocol;

import java.util.LinkedList;

public class RegisterMessage extends P2PMessage {

    private final int portNumber;
    
    private  int statusCode = 0;
    
    private  String phrase = "";
    
    // Construct request
    public RegisterMessage(byte[] data) {
        String message = new String(data);
        portNumber = Integer.parseInt(message.split(" ")[1]);
    }
    
    // Construct response
    public RegisterMessage(int portNumber, int statusCode, String phrase) {
        this.portNumber = portNumber;
        this.statusCode = statusCode;
        this.phrase = phrase;
    }
    
    public int getPortNumber() {
        return this.portNumber;
    }
    
    @Override
    protected String getMethodArgument () {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void addHeaders ( final LinkedList<P2PHeader> headers ) {
        // TODO Auto-generated method stub

    }

    @Override
    protected byte[] getMessageData () {
        return null;
    }

}
