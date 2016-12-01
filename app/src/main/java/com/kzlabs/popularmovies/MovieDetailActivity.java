package com.kzlabs.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kzlabs.popularmovies.interfaces.MovieConstants;

/**
 * Created by radsen on 11/29/16.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private int movieId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(getIntent() != null){
            movieId = getIntent().getIntExtra(MovieConstants.MOVIE_ID_KEY, 0);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_detail, MovieDetailFragment.newInstance(movieId))
                .commit();
    }
}
