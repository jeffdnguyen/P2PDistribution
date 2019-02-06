package edu.ncsu.NetworkingProject.protocol;

public abstract class ProtocolException extends RuntimeException {

    public ProtocolException(String message) {
        super(message);
    }

    public static class MissingArgumentException extends ProtocolException {

        public MissingArgumentException() {
            super("The received message contained no argument, but the argument type requires one!");
        }

    }

    public static class NoSuchMessageType extends ProtocolException {

        public NoSuchMessageType(String messageType) {
            super("The messageType \"" + messageType + "\" does not exist.");
        }

    }

}