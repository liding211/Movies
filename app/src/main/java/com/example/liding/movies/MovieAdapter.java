package com.example.liding.movies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.liding.movies.CustomView.MoviePoster;
import com.example.liding.movies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter {

    private static String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context context;

    ArrayList<Movie> moviesList = new ArrayList<Movie>();

    public void add(Movie movie) {
        moviesList.add(movie);
    }

    public void clear() {
        moviesList.clear();
    }

    public MovieAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    public int getCount() {
        return moviesList.size();
    }

    public Movie getItem(int position) {
        return moviesList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new MoviePoster(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        if(moviesList != null && !moviesList.isEmpty()) {
            Log.d(LOG_TAG, "Poster url - " + moviesList.get(position).getPosterPath());
            Picasso.with(context)
                    .load(moviesList.get(position).getPosterPath())
                    .into(imageView);
        }
        return imageView;
    }
}
