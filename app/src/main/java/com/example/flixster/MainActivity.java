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
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    List<Movie> movies = new ArrayList<>();

    EditText etSearch;
    Spinner spinnerSort;
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
        spinnerSort.setOnItemSelectedListener(this);

        createMovieAdapter();
        createSortAdapter();

        // search feature

        // resets if edit text is empty, otherwise call search function automatically
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // user reset their search query, so bring all results back to screen
                if (etSearch.getText().equals("") && movies.size() > 0) {
                    updateMovieAdapter(movies);
                } else if (movies.size() > 0) {
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
        for (Movie m : movies) {
            if (m.getTitle().toLowerCase().contains(phrase)) {
                searchResults.add(m);
            }
        }
        return searchResults;
    }

    // import all movie results from api and create a new adapter
    private void createMovieAdapter() {
        movies = new ArrayList<>();
        Movie.initializeGenres();

        // create the adapter
        final MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        // set adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");

                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    // Log.i(TAG, "Results: " + results.toString());

                    // read in movies from json array to List<Movie> movies
                    movies.addAll(Movie.fromJsonArray(results));

                    // uncomment to check if movies properly read in
                    // Log.i(TAG, "Movies: " + movies.size());

                    // sort movies
                    Collections.sort(movies);

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
    }

    // sort-by item selections
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // sort by title A-Z
        if (position == 0 && movies.size() > 0) {
            Collections.sort(movies);
            updateMovieAdapter(movies);
        }

        // sort by rating
        else if (position == 1 && movies.size() > 0) {
            Movie.RatingCompare ratingCompare = new Movie.RatingCompare();
            Collections.sort(movies, ratingCompare);
            updateMovieAdapter(movies);
        }

        // sort by date
        else if (position == 2 && movies.size() > 0) {
            Movie.DateCompare dateCompare = new Movie.DateCompare();
            Collections.sort(movies, dateCompare);
            updateMovieAdapter(movies);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}