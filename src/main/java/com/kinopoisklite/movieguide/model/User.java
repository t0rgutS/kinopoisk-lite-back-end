package com.kinopoisklite.movieguide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password", "favMovies", "refreshToken"})
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private String id;

    @Column(unique = true)
    @NotNull(message = "Укажите логин!")
    @NotEmpty(message = "Укажите логин!")
    private String login;

    @Column
    @NotNull(message = "Введите пароль!")
    @NotEmpty(message = "Введите пароль!")
    @JsonIgnore
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    @NotNull
    private Boolean external;

    @Column(name = "refresh_token")
    @JsonIgnore
    private String refreshToken;

    @Column
    @NotNull
    private Roles role;

    @ElementCollection
    @JsonIgnore
    private List<String> favMovies = new ArrayList<>();

    public enum Roles {
        ROLE_USER, ROLE_MODER, ROLE_ADMIN
    }

    public User(String id, String login, String password, String firstName, String lastName, Boolean external, Roles role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.external = external;
        this.role = role;
    }
}