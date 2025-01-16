package org.example.exceptions;

public class AdsNotFoundException extends RuntimeException {
    public AdsNotFoundException(String message) {
        super(message);
    }
}
