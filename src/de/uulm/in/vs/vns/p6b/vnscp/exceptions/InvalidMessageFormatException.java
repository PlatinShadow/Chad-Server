package de.uulm.in.vs.vns.p6b.vnscp.exceptions;

public class InvalidMessageFormatException extends RuntimeException {
    public InvalidMessageFormatException(String form) {
        super("Unknown Field (" + form + ")");
    }
}
