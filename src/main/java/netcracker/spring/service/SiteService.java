package netcracker.spring.service;

import netcracker.spring.model.Movie;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SiteService {
    Movie getMovieByName(String name);

    Movie getMovieById(String id);

    List<Movie> getMovieList(List<String> listName) throws InterruptedException, ExecutionException;
}
