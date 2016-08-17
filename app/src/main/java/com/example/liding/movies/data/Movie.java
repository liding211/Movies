package com.example.liding.movies.data;

import java.io.Serializable;
import java.util.HashMap;

public class Movie implements Serializable {

    private final int IMAGE_SIZE_185 = 185;
    private final int IMAGE_SIZE_342 = 342;
    private final int IMAGE_SIZE_500 = 500;

    private String id;
    private String overview;
    private String title;
    private String release_date;
    private String poster_image;
    private Double vote;

    public Movie(HashMap<String, String> data) {
        if(data == null) {
            return;
        }

        if (data.get(MovieFetchTask.MOVIE_ID) != null) {
            id = data.get(MovieFetchTask.MOVIE_ID);
        }

        if (data.get(MovieFetchTask.MOVIE_OVERVIEW) != null) {
            overview = data.get(MovieFetchTask.MOVIE_OVERVIEW);
        }

        if (data.get(MovieFetchTask.MOVIE_TITLE) != null) {
            title = data.get(MovieFetchTask.MOVIE_TITLE);
        }

        if (data.get(MovieFetchTask.MOVIE_VOTE) != null) {
            vote = Double.parseDouble(data.get(MovieFetchTask.MOVIE_VOTE));
        }

        if (data.get(MovieFetchTask.MOVIE_RELEASE_DATE) != null) {
            release_date = data.get(MovieFetchTask.MOVIE_RELEASE_DATE);
        }

        if (data.get(MovieFetchTask.MOVIE_POSTER_PATH) != null) {
            poster_image = data.get(MovieFetchTask.MOVIE_POSTER_PATH);
        }
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public Double getVote() {
        return vote;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getPosterPath() {
        return getPosterPathWithSize(IMAGE_SIZE_342);
    }

    public String getPosterPathWithSize(int size) {
        return MovieFetchTask.IMAGE_BASE_URL + size + poster_image;
    }
}
