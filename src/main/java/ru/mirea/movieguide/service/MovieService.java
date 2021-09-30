package ru.mirea.movieguide.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import ru.mirea.movieguide.exception.NotEnoughArgsException;
import ru.mirea.movieguide.exception.NotFoundException;
import ru.mirea.movieguide.exception.PersistenceException;
import ru.mirea.movieguide.model.AgeRating;
import ru.mirea.movieguide.model.Movie;
import ru.mirea.movieguide.model.Photo;
import ru.mirea.movieguide.repository.AgeRatingRepository;
import ru.mirea.movieguide.repository.MovieRepository;
import ru.mirea.movieguide.repository.PhotoRepository;

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
    private final PhotoRepository photoRepo;

    public List<Movie> getAll() {
        List<Movie> movies = movieRepo.findAll();
        movies.forEach(movie -> loadPoster(movie));
        return movies;
    }

    public Movie get(String id) throws PersistenceException {
        Movie movie = movieRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Фильм с идентификатором " + id + " не найден!"));
        loadPoster(movie);
        return movie;
    }

    @Secured("ADMIN")
    public Movie upsert(Map<String, Object> request) throws PersistenceException {
        Movie movie;
        if (request.containsKey("id")) {
            try {
                movie = movieRepo.getById(request.get("id").toString());
            } catch (EntityNotFoundException enf) {
                throw new NotFoundException("Фильм с идентификатором " + request.get("id") + " не найден!");
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
            if (rating == null && !request.containsKey("id"))
                throw new NotFoundException("Рейтинг " + ratingCategory + " не найден!");
            if (rating != null)
                movie.setAgeRating(rating);
        }
        if (request.containsKey("description"))
            movie.setDescription(request.get("description").toString());
        if (request.containsKey("photo")) {
            Map<String, String> photo = (Map<String, String>) request.get("photo");
            if (photo.containsKey("fileContent")) {
                String filename = photo.containsKey("fileName")
                        ? photo.get("fileName")
                        : String.format("image_%s.png", LocalDateTime.now().toString());
                String path = String.format("%s/resources/static/images/%s", System.getProperty("user.dir"), filename);
                Photo storedPhoto = photoRepo.findByPhotoUri(path).orElse(null);
                if (storedPhoto == null)
                    storedPhoto = new Photo();
                byte[] fileContent = Base64.getDecoder().decode(photo.get("fileContent"));
                try {
                    new FileOutputStream(path).write(fileContent);
                    storedPhoto.setPhotoUri(path);
                    photoRepo.save(storedPhoto);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new PersistenceException("Ошибка сохранения постера: " + e.getMessage());
                }
            } else throw new NotEnoughArgsException("Приложите постер!");
        }
        return movieRepo.save(movie);
    }

    private void loadPoster(Movie movie) {
        if (movie.getPhoto() != null) {
            try {
                byte[] fileContent = Files.readAllBytes(Paths.get(movie.getPhoto().getPhotoUri()));
                movie.getPhoto().setPhotoContent(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
