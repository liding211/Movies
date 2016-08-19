package com.example.liding.movies;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liding.movies.data.Movie;
import com.example.liding.movies.data.MovieDetailsTask;
import com.example.liding.movies.data.MovieFetchTask;
import com.example.liding.movies.data.MoviesContractor;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    public MovieDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Intent intent = getActivity().getIntent();

        if (
            (intent == null || intent.getSerializableExtra(MovieListFragment.EXTRA_MOVIE) == null) &&
            (getArguments() == null)
        ) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);

        TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
        TextView overview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
        TextView vote = (TextView) rootView.findViewById(R.id.movie_detail_vote);
        TextView year = (TextView) rootView.findViewById(R.id.movie_detail_year);
        final Button favorite = (Button) rootView.findViewById(R.id.movie_detail_favorite_button);
        ImageView poster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

        final Movie movie;

        if (
            getArguments() != null &&
            getArguments().getSerializable(MovieListFragment.EXTRA_MOVIE) != null
        ) {
            movie = (Movie) getArguments().getSerializable(MovieListFragment.EXTRA_MOVIE);
        } else if(
            intent != null &&
            intent.getSerializableExtra(MovieListFragment.EXTRA_MOVIE) != null
        ) {
            movie = (Movie) intent.getSerializableExtra(MovieListFragment.EXTRA_MOVIE);
        } else {
            movie = null;
        }

        if (movie != null) {
            getMovieVideo(movie, rootView);

            Cursor cursor = getActivity().getContentResolver().query(
                MoviesContractor.MovieEntry.CONTENT_URI,
                null,
                MoviesContractor.MovieEntry._ID + " = ?",
                new String[]{movie.getId()},
                null
            );
            if (cursor.moveToFirst()) {
                favorite.setText("remove from favorite");
            }
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Cursor cursor = getActivity().getContentResolver().query(
                        MoviesContractor.MovieEntry.CONTENT_URI,
                        null,
                        MoviesContractor.MovieEntry._ID + " = ?",
                        new String[]{movie.getId()},
                        null
                    );

                    String text;

                    if (cursor.moveToFirst()) {
                        text = favorite.getText().toString();
                        String.valueOf(getActivity().getContentResolver().delete(
                            MoviesContractor.MovieEntry.CONTENT_URI,
                            MoviesContractor.MovieEntry._ID + " = ?",
                            new String[]{movie.getId()}
                        ));
                        favorite.setText("mark as favorite");
                        favorite.invalidate();
                    } else {
                        text = favorite.getText().toString();
                        ContentValues values = new ContentValues();
                        values.put(MoviesContractor.MovieEntry.COLUMN_TITLE, movie.getTitle());
                        values.put(MoviesContractor.MovieEntry._ID, movie.getId());
                        getActivity().getContentResolver().insert(MoviesContractor.MovieEntry.CONTENT_URI, values);
                        favorite.setText("remove from favorite");
                        favorite.invalidate();
                    }
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            });

            title.setText(movie.getTitle());
            overview.setText(movie.getOverview());
            String voteValue = getString(R.string.movie_rate, movie.getVote().toString());
            vote.setText(voteValue);
            try {
                Date releaseDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(movie.getReleaseDate());

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(releaseDate);

                year.setText(String.valueOf(calendar.get(Calendar.YEAR)));

            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            Picasso.with(getActivity()).load(movie.getPosterPath()).into(poster);
        }

        return rootView;
    }

    public void getMovieVideo(Movie movie, View view) {
        MovieDetailVideoAdapter videoAdapter =
            new MovieDetailVideoAdapter(getActivity(), R.layout.movie_detail_video_item, getActivity());
        ListView videoList = (ListView) view.findViewById(R.id.movie_detail_video_list);
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG_TAG, "http://www.youtube.com/watch?v=" +
                        ((TextView) view.findViewById(R.id.movie_detail_video_id)).getText());
                startActivity(
                    new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" +
                        ((TextView) view.findViewById(R.id.movie_detail_video_id)).getText()))
                );

            }
        });
        videoList.setAdapter(videoAdapter);
        (new MovieDetailsTask(getActivity(), videoAdapter)).execute(Integer.valueOf(movie.getId()));
    }
}
