package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieActivity extends AppCompatActivity {

    Movie movie;

    TextView tvTitle;
    TextView tvDate;
    RatingBar rbRating;
    TextView tvOverview;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        getSupportActionBar().setTitle(movie.getTitle());

        tvTitle = findViewById(R.id.movie_details_title);
        tvDate = findViewById(R.id.movie_details_date);
        rbRating = findViewById(R.id.movie_details_rating);
        tvOverview = findViewById(R.id.movie_details_overview);
        videoView = findViewById(R.id.movie_details_video);

        tvTitle.setText(movie.getTitle());
        tvDate.setText("Relase Date: " + movie.getReleaseDateString());
        rbRating.setRating((float) (movie.getRating() / 2.0));
        tvOverview.setText(movie.getOverview());

    }
}