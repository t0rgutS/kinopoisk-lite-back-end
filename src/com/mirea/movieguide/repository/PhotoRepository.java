package com.mirea.movieguide.repository;

import com.mirea.movieguide.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, String> {
    Photo findByPhotoUri(String photoUri);
}
