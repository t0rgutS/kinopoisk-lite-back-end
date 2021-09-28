package ru.mirea.movieguide.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@Table(name = "photos")
public class Photo {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private String id;

    @Column(name = "photo_uri")
    @NotNull
    @NotEmpty
    private String photoUri;

    @Transient
    private byte[] photoContent;

    public Photo() {
        id = UUID.randomUUID().toString();
    }
}
