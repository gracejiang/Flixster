package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    List<Movie> movies = new ArrayList<>();

    EditText etSearch;
    Spinner spinner;
    RecyclerView rvMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        spinner = findViewById(R.id.filter);
        rvMovies = findViewById(R.id.rvMovies);


        createAdapter();

        // search feature

        // resets if edit text is empty
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSearch.getText().equals("")) {
                    createAdapter();
                    Toast.makeText(getApplicationContext(), "THIS WORKS!", Toast.LENGTH_SHORT).show();
                } else {
                    String phrase = etSearch.getText().toString();
                    List<Movie> searchResults = search(phrase);
                    updateAdapter(searchResults);
                    Toast.makeText(getApplicationContext(), phrase + " " + searchResults.size(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    // search for specific movie
    private List<Movie> search(String phrase) {
        List<Movie> searchResults = new ArrayList<>();
        for (Movie m : movies) {
            if (m.getTitle().toLowerCase().contains(phrase.toLowerCase())) {
                searchResults.add(m);
            }
        }
        return searchResults;
    }

    // create adapter to movies
    private void createAdapter() {

        movies = new ArrayList<>();

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
                    Log.i(TAG, "Results: " + results.toString());

                    // read in movies from json array to List<Movie> movies
                    movies.addAll(Movie.fromJsonArray(results));

                    // uncomment to check if movies properly read in
                    Log.i(TAG, "Movies: " + movies.size());

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

    private void updateAdapter(List<Movie> searchResults) {
        final MovieAdapter movieAdapter = new MovieAdapter(this, searchResults);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter.notifyDataSetChanged();
    }


}