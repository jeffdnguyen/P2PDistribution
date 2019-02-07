package edu.ncsu.NetworkingProject;

import java.io.Serializable;
import java.util.LinkedList;

public class RFCIndex implements Serializable {

    public LinkedList<RFCIndexEntry> index = new LinkedList<>();

    public void mergeWith(RFCIndex other) {
        for (RFCIndexEntry remoteEntry : other.index) {
            int i;
            if ((i = index.indexOf(remoteEntry)) != -1) {
                RFCIndexEntry localEntry = index.get(i);
                localEntry.ttl = Math.max(localEntry.ttl, remoteEntry.ttl);
            } else {
                index.add(remoteEntry);
            }
        }
    }

    @Override
    public String toString() {
        return index.toString();
    }
}
