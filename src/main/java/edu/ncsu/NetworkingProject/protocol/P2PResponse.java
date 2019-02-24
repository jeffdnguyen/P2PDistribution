package edu.ncsu.NetworkingProject.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class P2PResponse extends P2PCommunication {

    private final Status status;
    private final List<P2PHeader> headers;
    private final byte[]          data;

    public P2PResponse(Status status, List<P2PHeader> headers, byte[] data) {
        this.status = status;
        this.headers = headers;
        this.data = data;
    }

    public P2PResponse(Status status, int cookie, byte[] data) {
        this.status = status;
        this.headers = Collections.singletonList(new P2PHeader("cookie", String.valueOf(cookie)));
        this.data = data;
    }

    public P2PResponse(Status status, byte[] data) {
        this.status = status;
        this.headers = new ArrayList<>();
        this.data = data;
    }

    public P2PResponse(Status status, int cookie) {
        this.status = status;
        this.headers = Collections.singletonList(new P2PHeader("cookie", String.valueOf(cookie)));
        this.data = new byte[0];
    }

    protected static P2PResponse constructResponseFromByteBuffer(ByteBuffer responseAsBytes) {
        int headerLength = responseAsBytes.getInt();
        responseAsBytes.limit(responseAsBytes.position() + headerLength);
        String messageAsString = Charset.defaultCharset().decode(responseAsBytes).toString();
        String[] lines = messageAsString.split("\n");
        String[] firstLineTokens = lines[0].split(" ");

        Status status = Status.findByCode(Integer.parseInt(firstLineTokens[1]));

        StringBuilder statusPhrase = new StringBuilder();
        for (int i = 1; i < firstLineTokens.length; i++) {
            if (!statusPhrase.toString().isEmpty()) statusPhrase.append(" ");
            statusPhrase.append(firstLineTokens[i]);
        }

        if (!status.getPhrase().equals(statusPhrase.toString())) throw new RuntimeException("Status code and message do not match");

        List<P2PHeader> headers = Arrays.stream(lines)
                .skip(1)
                .map(line -> new P2PHeader(line.substring(0, line.indexOf(":")), line.substring(line.indexOf(":") + 1)))
                .collect(Collectors.toList());

        responseAsBytes.limit(responseAsBytes.capacity());
        byte[] data = new byte[responseAsBytes.capacity() - responseAsBytes.position()];
        responseAsBytes.get(data, 0, data.length);

        return new P2PResponse(status, headers, data);
    }

    private String getTextComponent() {
        StringBuilder output = new StringBuilder();
        output.append(VERSION);
        output.append(" ");
        output.append(status.getCode());
        output.append(" ");
        output.append(status.getPhrase());
        output.append("\n");

        appendHeaders(output, headers);

        return output.toString();
    }

    public List<P2PHeader> getHeaders () {
        return headers;
    }

    @Override
    public String toString() {
        return getTextComponent();
    }

    @Override
    public byte[] toByteArray() {
        byte[] textAsBytes = getTextComponent().getBytes();
        // Add a 1 to identify this as a P2PResponse and add the length to help the receiver parse the buffer
        return ByteBuffer.allocate(5 + textAsBytes.length + data.length).put((byte)1).putInt(textAsBytes.length + data.length).put(textAsBytes).put(data).array();
    }

}
