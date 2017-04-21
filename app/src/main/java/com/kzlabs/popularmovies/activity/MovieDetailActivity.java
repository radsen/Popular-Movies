package com.kzlabs.popularmovies.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.fragment.MovieDetailFragment;
import com.kzlabs.popularmovies.interfaces.MovieConstants;

/**
 * Created by radsen on 11/29/16.
 */
public class MovieDetailActivity extends BaseActivity {

    private int movieId;
    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(getIntent() != null){
            movieId = getIntent().getIntExtra(MovieConstants.MOVIE_ID_KEY, 0);
        }

        if(savedInstanceState == null){
            movieDetailFragment = MovieDetailFragment.newInstance(movieId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_movie_detail, movieDetailFragment, MovieDetailFragment.TAG)
                    .commit();
        } else {
            movieDetailFragment = (MovieDetailFragment)
                    getSupportFragmentManager().findFragmentByTag(MovieDetailFragment.TAG);
        }

    }

    @Override
    public void onNetworkStatusChange(boolean isConnected) {
        super.onNetworkStatusChange(isConnected);
        if(isConnected){
            movieDetailFragment.loadRequestedData();
        }
    }
}
