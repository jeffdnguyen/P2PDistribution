package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PMessage;

import java.io.Serializable;
import java.util.Date;

public class RFCIndexEntry implements Serializable {

    private int number;
    private String title;
    private String hostname;
    long ttl;

    public RFCIndexEntry(int number, String title) {
        this.number = number;
        this.title = title;
        this.hostname = P2PMessage.getHostname();
        this.ttl = new Date().getTime();
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RFCIndexEntry)) return false;

        return this.getNumber() == ((RFCIndexEntry) obj).number && this.getHostname().equals(((RFCIndexEntry) obj).getHostname());
    }

    @Override
    public String toString() {
        return "RFC " + getNumber() + ": " + getTitle() + " hosted by " + getHostname();
    }
}
