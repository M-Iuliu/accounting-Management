package com.accounting.exeption;

public class OfferStatusConflictException extends RuntimeException {
    public OfferStatusConflictException() {
        super();
    }

    public OfferStatusConflictException(String message) {
        super(message);
    }

    public OfferStatusConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public OfferStatusConflictException(Throwable cause) {
        super(cause);
    }
}

