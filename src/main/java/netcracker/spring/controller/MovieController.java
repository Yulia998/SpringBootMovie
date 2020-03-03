package netcracker.spring.controller;

import netcracker.spring.model.Document;
import netcracker.spring.model.Movie;
import netcracker.spring.service.SiteService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class MovieController {
    private final SiteService service;
    private Document document;

    public MovieController(SiteService service, Document document) {
        this.service = service;
        this.document = document;
    }

    @RequestMapping(value = "/movie", params = "name")
    public ResponseEntity<?> getMovieByName(@RequestParam(name = "name") String name,
                                            @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) throws IOException, InvalidFormatException {
        Movie movie = service.getMovieByName(name);
        return chooseFormat(movie, mediaType);
    }

    @RequestMapping(value = "/movie", params = "id")
    public ResponseEntity<?> getMovieById(@RequestParam(name = "id") String id,
                                          @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) throws IOException, InvalidFormatException {
        Movie movie = service.getMovieById(id);
        return chooseFormat(movie, mediaType);
    }

    @RequestMapping(value = "/movie", params = "listName")
    public ResponseEntity<?> getMovieList(@RequestParam(name = "listName") List<String> listName,
                                          @RequestParam(name = "mediaType", defaultValue = "json") String mediaType) throws Exception {
        List<Movie> movies = service.getMovieList(listName);
        return chooseFormat(movies, mediaType);
    }

    private ResponseEntity<?> chooseFormat(Object object, String mediaType) throws IOException, InvalidFormatException {
        String type = MediaType.APPLICATION_JSON_VALUE;
        if (mediaType.equals("doc")) {
            File file;
            if (object instanceof Movie) {
                file = document.createMovieDoc((Movie) object);
            } else {
                file = document.createMovieDoc((List<Movie>) object);
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                    .contentLength(file.length())
                    .body(resource);
        }
        if (mediaType.equals("xml")) {
            type = MediaType.APPLICATION_XML_VALUE;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, type)
                .body(object);
    }
}
