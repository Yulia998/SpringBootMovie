package netcracker.spring.service;

import netcracker.spring.model.JsonParser;
import netcracker.spring.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class MovieService implements SiteService {
    private final String serviceUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private int amountThread;

    public MovieService(@Value("${apiKey}") String key, @Value("${amountStream}") int amountThread) {
        this.serviceUrl = "http://www.omdbapi.com/?apikey=" + key + "&";
        this.amountThread = amountThread;
    }

    public Movie getMovieByName(String name) {
        String url = serviceUrl + "t=" + name;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Movie movie = JsonParser.jsonParser(response.getBody());
        return movie;
    }

    public Movie getMovieById(String id) {
        String url = serviceUrl + "i=" + id;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        Movie movie = JsonParser.jsonParser(response.getBody());
        return movie;
    }

    public List<Movie> getMovieList(List<String> listName) {
        ExecutorService executorService = Executors.newFixedThreadPool(amountThread);
        CompletionService<Movie> completionService = new ExecutorCompletionService<>(executorService);
        List<Movie> movies = new ArrayList<>();
        for (String movieName : listName) {
            Future<Movie> submit = completionService.submit(() -> getMovieByName(movieName));
            try {
                movies.add(submit.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return movies;
    }
}
