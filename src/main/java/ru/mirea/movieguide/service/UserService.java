package ru.mirea.movieguide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.User;
import ru.mirea.movieguide.repository.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserService {
    private final UserRepository userRepo;

    public User findUser(String username, String password) throws PersistenceException {
        return userRepo.findByUsernameAndPassword(username, password).orElseThrow(() ->
                new NotFoundException("Пользователь " + username + " не найден!"));
    }

    public User findUserById(String userId) throws PersistenceException {
        return userRepo.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден!"));
    }

    public User findUserByUsername(String username) throws PersistenceException {
        return userRepo.findByUsername(username).orElseThrow(() ->
                new NotFoundException("Пользователь " + username + " не найден!"));
    }

    public User findTokenOwner(String refreshToken) throws PersistenceException {
        return userRepo.findByRefreshToken(refreshToken).orElseThrow(() ->
                new NotFoundException("Пользователь не найден!"));
    }

    public String setRefreshToken(User user, boolean replace) throws PersistenceException {
        if (user.getRefreshToken() == null || replace)
            user.setRefreshToken(UUID.randomUUID().toString().replaceAll("\\-", ""));
        userRepo.save(user);
        return user.getRefreshToken();
    }
}
