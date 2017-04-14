package com.kzlabs.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieFragment movieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            movieFragment = new MovieFragment();
            movieFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,
                    movieFragment, MovieFragment.TAG).commit();
        } else {
            movieFragment = (MovieFragment) getSupportFragmentManager()
                    .findFragmentByTag(MovieFragment.TAG);
        }
    }

}
