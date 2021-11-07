package com.kinopoisklite.movieguide.exception;

public class UserAlreadyExistsException extends PersistenceException {
    public UserAlreadyExistsException(String login) {
        super(String.format("User %s already exists!", login));
    }
}
