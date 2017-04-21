package com.kzlabs.popularmovies.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.kzlabs.popularmovies.fragment.MovieFragment;
import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.fragment.MovieDetailFragment;

public class MainActivity extends BaseActivity implements MovieFragment.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieFragment movieFragment;
    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMovieSelected(int movieId) {
        if(movieDetailFragment != null){
            movieDetailFragment.loadDetail(movieId);
        }
    }

    private void attachToView(int layout, Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layout, fragment, tag)
                .commit();
    }

    @Override
    public void onNetworkStatusChange(boolean isConnected) {
        super.onNetworkStatusChange(isConnected);
        Log.d(TAG, "onNetworkStatusChange");
        if(!isConnected() && movieDetailFragment != null){
            Log.d(TAG, "onNetworkStatusChange not already connected and has detail fragment");
            movieDetailFragment.loadRequestedData();
        } else if (!isConnected() && !movieFragment.hasMovies()){
            Log.d(TAG, "onNetworkStatusChange not already connected and has data");
            movieFragment.loadRequestedData();
        }
    }
}
