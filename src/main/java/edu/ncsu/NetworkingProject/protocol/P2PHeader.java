package edu.ncsu.NetworkingProject.protocol;

public class P2PHeader {

    public String name;
    public String value;

    public P2PHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
    
}
