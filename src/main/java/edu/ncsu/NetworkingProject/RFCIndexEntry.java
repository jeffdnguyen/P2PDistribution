package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PCommunication;

import java.io.Serializable;
import java.util.Date;

public class RFCIndexEntry implements Serializable {

    private int number;
    private String title;
    private String hostname;
    private int port;
    long ttl;

    public RFCIndexEntry(int number, String title) {
        this.number = number;
        this.title = title;
        this.hostname = P2PCommunication.getHostname();
        this.ttl = new Date().getTime();
    }

    public RFCIndexEntry(int number, String title, String hostname, int port) {
        /* TODO: If an entry refers to a remote computer,
           the client needs to know both the hostname and the port so it can open a connection there
         */
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
