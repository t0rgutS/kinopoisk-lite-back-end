import com.kinopoisklite.movieguide.MovieGuideApplication;
import com.kinopoisklite.movieguide.model.Movie;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MovieGuideApplication.class)
@TestPropertySource(locations = "classpath:application.yml")
public class ModelBasedTest {
    private RestTemplate restTemplate = new RestTemplate();

    private Movie[] getMovies() {
        try {
            ResponseEntity<Movie[]> response = restTemplate
                    .exchange("http://localhost:8080/api/movies", HttpMethod.GET, null, Movie[].class);
            return response.getBody();
        } catch (HttpStatusCodeException hsce) {
            System.out.println(String.format("GET ERROR!\nStatus: %s\nResponse: %s",
                    hsce.getStatusCode().value(), hsce.getResponseBodyAsString()));
        }
        return new Movie[]{};
    }

    private Movie createTestMovie(String token) {
        String requestBody = "{\n" +
                "\t\"title\": \"Test\",\n" +
                "\t\"releaseYear\": 2019,\n" +
                "\t\"duration\": 190,\n" +
                "\t\"ratingCategory\": \"18+\"\n" +
                "}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity(requestBody, headers);
        try {
            ResponseEntity<Movie> response = restTemplate
                    .exchange("http://localhost:8080/api/movies", HttpMethod.POST, request, Movie.class);
            return response.getBody();
        } catch (HttpStatusCodeException hsce) {
            System.out.println(String.format("POST ERROR!\nStatus: %s\nResponse: %s",
                    hsce.getStatusCode().value(), hsce.getResponseBodyAsString()));
        }
        return null;
    }

    private boolean deleteTestMovie(String token, String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity(headers);
        try {
            ResponseEntity<Movie> response = restTemplate
                    .exchange("http://localhost:8080/api/movies/" + id, HttpMethod.DELETE, request, Movie.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpStatusCodeException hsce) {
            System.out.println(String.format("DELETE ERROR!\nStatus: %s\nResponse: %s",
                    hsce.getStatusCode().value(), hsce.getResponseBodyAsString()));
        }
        return false;
    }

    private String getToken(String login, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString(String.format("%s:%s", login, password).getBytes()));
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity(headers);
        try {
            ResponseEntity<Map> response = restTemplate
                    .exchange("http://localhost:8080/api/auth", HttpMethod.POST, request, Map.class);
            return (String) response.getBody().get("token");
        } catch (HttpStatusCodeException hsce) {
            System.out.println(String.format("AUTH ERROR!\nStatus: %s\nResponse: %s",
                    hsce.getStatusCode().value(), hsce.getResponseBodyAsString()));
        }
        return null;
    }

    @Test
    public void testMovieCreation() {
        String state = "GET";
        String token = null;
        String testMovieId = null;
        do {
            switch (state) {
                case "GET": {
                    Movie[] movies = getMovies();
                    Movie testMovie = Arrays.stream(movies).filter(movie -> movie.getTitle()
                            .equals("Test")).findFirst().orElse(null);
                    if (testMovie != null) {
                        testMovieId = testMovie.getId();
                        state = "DELETE";
                    } else
                        state = "POST";
                }
                break;
                case "POST": {
                    if (token == null)
                        state = "AUTH";
                    else {
                        Movie movie = createTestMovie(token);
                        if (movie == null)
                            state = "ERROR";
                        else
                            state = "GET";
                    }
                }
                break;
                case "AUTH": {
                    token = getToken("admin", "admin");
                    if (token == null)
                        state = "ERROR";
                    else
                        state = "POST";
                }
                break;
                case "DELETE": {
                    state = deleteTestMovie(token, testMovieId) ? "OK" : "ERROR";
                }
                break;
            }
        } while (!state.equals("OK") && !state.equals("ERROR"));
        assertTrue(state.equals("OK"));
    }
}
