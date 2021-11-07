package com.kinopoisklite.movieguide.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@Entity
@ToString(exclude = {"photoUri"})
@Table(name = "photos")
public class Photo {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "photo_id")
    private String id;

    @Column(name = "photo_uri")
    @NotNull
    @NotEmpty
    @JsonIgnore
    private String photoUri;

    @Transient
    private String photoContent;

    public Photo() {
        id = UUID.randomUUID().toString();
    }
}
