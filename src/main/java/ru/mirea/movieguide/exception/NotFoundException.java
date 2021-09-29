package ru.mirea.movieguide.exception;

public class NotFoundException extends PersistenceException {
    public NotFoundException(String message) {
        super(message);
    }
}
