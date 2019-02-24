package edu.ncsu.NetworkingProject;

import java.io.*;

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

}
