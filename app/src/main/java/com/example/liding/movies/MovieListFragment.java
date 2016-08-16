package com.example.liding.movies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.liding.movies.data.Movie;
import com.example.liding.movies.data.MovieFetchTask;

public class MovieListFragment extends Fragment {

    private static String LOG_TAG = MovieListFragment.class.getSimpleName();
    public static String EXTRA_MOVIE = "extra_movie";

    MovieAdapter adapter;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new MovieAdapter(getActivity(), R.layout.movie_list_fragment);
        updateMovieList();
        View rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);

        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movie_list_grid);
        moviesGrid.setAdapter(adapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = adapter.getItem(position);

                Intent movieDetailIntent = new Intent(getActivity(), DetailActivity.class);
                movieDetailIntent.putExtra(EXTRA_MOVIE, movie);
                startActivity(movieDetailIntent);
            }
        });

        return rootView;
    }

    public void updateMovieList() {
        MovieFetchTask task = new MovieFetchTask(getActivity(), adapter);
        task.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
