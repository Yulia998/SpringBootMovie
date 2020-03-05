package netcracker.spring.service;

import netcracker.spring.model.JsonParser;
import netcracker.spring.model.Movie;
import org.apache.log4j.Logger;
import org.json.JSONObject;
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

    public List<Movie> getMovieListBySearch(String name, int amount) throws ExecutionException, InterruptedException {
        ResponseEntity<String> response;
        int i = 0;
        List<String> responseList = new ArrayList<>();
        while (true) {
            String urlStr = serviceUrl.cloneBuilder().queryParam("s", name)
                    .queryParam("page", ++i).build().toString();
            response = restTemplate.getForEntity(urlStr, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody());
            if (jsonObject.has("Error") || responseList.size() >= amount / 10.0) {
                break;
            } else {
                responseList.add(response.getBody());
            }
        }
        List<String> listId = JsonParser.jsonListParser(responseList, amount);
        return findMovies(listId, "id");
    }

    public List<Movie> findMovies(List<String> nameId, String type) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(amountThread);
        CompletionService<Movie> completionService = new ExecutorCompletionService<>(executorService);
        List<Movie> movies = new ArrayList<>();
        Future<Movie> submit;
        for (String movie : nameId) {
            if (type.equals("name")) {
                submit = completionService.submit(() -> getMovieByName(movie));
            } else {
                submit = completionService.submit(() -> getMovieById(movie));
            }
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
