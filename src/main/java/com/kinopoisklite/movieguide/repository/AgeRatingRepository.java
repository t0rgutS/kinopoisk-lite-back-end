package com.kinopoisklite.movieguide.repository;

import com.kinopoisklite.movieguide.model.AgeRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgeRatingRepository extends JpaRepository<AgeRating, String> {
    Optional<AgeRating> findByRatingCategory(String ratingCategory);
}
