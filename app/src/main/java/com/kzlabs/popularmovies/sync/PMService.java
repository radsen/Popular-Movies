package com.kzlabs.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.kzlabs.popularmovies.interfaces.MovieConstants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by radsen on 4/7/17.
 */

public class PMService extends IntentService {

    public PMService() {
        super(PMService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int key = intent.getIntExtra(MovieConstants.SERVICE_KEY, -1);
        Uri uri = intent.getData();
        switch (key){
            case MovieConstants.MOVIE_LIST:
                PMTask.retrieveMovies(getApplicationContext(), uriToUrl(uri));
                break;
            case MovieConstants.MOVIE:
                PMTask.retrieveMovieById(getApplicationContext(), uriToUrl(uri));
                break;
            case MovieConstants.TRAILER:
                PMTask.retrieveTrailersByMovieId(getApplicationContext(), uriToUrl(uri));
                break;
            case MovieConstants.REVIEWS:
                PMTask.retrieveReviewsByMovieId(getApplicationContext(), uriToUrl(uri));
                break;
        }
    }

    private URL uriToUrl(Uri uri){
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
