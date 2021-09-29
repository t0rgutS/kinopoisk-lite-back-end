package ru.mirea.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.movieguide.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByRefreshToken(String refreshToken);
}
