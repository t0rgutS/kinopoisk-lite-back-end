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
                new User("96c8d7eb-3b36-47a6-99ee-19c04a70e728",
                        "test", encoder.encode("test"), "Тестер", "Тестовый",
                        false, User.Roles.ROLE_USER),
                new User("effc2403-9da5-4001-af5c-11906a7cdea6",
                        "moder", encoder.encode("moder"), "Злой", "Модератор",
                        false, User.Roles.ROLE_MODER),
                new User("868cecd8-77a7-4ca5-9c29-1139a73caa9e",
                        "admin", encoder.encode("admin"), "Очень Злой", "Админ",
                        false, User.Roles.ROLE_ADMIN)
        );
        userRepo.saveAll(initialUsers);

        List<AgeRating> initialRatings = Arrays.asList(
                new AgeRating("3726cef7-ebb2-445f-8d1b-2c649e43583f", "0+", 0),
                new AgeRating("84dbccdc-8fd7-4f9b-b960-ab3044bba6d7", "6+", 6),
                new AgeRating("9e25d9d6-36b4-4576-94c6-a5cd32a6fcec", "12+", 12),
                new AgeRating("128a4aa0-da3d-4ddb-982f-70fbd8923698", "16+", 16),
                new AgeRating("52fdb688-4a79-4cc7-8fea-2b84def1b4c6", "18+", 18)
        );
        ageRatingRepo.saveAll(initialRatings);

        List<Photo> initialPhotos = Arrays.asList(
                new Photo("alexei.jpg"),
                new Photo("test.png"),
                new Photo("starosta.jpg")
        );
        photoRepo.saveAll(initialPhotos);

        List<Movie> initialMovies = Arrays.asList(
                new Movie("b0425694-cf99-4a94-8782-2e541fcb350c",
                        "Test Movie 1", 2021, 190, "США", "боевик",
                        null, initialRatings.get(3), initialPhotos.get(0)),
                new Movie("d7ba9b91-f872-4583-ac0d-de78dd507567",
                        "Test Movie 2", 2019, 210, "Зимбабве", "драма",
                        "Тестовый фильм №2", initialRatings.get(2), initialPhotos.get(1)),
                new Movie("786f539e-3bc2-40cd-a3a0-a7f520a2c01c",
                        "Test Movie 3", 2020, 150, "Россия", "комедия",
                        null, initialRatings.get(4), initialPhotos.get(2))
        );
        movieRepo.saveAll(initialMovies);
    }
}