package ru.mirea.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.movieguide.model.AgeRating;

import java.util.Optional;

public interface AgeRatingRepository extends JpaRepository<AgeRating, String> {
    Optional<AgeRating> findByRatingCategory(String ratingCategory);
}
