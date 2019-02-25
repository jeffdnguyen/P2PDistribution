package edu.ncsu.NetworkingProject;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class PeerList {

    private String        hostname;

    private int           cookie;

    private boolean       isActive;

    private int           TTL;

    private int           portNumber;

    private int           numberOfTimesActive;

    private LocalDateTime lastActive;

    private Timer         timer;

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

    public void setTTL ( final int TTL ) {
        this.TTL = TTL;
        if ( isActive )
            decayTTL();
    }

    private void decayTTL () {
        if ( timer != null ) {
            timer.cancel();
        }
        timer = new Timer( true );
        timer.schedule( new deactivatePeer(), getTTL() * 1000 );
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

    public LocalDateTime getLastActive () {
        return lastActive;
    }

    public void setLastActive ( final LocalDateTime lastActive ) {
        this.lastActive = lastActive;
    }

    class deactivatePeer extends TimerTask {
        public void run () {
            isActive = false;
            System.out.println("Peer " + cookie + " set to 'inactive'");
        }
    }

}
