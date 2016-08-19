package com.example.liding.movies.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.liding.movies.BuildConfig;
import com.example.liding.movies.MovieDetailVideoAdapter;

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

public class MovieDetailsTask extends AsyncTask<Integer, Void, HashMap<Integer, HashMap<String, String>>> {

    public static final String SUB_SECTION_MOVIE = "videos";

    public static final String RESULTS = "results";
    public static final String MOVIE_DETAILS_VIDEO_TYPE = "type";
    public static final String MOVIE_DETAILS_VIDEO_NAME = "name";
    public static final String MOVIE_DETAILS_VIDEO_SITE = "site";
    public static final String MOVIE_DETAILS_VIDEO_KEY = "key";

    private final String VIDEO_FROM_SITE_YOUTUBE = "YouTube";

    public static final String LOG_TAG = MovieDetailsTask.class.getSimpleName();

    private Context context;
    private MovieDetailVideoAdapter adapter;
    private MovieFetchTask fetchTask;

    public MovieDetailsTask(Context context, MovieDetailVideoAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        fetchTask = new MovieFetchTask(null, null);
    }

    @Override
    protected HashMap<Integer, HashMap<String, String>> doInBackground(Integer... params) {

        String request_url = fetchTask.BASE_API_URL + fetchTask.SLASH + fetchTask.API_LEVEL +
                fetchTask.SLASH + fetchTask.SECTION_MOVIE + fetchTask.SLASH + params[0] +
                fetchTask.SLASH + SUB_SECTION_MOVIE + "?";

        Uri builtUri = Uri.parse(request_url).buildUpon()
                .appendQueryParameter(fetchTask.API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        try {
            return getVideoListFromJson(makeRequest(builtUri));
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

    private HashMap<Integer, HashMap<String, String>> getVideoListFromJson(String jsonStr)
            throws JSONException {

        HashMap<Integer, HashMap<String, String>> movieVideos = new HashMap<Integer, HashMap<String, String>>();

        JSONObject json = new JSONObject(jsonStr);
        JSONArray movies = json.getJSONArray(RESULTS);

        for (int i = 0; i < movies.length(); i++) {
            HashMap<String, String> videos = new HashMap<String, String>();

            JSONObject movie = movies.getJSONObject(i);
            videos.put(MOVIE_DETAILS_VIDEO_SITE, movie.getString(MOVIE_DETAILS_VIDEO_SITE));
            videos.put(MOVIE_DETAILS_VIDEO_NAME, movie.getString(MOVIE_DETAILS_VIDEO_NAME));
            videos.put(MOVIE_DETAILS_VIDEO_KEY, movie.getString(MOVIE_DETAILS_VIDEO_KEY));

            movieVideos.put(i, videos);
        }

        return movieVideos;
    }

    @Override
    public void onPostExecute(final HashMap<Integer, HashMap<String, String>> result) {
        if (result != null && adapter != null) {
            adapter.clear();
            final HashMap<String, String> videos = new HashMap<String, String>();
            for (int i = 0; i < result.size(); i++) {
                final HashMap<String, String> video = result.get(i);
                if (video.get(MOVIE_DETAILS_VIDEO_SITE).equals(VIDEO_FROM_SITE_YOUTUBE)) {
                    videos.put(
                        video.get(MOVIE_DETAILS_VIDEO_KEY),
                        video.get(MOVIE_DETAILS_VIDEO_NAME)
                    );
                }
            }
            adapter.add(videos);
        }
    }
}
