package netcracker.spring.service;

import netcracker.spring.model.JsonParser;
import netcracker.spring.model.Movie;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class MovieService implements SiteService {
    private static final Logger LOGGER = Logger.getLogger(MovieService.class);
    private final UriComponentsBuilder serviceUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private int amountThread;

    public MovieService(@Value("${apiKey}") String key, @Value("${amountStream}") int amountThread) {
        this.serviceUrl = UriComponentsBuilder.fromHttpUrl("http://www.omdbapi.com/")
                .queryParam("apikey", key);
        this.amountThread = amountThread;
    }

    public Movie getMovieByName(String name) {
        String urlStr = serviceUrl.cloneBuilder().queryParam("t", name).build().toString();
        ResponseEntity<String> response = restTemplate.getForEntity(urlStr, String.class);
        Movie movie = JsonParser.jsonParser(response.getBody());
        return movie;
    }

    public Movie getMovieById(String id) {
        String urlStr = serviceUrl.cloneBuilder().queryParam("i", id).build().toString();
        ResponseEntity<String> response = restTemplate.getForEntity(urlStr, String.class);
        Movie movie = JsonParser.jsonParser(response.getBody());
        return movie;
    }

    public List<Movie> getMovieList(List<String> listName) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(amountThread);
        CompletionService<Movie> completionService = new ExecutorCompletionService<>(executorService);
        List<Movie> movies = new ArrayList<>();
        for (String movieName : listName) {
            Future<Movie> submit = completionService.submit(() -> getMovieByName(movieName));
            try {
                movies.add(submit.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e);
                throw e;
            }
        }
        return movies;
    }
}
