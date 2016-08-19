package com.example.liding.movies;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.liding.movies.data.Movie;

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback {

    private String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private Boolean twoColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_fragment) != null) {
            twoColumn = true;
            if (savedInstanceState == null) {
                getFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_detail_fragment, new MovieDetailFragment(), DETAIL_FRAGMENT_TAG)
                    .commit();
            }
        } else {
            twoColumn = false;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (twoColumn) {
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            Bundle args = new Bundle();
            args.putSerializable(MovieListFragment.EXTRA_MOVIE, movie);
            movieDetailFragment.setArguments(args);
            getFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_fragment, movieDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
        } else {
            Intent movieDetailIntent = new Intent(this, DetailActivity.class);
            movieDetailIntent.putExtra(MovieListFragment.EXTRA_MOVIE, movie);
            startActivity(movieDetailIntent);
        }
    }
}
