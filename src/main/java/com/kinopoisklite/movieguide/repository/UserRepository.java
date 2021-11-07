package com.kinopoisklite.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kinopoisklite.movieguide.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String username);

    Optional<User> findByRefreshToken(String refreshToken);
}
