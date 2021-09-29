package ru.mirea.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.movieguide.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, String> {

}
