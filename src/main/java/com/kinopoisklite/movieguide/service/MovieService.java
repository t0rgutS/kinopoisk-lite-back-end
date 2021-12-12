package com.kinopoisklite.movieguide.service;

import com.kinopoisklite.movieguide.exception.NotEnoughArgsException;
import com.kinopoisklite.movieguide.exception.NotFoundException;
import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.AgeRating;
import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.Photo;
import com.kinopoisklite.movieguide.model.dto.MovieDTO;
import com.kinopoisklite.movieguide.repository.AgeRatingRepository;
import com.kinopoisklite.movieguide.repository.MovieRepository;
import com.kinopoisklite.movieguide.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MovieService {
    private final MovieRepository movieRepo;
    private final AgeRatingRepository ageRatingRepo;
    private final PhotoRepository coverRepo;

    public List<Movie> findAll() {
        List<Movie> movies = movieRepo.findAll();
        movies.forEach(this::loadPoster);
        return movies;
    }

    public Movie findById(String id) throws PersistenceException {
        Movie movie = movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Movie " + id + " not found!"));
        loadPoster(movie);
        return movie;
    }

    public List<Movie> findByIds(List<String> ids) {
        List<Movie> movies = movieRepo.findByIdIn(ids);
        movies.forEach(this::loadPoster);
        return movies;
    }

    public void delete(String id) throws PersistenceException {
        if (id == null)
            throw new NotEnoughArgsException("Movie id not present!");
        if (id.isEmpty())
            throw new NotEnoughArgsException("Movie id is empty!");
        Movie movie = movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Movie " + id + " not found!"));
        movieRepo.delete(movie);
    }

    public Movie upsert(String id, MovieDTO request) throws PersistenceException {
        Movie movie;
        if (id != null) {
            try {
                movie = movieRepo.getById(id);
            } catch (EntityNotFoundException enf) {
                throw new NotFoundException("Movie " + id + " not found!");
            }
        } else
            movie = new Movie();
        if (request.getTitle() != null)
            movie.setTitle(request.getTitle());
        if (request.getDuration() != null)
            movie.setDuration(request.getDuration());
        if (request.getCountry() != null)
            movie.setCountry(request.getCountry());
        if (request.getGenre() != null)
            movie.setGenre(request.getGenre());
        if (request.getReleaseYear() != null)
            movie.setReleaseYear(request.getReleaseYear());
        if (request.getRatingCategory() != null) {
            AgeRating rating = ageRatingRepo.findByRatingCategory(request.getRatingCategory()).orElse(null);
            if (rating == null && id == null)
                throw new NotFoundException("Рейтинг " + request.getRatingCategory() + " не найден!");
            if (rating != null)
                movie.setAgeRating(rating);
        }
        if (request.getDescription() != null)
            movie.setDescription(request.getDescription());
        if (request.getCover() != null) {
            MovieDTO.Cover coverData = request.getCover();
            if (coverData.getContent() != null) {
                String path;
                if (coverData.getFileName() != null) {
                    path = String.format("%s/src/main/resources/static/images/%s",
                            System.getProperty("user.dir"),
                            coverData.getFileName());
                    if (coverRepo.findByUri(path).orElse(null) != null) {
                        path = String.format("%s/src/main/resources/static/images/%s",
                                System.getProperty("user.dir"),
                                String.format("image_%s.png", Instant.now().toEpochMilli()));
                    }
                } else
                    path = String.format("%s/src/main/resources/static/images/%s",
                            System.getProperty("user.dir"),
                            String.format("image_%s.png", Instant.now().toEpochMilli()));
                Photo cover = new Photo();
                byte[] fileContent = Base64.getDecoder().decode(coverData.getContent());
                try {
                    new FileOutputStream(path).write(fileContent);
                    cover.setUri(path);
                    cover.setContent(coverData.getContent());
                    coverRepo.save(cover);
                    movie.setCover(cover);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new PersistenceException("Ошибка сохранения постера: " + e.getMessage());
                }
            } else throw new NotEnoughArgsException("Приложите постер!");
        }
        movieRepo.save(movie);
        return movie;
    }

    private void loadPoster(Movie movie) {
        if (movie.getCover() != null) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(movie.getCover().getUri()));
                movie.getCover().setContent(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
