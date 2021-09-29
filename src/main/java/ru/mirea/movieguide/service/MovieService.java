package ru.mirea.movieguide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import ru.mirea.movieguide.exception.NotEnoughArgsException;
import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.AgeRating;
import ru.mirea.movieguide.model.Movie;
import ru.mirea.movieguide.repository.AgeRatingRepository;
import ru.mirea.movieguide.repository.MovieRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MovieService {
    private final MovieRepository movieRepo;
    private final AgeRatingRepository ageRatingRepo;

    public List<Movie> getAll() {
        return movieRepo.findAll();
    }

    public Movie get(String id) throws PersistenceException {
        return movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Фильм с идентификатором " + id + " не найден!"));
    }

    @Secured("ADMIN")
    public Movie upsert(Map<String, String> request) throws PersistenceException {
        Movie movie;
        if (request.containsKey("id")) {
            try {
                movie = movieRepo.getById(request.get("id"));
            } catch (EntityNotFoundException enf) {
                throw new NotFoundException("Фильм с идентификатором " + request.get("id") + " не найден!");
            }
        } else
            movie = new Movie();
        movie.setTitle(request.containsKey("title") ? request.get("title") : movie.getTitle());
        movie.setDuration(request.containsKey("duration") ? Integer.parseInt(request.get("duration")) : movie.getDuration());
        movie.setReleaseYear(request.containsKey("releaseYear") ? Integer.parseInt(request.get("releaseYear")) : movie.getReleaseYear());
        String ratingCategory = request.get("ratingCategory");
        if (ratingCategory != null && !ratingCategory.isEmpty()) {
            AgeRating rating = ageRatingRepo.findByRatingCategory(ratingCategory).orElse(null);
            if (rating == null)
                throw new NotFoundException("Рейтинг " + ratingCategory + " не найден!");
            movie.setAgeRating(rating);
        } else if (!request.containsKey("id"))
            throw new NotEnoughArgsException("Укажите возрастной рейтинг!");
        if (request.containsKey("description"))
            movie.setDescription(request.get("description"));
        if (request.containsKey("photo")) {
            //TODO photos
        }
        return movieRepo.save(movie);
    }
}
