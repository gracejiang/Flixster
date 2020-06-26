package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.flixster.databinding.ActivityMovieBinding;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieActivity extends AppCompatActivity {

    Movie movie;

    TextView tvTitle;
    TextView tvDate;
    TextView tvGenres;
    RatingBar rbRating;
    TextView tvOverview;
    VideoView videoView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieBinding binding = ActivityMovieBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        getSupportActionBar().setTitle(movie.getTitle());

        tvTitle = findViewById(R.id.movie_details_title);
        tvDate = findViewById(R.id.movie_details_date);
        tvGenres = findViewById(R.id.movie_details_genres);
        rbRating = findViewById(R.id.movie_details_rating);
        tvOverview = findViewById(R.id.movie_details_overview);
        videoView = findViewById(R.id.movie_details_video);

        tvTitle.setText(movie.getTitle());
        tvDate.setText("Release Date: " + movie.getReleaseDateString());
        tvGenres.setText(movie.getGenresString());
        rbRating.setRating((float) (movie.getRating() / 2.0));
        tvOverview.setText(movie.getOverview());

    }
}