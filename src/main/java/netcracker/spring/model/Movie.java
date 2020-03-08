package netcracker.spring.model;

import java.util.List;

public class Movie {
    private String title;
    private int year;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String actors;
    private String plot;
    private String country;
    private String poster;
    private String type;
    private List<Rating> ratings;

    public Movie(String title, int year, String released, String runtime,
                 String genre, String director, String actors, String plot,
                 String country, String poster, String type, List<Rating> ratings) {
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.actors = actors;
        this.plot = plot;
        this.country = country;
        this.poster = poster;
        this.type = type;
        this.ratings = ratings;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public String getCountry() {
        return country;
    }

    public String getPoster() {
        return poster;
    }

    public String getType() {
        return type;
    }

    public List<Rating> getRatings() {
        return ratings;
    }
}
