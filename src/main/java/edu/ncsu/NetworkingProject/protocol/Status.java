package edu.ncsu.NetworkingProject.protocol;

import java.util.Arrays;

public enum Status {

    SUCCESS(100, "Success"),
    BAD_REQUEST(200, "Bad Request"),
    FORBIDDEN(201, "Forbidden"),
    NOT_FOUND(202, "Not Found");

    private final int code;
    private final String phrase;

    private Status(int code, String phrase) {

        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }

    public static Status findByCode(int code) {
        return Arrays.stream(Status.values()).filter(status -> code == status.code).findFirst().orElseThrow();
    }

}
