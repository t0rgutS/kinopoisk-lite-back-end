package ru.mirea.movieguide.exception;

public class NotEnoughArgsException extends PersistenceException {
    public NotEnoughArgsException(String message) {
        super(message);
    }
}
