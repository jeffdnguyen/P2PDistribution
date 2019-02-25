package edu.ncsu.NetworkingProject;

import java.io.Serializable;

public class PeerListEntry implements Serializable {
    
    private String        hostname;

    private int           cookie;

    private boolean       isActive;

    private int           portNumber;

    private int           numberOfTimesActive;

    private long        willExpireTime;

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

    public long getTTL () {
        return willExpireTime;
    }

    public void setTTL ( final int TTL ) {
        this.willExpireTime = System.currentTimeMillis() + TTL;
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

}
