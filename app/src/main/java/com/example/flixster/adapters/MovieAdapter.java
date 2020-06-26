package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.MovieActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // inflate layout (itemMovie) and return it inside view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Log.d("MovieAdapter", "onCreateViewHolder");

        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // populates data into item through view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Log.d("MovieAdapter", "onBindViewHolder " + position);

        // get movie at passed in position
        Movie movie = movies.get(position);

        // bind movie data into view holder
        holder.bind(movie);
    }

    // return number of items in list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvOverview;
        TextView tvDate;
        ImageView ivPoster;
        RatingBar ratingBar;
        TextView tvGenres;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvGenres = itemView.findViewById(R.id.tvGenres);
        }

        public void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            tvDate.setText(movie.getReleaseDateString());
            tvGenres.setText(movie.getGenresString().toUpperCase());

            // add image
            String imageUrl;
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
            } else {
                imageUrl = movie.getPosterPath();
            }

            int radius = 20; // corner radius
            int margin = 0; // crop margin

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.mipmap.placeholder_foreground)
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivPoster);

            // add rating
            ratingBar.setRating((float) (movie.getRating() / 2.0));
            ratingBar.setIsIndicator(true);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, MovieActivity.class);

                // serialize movie using parceler & use short name as key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                context.startActivity(intent);
            }
        }
    }

}
