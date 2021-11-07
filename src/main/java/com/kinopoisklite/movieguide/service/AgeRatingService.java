package com.kinopoisklite.movieguide.service;

import com.kinopoisklite.movieguide.exception.NotFoundException;
import com.kinopoisklite.movieguide.exception.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.kinopoisklite.movieguide.model.AgeRating;
import com.kinopoisklite.movieguide.repository.AgeRatingRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
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
