package com.kinopoisklite.movieguide.exception;

public class NotFoundException extends PersistenceException {
    public NotFoundException(String message) {
        super(message);
    }
}
