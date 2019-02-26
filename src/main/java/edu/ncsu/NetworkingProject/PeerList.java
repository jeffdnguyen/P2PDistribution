package edu.ncsu.NetworkingProject;

import java.util.Arrays;
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
        backingList
                .stream()
                .filter(entry -> entry.getTTL() <= System.currentTimeMillis())
                .forEach(entry -> entry.setActive(false));
    }

    public void forEachActivePeer(Predicate<? super PeerListEntry> loop) {
        cleanList();

        for (PeerListEntry entry : backingList) {
            if (!entry.isActive()) continue;
            if (!loop.test(entry)) break;
        }
    }

    /**
     * Since PeerList isn't serializable, this method returns a serializable copy
     * of backingList
     */
    public LinkedList<PeerListEntry> getCopy() {
        return new LinkedList<>(backingList);
    }

}
