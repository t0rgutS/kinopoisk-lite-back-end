package com.kinopoisklite.movieguide.model;

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
@Table(name = "age_ratings")
public class AgeRating {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "age_rating_id")
    private String id;

    @Column(name = "rating_category")
    @NotNull(message = "Укажите возрастной рейтинг!")
    @NotEmpty(message = "Укажите возрастной рейтинг!")
    private String ratingCategory;

    @Column(name = "min_age")
    @NotNull(message = "Укажите минимальный возраст!")
    @NotEmpty(message = "Укажите минимальный возраст!")
    private Integer minAge;

    public AgeRating() {
        id = UUID.randomUUID().toString();
    }

    public AgeRating(String id, String ratingCategory, Integer minAge) {
        this.id = id;
        this.ratingCategory = ratingCategory;
        this.minAge = minAge;
    }

}
