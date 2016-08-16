package com.example.liding.movies;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //прикрепляем шаблон к главной активности
        setContentView(R.layout.activity_main);

        //биндим фрагмент в наш шаблон при условии что инстанс отстутствует
        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, new MovieListFragment())
                .commit();
        }
    }
}
