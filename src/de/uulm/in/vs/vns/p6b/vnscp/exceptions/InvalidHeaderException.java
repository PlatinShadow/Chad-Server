package de.uulm.in.vs.vns.p6b.vnscp.exceptions;

public class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(String header) {
        super("Invalid Message Header - " + header);
    }

}
