package com.smartshop.Exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized access.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

}
