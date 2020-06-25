package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Movie implements Comparable<Movie> {

    String posterPath;
    String backdropPath;
    String title;
    String overview;
    double rating;
    String releaseDateString;
    Date releaseDate;


    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getInt("vote_average");
        releaseDateString = jsonObject.getString("release_date");
        releaseDate = stringToDate(releaseDateString);
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    private static Date stringToDate(String dateStr) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = formatter.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // getter methods
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
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

    public Date getReleaseDate() {
        return releaseDate;
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

