package com.kinopoisklite.movieguide.service;

import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.exception.UserAlreadyExistsException;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserService {
    private final UserRepository userRepo;

    @Lazy
    @Autowired
    private BCryptPasswordEncoder encoder;

    public User findById(String userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public User findByLogin(String login) {
        return userRepo.findByLogin(login).orElse(null);
    }

    public String setRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        userRepo.save(user);
        return user.getRefreshToken();
    }

    public User createUser(String id, String login, String password,
                           String firstName, String lastName, Boolean external, User.Roles role) throws PersistenceException {
        User user = new User();
        if (id != null)
            user.setId(id);
        else
            user.setId(UUID.randomUUID().toString());
        if (findByLogin(login) != null)
            throw new UserAlreadyExistsException(login);
        user.setLogin(login);
        user.setPassword(encoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setExternal(external);
        if (role != null)
            user.setRole(role);
        else
            user.setRole(User.Roles.ROLE_USER);
        return userRepo.save(user);
    }

    public User updateUser(String id, String firstName, String lastName) throws PersistenceException {
        User user = userRepo.findById(id).orElseThrow(() -> new PersistenceException("User not found!"));
        if (user.getExternal())
            throw new PersistenceException(String.format("User %s is external, his profile is not editable!",
                    user.getLogin()));
        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        return userRepo.save(user);
    }

    public void addToFav(User user, String movieId) {
        if (!user.getFavMovies().contains(movieId)) {
            user.getFavMovies().add(movieId);
            userRepo.save(user);
        }
    }

    public void removeFromFav(User user, String movieId) {
        if (user.getFavMovies().contains(movieId)) {
            user.getFavMovies().remove(movieId);
            userRepo.save(user);
        }
    }

}
