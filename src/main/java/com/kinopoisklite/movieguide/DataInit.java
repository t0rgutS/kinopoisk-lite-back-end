package com.kinopoisklite.movieguide;

import com.kinopoisklite.movieguide.model.AgeRating;
import com.kinopoisklite.movieguide.model.Movie;
import com.kinopoisklite.movieguide.model.Photo;
import com.kinopoisklite.movieguide.model.User;
import com.kinopoisklite.movieguide.repository.AgeRatingRepository;
import com.kinopoisklite.movieguide.repository.MovieRepository;
import com.kinopoisklite.movieguide.repository.PhotoRepository;
import com.kinopoisklite.movieguide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements ApplicationRunner {
    private final MovieRepository movieRepo;
    private final AgeRatingRepository ageRatingRepo;
    private final UserRepository userRepo;
    private final PhotoRepository photoRepo;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<User> initialUsers = Arrays.asList(
                new User("test", encoder.encode("test"), "Тестер", "Тестовый",
                        false, User.Roles.ROLE_USER),
                new User("moder", encoder.encode("moder"), "Злой", "Модератор",
                        false, User.Roles.ROLE_MODER),
                new User("admin", encoder.encode("admin"), "Очень Злой", "Админ",
                        false, User.Roles.ROLE_ADMIN)
        );
        userRepo.saveAll(initialUsers);

        List<AgeRating> initialRatings = Arrays.asList(
                new AgeRating("0+", 0),
                new AgeRating("6+", 6),
                new AgeRating("12+", 12),
                new AgeRating("16+", 16),
                new AgeRating("18+", 18)
        );
        ageRatingRepo.saveAll(initialRatings);

        List<Photo> initialPhotos = Arrays.asList(
                new Photo("alexei.jpg"),
                new Photo("test.png"),
                new Photo("starosta.jpg")
        );
        photoRepo.saveAll(initialPhotos);

        List<Movie> initialMovies = Arrays.asList(
                new Movie("Test Movie 1", 2021, 190, "США", "боевик",
                        null, initialRatings.get(3), initialPhotos.get(0)),
                new Movie("Test Movie 2", 2019, 210, "Зимбабве", "драма",
                        "Тестовый фильм №2", initialRatings.get(2), initialPhotos.get(1)),
                new Movie("Test Movie 3", 2020, 150, "Россия", "комедия",
                        null, initialRatings.get(4), initialPhotos.get(2))
        );
        movieRepo.saveAll(initialMovies);
    }
}