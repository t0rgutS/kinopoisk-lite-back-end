package ru.mirea.movieguide.service;

import lombok.RequiredArgsConstructor;
import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.AgeRating;
import ru.mirea.movieguide.repository.AgeRatingRepository;

import java.util.List;

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
