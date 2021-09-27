package com.mirea.movieguide.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Table(name = "age_ratings")
public class AgeRating {
    @Id
   // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "age_rating_uuid")
    private String uuid;

    @Column(name = "rating_category")
    @NotNull(message = "Укажите возрастной рейтинг!")
    @NotEmpty(message = "Укажите возрастной рейтинг!")
    private String ratingCategory;

    @Column(name = "min_age")
    @NotNull(message = "Укажите минимальный возраст!")
    @NotEmpty(message = "Укажите минимальный возраст!")
    private Integer minAge;

    public AgeRating() {
        uuid = UUID.randomUUID().toString();
    }

}
