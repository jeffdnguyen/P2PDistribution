package edu.ncsu.NetworkingProject;

import java.io.File;

public class Peer {

    public static final File rfcRoot = new File("./rfcs/");

    private static RFCIndex rfcIndex = new RFCIndex();

    public static void main(String[] args) {
        addLocalRfcFiles();

        RFCPeerServer server = new RFCPeerServer(8765, rfcIndex);
        Thread serverThread = new Thread(server);
        serverThread.setName("RFC server thread");
        serverThread.start();
    }

    private static void addLocalRfcFiles() {
        for (File file : rfcRoot.listFiles()) {
            if (!file.getName().startsWith("rfc")) continue;
            RFCFile rfcFile = new RFCFile(file);
            rfcIndex.index.add(new RFCIndexEntry(rfcFile.id, rfcFile.title));
        }
    }

}
