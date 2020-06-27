package com.example.flixster.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Headers;

@Parcel
public class Movie implements Comparable<Movie> {

    // same for all movies
    static Map<Integer, String> genresMap = new HashMap<>();

    // individual movie properties
    String posterPath;
    String backdropPath;
    String title;
    String overview;
    double rating;
    String releaseDateString;
    Calendar releaseDate;
    int id;
    List<String> genres = new ArrayList<>();
    String genresString = "";

    public Movie() { }

    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getInt("vote_average");

        // release date
        releaseDate = stringToDate(jsonObject.getString("release_date"));
        releaseDateString = dateToString(releaseDate);

        id = jsonObject.getInt("id");

        // read in genres
        JSONArray genresJsonArray = jsonObject.getJSONArray("genre_ids");
        for (int i = 0; i < genresJsonArray.length(); i++) {
            int genreId = genresJsonArray.getInt(i);
            String genreName = genresMap.get(genreId);

            genres.add(genreName);
            genresString = genresString + genreName + ", ";
        }

        if (genresString.length() > 1) {
            genresString = genresString.substring(0, genresString.length() - 2);
        }

        // uncomment to check if genres correctly read in
        // Log.i("Movie", title + " " + genres);
        // Log.i("Movie", title + " " + genresString);

    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        // read in json arrays
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    // initialize genre array
    public static void initializeGenres() {
        genresMap.put(28, "Action");
        genresMap.put(12, "Adventure");
        genresMap.put(16, "Animation");
        genresMap.put(35, "Comedy");
        genresMap.put(80, "Crime");
        genresMap.put(99, "Documentary");
        genresMap.put(18, "Drama");
        genresMap.put(10751, "Family");
        genresMap.put(14, "Fantasy");
        genresMap.put(36, "History");
        genresMap.put(27, "Horror");
        genresMap.put(10402, "Music");
        genresMap.put(9648, "Mystery");
        genresMap.put(10749, "Romance");
        genresMap.put(878, "Science Fiction");
        genresMap.put(10770, "TV Movie");
        genresMap.put(53, "Thriller");
        genresMap.put(10752, "War");
        genresMap.put(37, "Western");
    }

    private static Calendar stringToDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(formatter.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c;
    }

    private static String dateToString(Calendar c) {
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        return month + " " + c.get(Calendar.DATE) + ", " + c.get(Calendar.YEAR);
    }

    // filter method
    public boolean isGenre(String genre) {
        return genres.contains(genre);
    }

    // getter methods
    public String getPosterPath() {
        // to test for placeholder image
        // return "";
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
        // to test for placeholder image
        // return "";
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDateString() {
        return releaseDateString;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public int getId() {
        return id;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getGenresString() {
        return genresString;
    }

    // sort by title, ascending
    @Override
    public int compareTo(Movie movie) {
        return (this.getTitle()).compareTo(movie.getTitle());
    }

    // sort by different features

    // sort by rating, descending
    public static class RatingCompare implements Comparator<Movie> {
        @Override
        public int compare(Movie m1, Movie m2) {
            if (m1.getRating() < m2.getRating()) {
                return 1;
            } else if (m1.getRating() > m2.getRating()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    // sort by date, descending
    public static class DateCompare implements Comparator<Movie> {
        @Override
        public int compare(Movie m1, Movie m2) {
            return (m1.getReleaseDate().compareTo(m2.getReleaseDate())) * -1;
        }
    }



}

