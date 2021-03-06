package netcracker.spring.controller;

import netcracker.spring.model.Document;
import netcracker.spring.model.Movie;
import netcracker.spring.service.SiteService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class MovieController {
    private final SiteService service;
    private Document document;

    public MovieController(SiteService service, Document document) {
        this.service = service;
        this.document = document;
    }

    @RequestMapping(value = "/movie", params = "name")
    public ResponseEntity<?> getMovieByName(@RequestParam(name = "name") String name) {
        Movie movie = service.getMovieByName(name);
        return ResponseEntity.ok(movie);
    }

    @RequestMapping(value = "/movie", params = "id")
    public ResponseEntity<?> getMovieById(@RequestParam(name = "id") String id) {
        Movie movie = service.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @RequestMapping(value = "/movie", params = "listName")
    public ResponseEntity<?> getMovieList(@RequestParam(name = "listName") List<String> listName) throws ExecutionException, InterruptedException {
        List<Movie> movies = service.findMovies(listName, "name");
        return ResponseEntity.ok(movies);
    }

    @RequestMapping(value = "/movie", params = "search")
    public ResponseEntity<?> getMovieBySearch(@RequestParam(name = "search") String search,
                                              @RequestParam(name = "amount", defaultValue = "10") int amount) throws ExecutionException, InterruptedException {
        List<Movie> movies = service.getMovieListBySearch(search, amount);
        return ResponseEntity.ok(movies);
    }

    @RequestMapping(value = "/doc", params = "listName")
    public ResponseEntity<?> getDoc(@RequestParam(name = "listName") List<String> listName) throws Exception {
        List<Movie> movies = service.findMovies(listName, "name");
        byte[] byteArray = document.createMovieDoc(movies);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArray));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=movies.docx")
                .body(resource);
    }
}
