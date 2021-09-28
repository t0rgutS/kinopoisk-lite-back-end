package ru.mirea.movieguide.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "password")
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private String id;

    @Column(unique = true)
    @NotNull(message = "Укажите логин!")
    @NotEmpty(message = "Укажите логин!")
    private String username;

    @Column
    @NotNull(message = "Введите пароль!")
    @NotEmpty(message = "Введите пароль!")
    private String password;

    @JoinColumn(name = "role_id")
    @OneToMany(fetch = FetchType.LAZY)
    private Role role;

    @Column(name = "refresh_token")
    private String refreshToken;

    public User() {
        id = UUID.randomUUID().toString();
    }
}
