package com.kzlabs.popularmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 4/19/17.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String TAG = MoviesLoader.class.getSimpleName();

    private final Uri uri;
    private List<Movie> movieList;

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public MoviesLoader(Context context, Uri uri) {
        super(context);
        this.uri = uri;
        movieList = new ArrayList<>();
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.d(TAG, "loadInBackground");

        Cursor data = getContext().getContentResolver().query(uri, null, null, null, null);

        movieList.clear();

        if(data.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(data.getInt(data.getColumnIndex(PopularMoviesEntry._ID)));
                movie.setTitle(data.getString(data.getColumnIndex(PopularMoviesEntry.TITLE)));
                movie.setRuntime(data.getInt(data.getColumnIndex(PopularMoviesEntry.RUNTIME)));
                movie.setPoster(data.getString(data.getColumnIndex(PopularMoviesEntry.POSTER)));
                movie.setOverview(data.getString(data.getColumnIndex(PopularMoviesEntry.SYNOPSIS)));
                movie.setAverage(data.getFloat(data.getColumnIndex(PopularMoviesEntry.AVERAGE)));
                movie.setYear(getContext().getString(R.string.format_date),
                        data.getString(data.getColumnIndex(PopularMoviesEntry.RELEASE_DATE)));
                movieList.add(movie);
                Bitmap image = IOUtils.byteArrayToBitmap(
                        data.getBlob(data.getColumnIndex(PopularMoviesEntry.IMAGE)));
                movie.setBitmap(image);
            } while (data.moveToNext());
        }

        data.close();

        return movieList;
    }

}
