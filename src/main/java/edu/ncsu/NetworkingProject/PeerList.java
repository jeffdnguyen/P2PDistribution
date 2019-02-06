package edu.ncsu.NetworkingProject;

import java.util.Date;

public class PeerList {

    private String  hostname;

    private int     cookie;

    private boolean isActive;

    private int     TTL;

    private int     portNumber;

    private int     numberOfTimesActive;

    private Date    lastActive;

    public String getHostname () {
        return hostname;
    }

    public void setHostname ( final String hostname ) {
        this.hostname = hostname;
    }

    public int getCookie () {
        return cookie;
    }

    public void setCookie ( final int cookie ) {
        this.cookie = cookie;
    }

    public boolean isActive () {
        return isActive;
    }

    public void setActive ( final boolean isActive ) {
        this.isActive = isActive;
    }

    public int getTTL () {
        return TTL;
    }

    public void setTTL ( final int tTL ) {
        TTL = tTL;
    }

    public int getPortNumber () {
        return portNumber;
    }

    public void setPortNumber ( final int portNumber ) {
        this.portNumber = portNumber;
    }

    public int getNumberOfTimesActive () {
        return numberOfTimesActive;
    }

    public void setNumberOfTimesActive ( final int numberOfTimesActive ) {
        this.numberOfTimesActive = numberOfTimesActive;
    }

    public Date getLastActive () {
        return lastActive;
    }

    public void setLastActive ( final Date lastActive ) {
        this.lastActive = lastActive;
    }

}
