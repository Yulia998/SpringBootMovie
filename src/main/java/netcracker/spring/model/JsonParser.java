package netcracker.spring.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JsonParser {
    private static final Logger LOGGER = Logger.getLogger(JsonParser.class);

    public static Movie jsonParser(String json) {
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("Error")) {
            String error = jsonObject.getString("Error");
            LOGGER.warn(error);
            throw new IllegalArgumentException(error);
        }
        String title = jsonObject.getString("Title");
        int year = Integer.parseInt(jsonObject.getString("Year").substring(0, 4));
        String released = jsonObject.getString("Released");
        String runtime = jsonObject.getString("Runtime");
        String genre = jsonObject.getString("Genre");
        String director = jsonObject.getString("Director");
        String actors = jsonObject.getString("Actors");
        String plot = jsonObject.getString("Plot");
        String country = jsonObject.getString("Country");
        String poster = jsonObject.getString("Poster");
        String type = jsonObject.getString("Type");
        JSONArray ratings = jsonObject.getJSONArray("Ratings");
        List<Rating> ratingsList = new ArrayList<>();
        Rating rating;
        for (int i = 0; i < ratings.length(); i++) {
            rating = new Rating(ratings.getJSONObject(i).getString("Source"),
                    ratings.getJSONObject(i).getString("Value"));
            ratingsList.add(rating);
        }
        return new Movie(title, year, released, runtime, genre, director, actors, plot, country, poster, type, ratingsList);
    }
}
