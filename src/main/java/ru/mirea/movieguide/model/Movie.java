package ru.mirea.movieguide.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @NotNull
    @NotEmpty
    private String title;

    @Column(name = "release_year")
    @NotNull
    private Integer releaseYear;

    @Column
    @NotNull
    private Integer duration;

    @Column
    private String description;

    @JoinColumn(name = "age_rating_id")
    @NotNull(message = "Укажите возрастной рейтинг!")
    @ManyToOne(fetch = FetchType.LAZY)
    private AgeRating ageRating;

    @JoinColumn(name = "cover_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Photo photo;

    public Movie() {
        id = UUID.randomUUID().toString();
    }

    public Movie(String uuid) {
        this.id = uuid;
    }

}
