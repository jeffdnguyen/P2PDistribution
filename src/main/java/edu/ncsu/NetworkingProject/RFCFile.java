package edu.ncsu.NetworkingProject;

import java.io.*;

public class RFCFile {

    final int id;
    final String title;

    public RFCFile(File referent) {
        id = Integer.parseInt(referent
                .getName()
                .substring(3)
                .chars()
                .takeWhile(Character::isDigit)
                .mapToObj(i -> (char)i).collect(
                StringBuilder::new,
                StringBuilder::append,
                StringBuilder::append
        ).toString());

        try {
            FileInputStream fstream = new FileInputStream(referent);
            BufferedReader input = new BufferedReader(new InputStreamReader(fstream));

            String line;
            while ((line = input.readLine()) != null) {
                if (line.startsWith("RFC " + id)) {
                    String temp = line
                            .substring(4)
                            .chars()
                            .dropWhile(codePoint -> !Character.isAlphabetic(codePoint))
                            .takeWhile(codePoint -> codePoint != '\n')
                            .mapToObj(i -> (char)i).collect(
                                    StringBuilder::new,
                                    StringBuilder::append,
                                    StringBuilder::append
                            ).toString();
                    input.close();

                    Character prev = null;
                    String temp2 = "";
                    for (char c : temp.toCharArray()) {
                        if ((prev != null && prev != ' ') || c != ' ') {
                            if (prev != null) temp2 += prev;
                            prev = c;
                        } else {
                            break;
                        }
                    }
                    title = temp2;
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file", e);
        }
        throw new RuntimeException("Title could not be located");
    }
}
