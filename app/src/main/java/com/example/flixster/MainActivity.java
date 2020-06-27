package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    Map<Integer, String> genresMap = new HashMap<>();
    List<Movie> allMovies = new ArrayList<>();
    List<Movie> currMovies = new ArrayList<>();

    int sortBy;
    int filterBy;

    EditText etSearch;
    Spinner spinnerSort;
    Spinner spinnerGenres;
    RecyclerView rvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etSearch = findViewById(R.id.etSearch);
        spinnerSort = findViewById(R.id.spinnerSort);
        rvMovies = findViewById(R.id.rvMovies);
        spinnerGenres = findViewById(R.id.spinnerGenres);

        createMovieAdapter();
        createSortAdapter();
        createGenreAdapter();

        // search feature

        // resets if edit text is empty, otherwise call search function automatically
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // user reset their search query, so bring all results back to screen
                if (etSearch.getText().equals("") && currMovies.size() > 0) {
                    updateMovieAdapter(currMovies);
                } else if (currMovies.size() > 0) {
                    // user searches for a query
                    String phrase = etSearch.getText().toString().toLowerCase();
                    List<Movie> searchResults = search(phrase);
                    updateMovieAdapter(searchResults);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "NOW LANDSCAPE", Toast.LENGTH_LONG);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "NOW PORTRAIT", Toast.LENGTH_LONG);
        }
    }

    // search for specific movie
    private List<Movie> search(String phrase) {
        List<Movie> searchResults = new ArrayList<>();
        for (Movie m : currMovies) {
            if (m.getTitle().toLowerCase().contains(phrase)) {
                searchResults.add(m);
            }
        }
        return searchResults;
    }

    // import all movie results from api and create a new adapter
    private void createMovieAdapter() {
        allMovies = new ArrayList<>();
        Movie.initializeGenres();

        // create the adapter
        final MovieAdapter movieAdapter = new MovieAdapter(this, allMovies);

        // set adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    // Log.i(TAG, "Results: " + results.toString());

                    // read in movies from json array to List<Movie> movies
                    allMovies.addAll(Movie.fromJsonArray(results));

                    // uncomment to check if movies properly read in
                    // Log.i(TAG, "Movies: " + movies.size());

                    // sort movies
                    Collections.sort(allMovies);
                    currMovies = allMovies;
                    sortBy();
                    filterBy();
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

    }

    // update adapter given a list of movies
    private void updateMovieAdapter(List<Movie> moviesList) {
        final MovieAdapter movieAdapter = new MovieAdapter(this, moviesList);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter.notifyDataSetChanged();
    }

    // create adapter for sort-by options
    private void createSortAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
        spinnerSort.setOnItemSelectedListener(new SortSpinnerClass());
    }

    private void createGenreAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genre_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenres.setAdapter(adapter);
        spinnerGenres.setOnItemSelectedListener(new GenreSpinnerClass());
    }

    // handles sort spinner
    class SortSpinnerClass implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            sortBy = position;

            // check that json has already been read in
            if (allMovies.size() > 0) {
                sortBy();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    // sort by whatever sort is selected
    private void sortBy() {
        if (sortBy == 0) {
            Collections.sort(currMovies);
            updateMovieAdapter(currMovies);
        }

        // sort by rating
        else if (sortBy == 1) {
            Movie.RatingCompare ratingCompare = new Movie.RatingCompare();
            Collections.sort(currMovies, ratingCompare);
            updateMovieAdapter(currMovies);
        }

        // sort by date
        else if (sortBy == 2) {
            Movie.DateCompare dateCompare = new Movie.DateCompare();
            Collections.sort(currMovies, dateCompare);
            updateMovieAdapter(currMovies);
        }
    }

    // filter by whatever is selected
    private void filterBy() {
        // check json already read in
        if (allMovies.size() <= 0) {
            return;
        }

        // filter by genre
        if (filterBy > 0) {
            String genre = genresMap.get(filterBy);
            List<Movie> filteredMovies = new ArrayList<>();
            for (Movie m : allMovies) {
                if (m.isGenre(genre)) {
                    filteredMovies.add(m);
                }
            }
            currMovies = filteredMovies;
            sortBy();
            updateMovieAdapter(currMovies);
        } else if (filterBy == 0) {
            currMovies = allMovies;
            updateMovieAdapter(currMovies);
        }
    }


    // handles genres spinner
    class GenreSpinnerClass implements AdapterView.OnItemSelectedListener {

        public GenreSpinnerClass() {
            String[] rawArray = {"", "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
                    "Drama", "Family", "Fantasy", "History",  "Horror", "Music", "Mystery", "Romance",
                    "Science Fiction", "TV Movie", "Thriller", "War", "Western"};

            for (int i = 0; i < rawArray.length; i++) {
                genresMap.put(i, rawArray[i]);
            }

        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            filterBy = position;
            filterBy();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}