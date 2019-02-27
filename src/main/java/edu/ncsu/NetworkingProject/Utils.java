package edu.ncsu.NetworkingProject;

import edu.ncsu.NetworkingProject.protocol.P2PHeader;

import java.io.*;
import java.util.List;

public class Utils {

    public static <E> E byteArrayToObject(byte[] objectAsBytes) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(objectAsBytes);
            ObjectInputStream is = new ObjectInputStream(in);
            return (E) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] objectToByteArray(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getCookieFromHeaders(List<P2PHeader> headers) {
        P2PHeader cookie = headers.stream()
                .filter(header -> header.name.equals("Cookie"))
                .findFirst()
                .orElseThrow();
        return Integer.parseInt(cookie.value);
    }

    public static String getHostFromHeaders(List<P2PHeader> headers) {
        P2PHeader host = headers.stream()
                .filter(header -> header.name.equals("Host"))
                .findFirst()
                .orElseThrow();
        return host.value;
    }

}
