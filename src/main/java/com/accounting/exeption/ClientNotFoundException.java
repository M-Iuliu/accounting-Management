package com.accounting.exeption;

public class ClientNotFoundException extends Exception {
    // Constructor with no arguments
    public ClientNotFoundException() {
        super();
    }

    // Constructor that accepts a message
    public ClientNotFoundException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }
}

