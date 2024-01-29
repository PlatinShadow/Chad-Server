package de.uulm.in.vs.vns.p6b.vnscp.exceptions;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String message) {
        super("Invalid Message - " + message);
    }
}
