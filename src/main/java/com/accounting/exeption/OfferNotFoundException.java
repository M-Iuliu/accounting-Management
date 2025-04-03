package com.accounting.exeption;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException() {
        super();
    }

    public OfferNotFoundException(String message) {
        super(message);
    }

    public OfferNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OfferNotFoundException(Throwable cause) {
        super(cause);
    }
}

