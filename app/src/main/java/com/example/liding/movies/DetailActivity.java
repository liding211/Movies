package com.example.liding.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .add(R.id.detail_container, new MovieDetailFragment())
                .commit();
        }
    }
}
