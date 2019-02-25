package edu.ncsu.NetworkingProject;

import java.util.LinkedList;

public class PeerList extends LinkedList<PeerListEntry> {

    private static PeerList INSTANCE = new PeerList();

    private PeerList() {}

    public static PeerList getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public PeerListEntry get(int index) {
        if (super.get(index).getTTL() >= System.currentTimeMillis()) this.remove(index);
        return super.get(index);
    }
}
