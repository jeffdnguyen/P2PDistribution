package edu.ncsu.NetworkingProject;

import java.util.LinkedList;
import java.util.function.Predicate;

public class PeerList {

    private static PeerList INSTANCE = new PeerList();

    private LinkedList<PeerListEntry> backingList = new LinkedList<>();

    private PeerList() {}

    public static PeerList getINSTANCE() {
        return INSTANCE;
    }

    public void add(PeerListEntry entry) {
        backingList.add(entry);
    }

    public void cleanList() {
        backingList.removeIf(entry -> entry.getTTL() >= System.currentTimeMillis());
    }

    public void forEach(Predicate<? super PeerListEntry> loop) {
        cleanList();

        for (PeerListEntry entry : backingList) {
            if (!loop.test(entry)) break;
        }
    }

}
