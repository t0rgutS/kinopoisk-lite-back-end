package com.kinopoisklite.movieguide.repository;

import com.kinopoisklite.movieguide.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PhotoRepository extends JpaRepository<Photo, String> {
    Optional<Photo> findByPhotoUri(String photoUri);
}
