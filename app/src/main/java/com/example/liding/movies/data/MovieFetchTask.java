package com.example.liding.movies.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.liding.movies.BuildConfig;
import com.example.liding.movies.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MovieFetchTask extends AsyncTask<MovieFetchTask.MoviesListType, Void, HashMap<Integer, HashMap<String, String>>> {

    private final String LOG_TAG = MovieFetchTask.class.getSimpleName();
    private final String BASE_API_URL = "http://api.themoviedb.org";
    private final String API_LEVEL = "3";
    private final String SECTION_MOVIE = "movie";
    private final String SLASH = "/";


    private final String SORT_BY_PARAM = "sort_by";
    private final String API_KEY_PARAM = "api_key";

    // These are the names of the JSON objects that need to be extracted.
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w";
    public static final String RESULTS = "results";
    public static final String MOVIE_POSTER_PATH = "poster_path";
    public static final String MOVIE_OVERVIEW = "overview";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_VOTE = "vote_average";
    public static final String MOVIE_RELEASE_DATE = "release_date";
    public static final String MOVIE_ID = "id";

    public static enum MoviesListType {
        MOVIES_MOST_POPULAR("popular"),
        MOVIES_HIGHEST_RATED("top_rated");

        String listType;

        MoviesListType(String value) {
            listType = value;
        }

        public String toString() {
            return listType;
        }
    }
    private Context context;
    private MovieAdapter adapter;

    public MovieFetchTask(Context context, MovieAdapter movieAdapter) {
        this.context = context;
        this.adapter = movieAdapter;
    }

    @Override
    protected HashMap<Integer, HashMap<String, String>> doInBackground(MoviesListType... params) {

        String request_url  = BASE_API_URL + SLASH + API_LEVEL + SLASH + SECTION_MOVIE;

        if (params.length > 0 && params[0] != null) {
            switch (params[0]) {
                case MOVIES_MOST_POPULAR:
                    request_url += SLASH + MoviesListType.MOVIES_MOST_POPULAR.toString();
                    break;
                case MOVIES_HIGHEST_RATED:
                    request_url += SLASH + MoviesListType.MOVIES_HIGHEST_RATED.toString();
                    break;
                default:
                    break;
            }
        } else {
            request_url += SLASH + MoviesListType.MOVIES_MOST_POPULAR.toString();
        }

        request_url += "?";

        Uri builtUri = Uri.parse(request_url).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        try {
            return getMoviesListFromJson(makeRequest(builtUri));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String makeRequest(Uri uri) {
        String response = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            response = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return response;
    }

    private HashMap<Integer, HashMap<String, String>> getMoviesListFromJson(String jsonStr)
            throws JSONException {

        HashMap<Integer, HashMap<String, String>> parsedData = new HashMap<Integer, HashMap<String, String>>();

        JSONObject json = new JSONObject(jsonStr);
        JSONArray movies = json.getJSONArray(RESULTS);

        for(int i = 0; i < movies.length(); i++) {
            HashMap<String, String> movieData = new HashMap<String, String>();

            JSONObject movie = movies.getJSONObject(i);
            movieData.put(MOVIE_POSTER_PATH, movie.getString(MOVIE_POSTER_PATH));
            movieData.put(MOVIE_OVERVIEW, movie.getString(MOVIE_OVERVIEW));
            movieData.put(MOVIE_TITLE, movie.getString(MOVIE_TITLE));
            movieData.put(MOVIE_VOTE, movie.getString(MOVIE_VOTE));
            movieData.put(MOVIE_RELEASE_DATE, movie.getString(MOVIE_RELEASE_DATE));
            movieData.put(MOVIE_ID, movie.getString(MOVIE_ID));

            parsedData.put(i, movieData);
        }

        return parsedData;
    }

    @Override
    public void onPostExecute(HashMap<Integer, HashMap<String, String>> result) {
        if(result != null && adapter != null) {
            adapter.clear();
            for (int i = 0; i < result.size(); i++) {
                adapter.add(new Movie(result.get(i)));
            }
            if (adapter.getCount() > 0) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
