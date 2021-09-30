package ru.mirea.movieguide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private String username;

    @Column
    @NotNull(message = "Введите пароль!")
    @NotEmpty(message = "Введите пароль!")
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @Column(name = "refresh_token")
    private String refreshToken;

    public User() {
        id = UUID.randomUUID().toString();
    }
}
