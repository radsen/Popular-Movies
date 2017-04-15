package com.kzlabs.popularmovies;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MovieFragment.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieFragment movieFragment;
    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean multiPane = getResources().getBoolean(R.bool.twoPaneMode);

        if(multiPane && savedInstanceState == null){
            movieFragment = MovieFragment.newInstance(getIntent().getExtras());
            attachToView(R.id.fl_movie_list, movieFragment, MovieFragment.TAG);
            movieDetailFragment = new MovieDetailFragment();
            attachToView(R.id.fl_movie_detail, movieDetailFragment, MovieDetailFragment.TAG);
        } else if (multiPane && savedInstanceState != null) {
            movieFragment = (MovieFragment) getSupportFragmentManager()
                    .findFragmentByTag(MovieFragment.TAG);
            movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager()
                    .findFragmentByTag(MovieDetailFragment.TAG);
        } else if (!multiPane && savedInstanceState == null) {
            movieFragment = MovieFragment.newInstance(getIntent().getExtras());
            attachToView(R.id.fl_movie_list, movieFragment, MovieFragment.TAG);
        } else if (!multiPane && savedInstanceState != null) {
            movieFragment = (MovieFragment) getSupportFragmentManager()
                    .findFragmentByTag(MovieFragment.TAG);
        }
    }

    @Override
    public void onMovieSelected(int movieId) {
        movieDetailFragment.loadDetail(movieId);
    }

    private void attachToView(int layout, Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layout, fragment, tag)
                .commit();
    }
}
