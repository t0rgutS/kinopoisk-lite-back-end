package com.mirea.movieguide.service;

import com.mirea.movieguide.exception.NotFoundException;
import com.mirea.movieguide.exception.PersistenceException;
import com.mirea.movieguide.model.AgeRating;
import com.mirea.movieguide.repository.AgeRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgeRatingService {
    private final AgeRatingRepository ageRatingRepo;

    public List<AgeRating> getAll() {
        return ageRatingRepo.findAll();
    }

    public AgeRating get(String id) throws PersistenceException {
        return ageRatingRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Рейтинг с идентификатором " + id + " не найден!"));
    }
}
