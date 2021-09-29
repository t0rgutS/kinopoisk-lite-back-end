package ru.mirea.movieguide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.movieguide.model.Photo;


public interface PhotoRepository extends JpaRepository<Photo, String> {
    Photo findByPhotoUri(String photoUri);
}
