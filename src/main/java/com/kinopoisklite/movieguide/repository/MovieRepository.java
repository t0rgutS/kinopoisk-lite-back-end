package com.kinopoisklite.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kinopoisklite.movieguide.model.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, String> {
    List<Movie> findByIdIn(List<String> ids);
}
