package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieBinding;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieActivity extends YouTubeBaseActivity {

    Movie movie;

    TextView tvTitle;
    TextView tvDate;
    TextView tvGenres;
    RatingBar rbRating;
    TextView tvOverview;
    YouTubePlayerView playerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieBinding binding = ActivityMovieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        // getSupportActionBar().setTitle(movie.getTitle());

        tvTitle = findViewById(R.id.movie_details_title);
        tvDate = findViewById(R.id.movie_details_date);
        tvGenres = findViewById(R.id.movie_details_genres);
        rbRating = findViewById(R.id.movie_details_rating);
        tvOverview = findViewById(R.id.movie_details_overview);
        playerView = findViewById(R.id.movie_details_player);

        tvTitle.setText(movie.getTitle());
        tvDate.setText("Release Date: " + movie.getReleaseDateString());
        tvGenres.setText(movie.getGenresString());
        rbRating.setRating((float) (movie.getRating() / 2.0));
        tvOverview.setText(movie.getOverview());

        readInVideoUrl(movie);
    }

    private void readInVideoUrl(Movie movie) {
        final String TAG = "MovieActivity";
        final String apiKey = getString(R.string.default_api_key);
        final String movieId = "" + movie.getId();
        final String API_URL = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + apiKey + "&language=en-US";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");

                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    if (results.length() > 0) {
                        JSONObject firstUrl = results.getJSONObject(0);
                        String videoKey = firstUrl.getString("key");
                        initializeYouTubeView(videoKey);
                    } else {
                        hideYouTubeView();
                    }
                    Log.i(TAG, jsonObject.toString());
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

    private void initializeYouTubeView(final String videoKey) {
        playerView.initialize(getString(R.string.default_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(videoKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }

    private void hideYouTubeView() {
        playerView.setVisibility(View.GONE);
    }
}