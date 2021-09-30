package ru.mirea.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.movieguide.model.Photo;

import java.util.Optional;


public interface PhotoRepository extends JpaRepository<Photo, String> {
    Optional<Photo> findByPhotoUri(String photoUri);
}
