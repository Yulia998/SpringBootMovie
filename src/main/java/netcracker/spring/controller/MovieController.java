package netcracker.spring.controller;

import netcracker.spring.model.Movie;
import netcracker.spring.service.SiteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {
    private final SiteService service;

    public MovieController(SiteService service) {
        this.service = service;
    }

    @RequestMapping(value = "/movie", params = "name")
    public ResponseEntity<?> getMovieByName(@RequestParam(name = "name") String name,
                                            @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) {
        Movie movie = service.getMovieByName(name);
        return chooseFormat(movie, mediaType);
    }

    @RequestMapping(value = "/movie", params = "id")
    public ResponseEntity<?> getMovieById(@RequestParam(name = "id") String id,
                                          @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) {
        Movie movie = service.getMovieById(id);
        return chooseFormat(movie, mediaType);
    }

    @RequestMapping(value = "/movie", params = "listName")
    public ResponseEntity<?> getMovieList(@RequestParam(name = "listName") List<String> listName,
                                          @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) {
        List<Movie> movies = service.getMovieList(listName);
        return chooseFormat(movies, mediaType);
    }

    private ResponseEntity<?> chooseFormat(Object object, String mediaType) {
        String type = MediaType.APPLICATION_JSON_VALUE;
        if (mediaType.equals("xml")) {
            type = MediaType.APPLICATION_XML_VALUE;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, type)
                .body(object);
    }
}
