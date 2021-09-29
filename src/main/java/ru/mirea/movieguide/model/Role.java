package ru.mirea.movieguide.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "role_id")
    private String id;

    @Column
    @NotNull(message = "Укажите роль!")
    @NotEmpty(message = "Укажите роль!")
    private String role;

    public Role() {
        id = UUID.randomUUID().toString();
    }
}
