package com.kinopoisklite.movieguide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kinopoisklite.movieguide.exception.PersistenceException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;


@Getter
@Setter
@Entity
@ToString(exclude = {"uri"})
@Table(name = "photos")
public class Photo {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private String id;

    @Column(name = "uri")
    @NotNull
    @NotEmpty
    @JsonIgnore
    private String uri;

    @Transient
    private String content;

    public Photo() {
        id = UUID.randomUUID().toString();
    }

    public Photo(String filename) {
        this.id = UUID.randomUUID().toString();
        this.uri = String.format("%s/src/main/resources/static/images/%s",
                System.getProperty("user.dir"),
                filename);
    }

    @SneakyThrows
    public Photo(String filename, String photoContent) {
        this.id = UUID.randomUUID().toString();
        this.uri = String.format("%s/src/main/resources/static/images/%s",
                System.getProperty("user.dir"),
                filename);
        byte[] fileContent = Base64.getDecoder().decode(photoContent);
        try {
            new FileOutputStream(this.uri).write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PersistenceException("Ошибка сохранения постера: " + e.getMessage());
        }
    }
}
