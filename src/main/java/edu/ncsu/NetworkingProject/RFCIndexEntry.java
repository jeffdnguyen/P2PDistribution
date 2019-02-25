package edu.ncsu.NetworkingProject;

import java.io.Serializable;
import java.util.Date;

public class RFCIndexEntry implements Serializable {

    private int number;
    private String title;
    private String hostname;
    private int port;
    long ttl;

    public RFCIndexEntry(int number, String title, String hostname, int port) {
        this.number = number;
        this.title = title;
        this.hostname = hostname;
        this.port = port;
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

    public int getPort() {
        return port;
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
