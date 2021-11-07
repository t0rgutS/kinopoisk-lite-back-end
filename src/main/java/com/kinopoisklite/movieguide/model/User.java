package com.kinopoisklite.movieguide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "password")
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
    private String refreshToken;

    @Column
    @NotNull
    private Roles role;

    @Transient
    private List<String> favMovies = new ArrayList<>();

    public enum Roles {
        ROLE_USER, ROLE_MODER, ROLE_ADMIN
    }
}