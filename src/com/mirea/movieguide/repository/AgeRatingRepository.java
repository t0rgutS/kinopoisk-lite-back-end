package com.mirea.movieguide.repository;

import com.mirea.movieguide.model.AgeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgeRatingRepository extends JpaRepository<AgeRating, String> {
    Optional<AgeRating> findByRatingCategory(String ratingCategory);
}
