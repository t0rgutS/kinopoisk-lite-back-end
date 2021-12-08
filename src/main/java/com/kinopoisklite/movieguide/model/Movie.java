package com.kinopoisklite.movieguide.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "movie_id")
    private String id;

    @Column
    @NotNull(message = "Movie title must be present!")
    @NotEmpty(message =  "Movie title must not be null!")
    private String title;

    @Column(name = "release_year")
    @NotNull(message = "Movie release year must be present!")
    private Integer releaseYear;

    @Column
    @NotNull(message = "Movie duration must be present!")
    private Integer duration;

    @Column
    private String country;

    @Column
    private String genre;

    @Column
    private String description;

    @JoinColumn(name = "age_rating_id")
    @NotNull(message = "Age rating must be present!")
    @ManyToOne
    private AgeRating ageRating;

    @JoinColumn(name = "cover_id")
    @OneToOne
    private Photo cover;

    public Movie() {
        id = UUID.randomUUID().toString();
    }

    public Movie(String uuid) {
        this.id = uuid;
    }

    public Movie(String title, Integer releaseYear, Integer duration, String country, String genre,
                 String description, AgeRating rating, Photo cover) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.country = country;
        this.genre = genre;
        this.description = description;
        this.ageRating = rating;
        this.cover = cover;
    }

}
