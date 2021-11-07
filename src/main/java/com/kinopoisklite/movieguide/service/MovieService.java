package com.kinopoisklite.movieguide.service;

import com.kinopoisklite.movieguide.exception.NotEnoughArgsException;
import com.kinopoisklite.movieguide.exception.NotFoundException;
import com.kinopoisklite.movieguide.exception.PersistenceException;
import com.kinopoisklite.movieguide.model.AgeRating;
import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.Photo;
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
        return movieRepo.findAll();
    }

    public Movie findById(String id) throws PersistenceException {
        Movie movie = movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Фильм с идентификатором " + id + " не найден!"));
        loadPoster(movie);
        return movie;
    }

    public List<Movie> findByIds(List<String> ids) {
        return movieRepo.findByIdIn(ids);
    }

    public void delete(String id) throws PersistenceException {
        if (id == null)
            throw new NotEnoughArgsException("Укажите идентификатор фильма!");
        if (id.isEmpty())
            throw new NotEnoughArgsException("Укажите идентификатор фильма!");
        Movie movie = movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Фильм с идентификатором " + id + " не найден!"));
        movieRepo.delete(movie);
    }

    public Movie upsert(String id, Map<String, Object> request) throws PersistenceException {
        Movie movie;
        if (id != null) {
            try {
                movie = movieRepo.getById(id);
            } catch (EntityNotFoundException enf) {
                throw new NotFoundException("Фильм с идентификатором " + id + " не найден!");
            }
        } else {
            if (!request.containsKey("title"))
                throw new NotEnoughArgsException("Укажите название фильма!");
            if (!request.containsKey("duration"))
                throw new NotEnoughArgsException("Укажите продолжительность фильма!");
            if (!request.containsKey("releaseYear"))
                throw new NotEnoughArgsException("Укажите год выхода фильма!");
            if (!request.containsKey("ratingCategory"))
                throw new NotEnoughArgsException("Укажите возрастной рейтинг!");
            movie = new Movie();
        }
        if (request.containsKey("title"))
            movie.setTitle(request.get("title").toString());
        if (request.containsKey("duration"))
            movie.setDuration(Integer.parseInt(request.get("duration").toString()));
        if (request.containsKey("releaseYear"))
            movie.setReleaseYear(Integer.parseInt(request.get("releaseYear").toString()));
        if (request.containsKey("ratingCategory")) {
            String ratingCategory = request.get("ratingCategory").toString();
            AgeRating rating = ageRatingRepo.findByRatingCategory(ratingCategory).orElse(null);
            if (rating == null && id == null)
                throw new NotFoundException("Рейтинг " + ratingCategory + " не найден!");
            if (rating != null)
                movie.setAgeRating(rating);
        }
        if (request.containsKey("description"))
            movie.setDescription(request.get("description").toString());
        if (request.containsKey("cover")) {
            Map<String, String> coverData = (Map<String, String>) request.get("cover");
            if (coverData.containsKey("fileContent")) {
                String path;
                if (coverData.containsKey("fileName")) {
                    path = String.format("%s/src/main/resources/static/images/%s",
                            System.getProperty("user.dir"),
                            coverData.get("fileName"));
                    if (coverRepo.findByPhotoUri(path).orElse(null) != null) {
                        path = String.format("%s/src/main/resources/static/images/%s",
                                System.getProperty("user.dir"),
                                String.format("image_%s.png", LocalDateTime.now().toString()));
                    }
                } else
                    path = String.format("%s/src/main/resources/static/images/%s",
                            System.getProperty("user.dir"),
                            String.format("image_%s.png", LocalDateTime.now().toString()));
                Photo cover = new Photo();
                byte[] fileContent = Base64.getDecoder().decode(coverData.get("fileContent"));
                try {
                    new FileOutputStream(path).write(fileContent);
                    cover.setPhotoUri(path);
                    coverRepo.save(cover);
                    movie.setCover(cover);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new PersistenceException("Ошибка сохранения постера: " + e.getMessage());
                }
            } else throw new NotEnoughArgsException("Приложите постер!");
        }
        return movieRepo.save(movie);
    }

    private void loadPoster(Movie movie) {
        if (movie.getCover() != null) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(movie.getCover().getPhotoUri()));
                movie.getCover().setPhotoContent(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
