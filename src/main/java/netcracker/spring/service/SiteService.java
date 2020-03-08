package netcracker.spring.service;

import netcracker.spring.model.Movie;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SiteService {
    Movie getMovieByName(String name);

    Movie getMovieById(String id);

    List<Movie> findMovies(List<String> nameId, String type) throws InterruptedException, ExecutionException;

    List<Movie> getMovieListBySearch(String name, int amount) throws ExecutionException, InterruptedException;
}
