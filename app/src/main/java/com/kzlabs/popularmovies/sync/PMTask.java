package com.kzlabs.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.data.PopularMoviesContract;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.model.Comment;
import com.kzlabs.popularmovies.model.Trailer;
import com.kzlabs.popularmovies.util.JSONUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 4/7/17.
 */

public class PMTask {

    private static final String TAG = PMTask.class.getSimpleName();

    public static void retrieveMovies(Context context, Uri uri){
        StringBuilder sb = NetworkHelper.getContentFromServer(NetworkHelper.uriToUrl(uri));

        Intent result = new Intent();

        if(sb.length() == 0){
            result.setAction(MovieConstants.ACTION_ERROR);
        } else {
            try {
                JSONObject jsonResponse = new JSONObject(sb.toString());

                JSONArray jsonMovieArray = jsonResponse.getJSONArray("results");

                ContentValues[] contentValues = new ContentValues[jsonMovieArray.length()];

                for(int index = 0; index < jsonMovieArray.length(); index++){
                    JSONObject jsonMovie = jsonMovieArray.getJSONObject(index);

                    Log.d(TAG, jsonMovie.toString());

                    ContentValues contentValue = new ContentValues();
                    contentValue.put(PopularMoviesEntry._ID,
                            jsonMovie.getInt("id"));
                    contentValue.put(PopularMoviesEntry.TITLE,
                            jsonMovie.getString("title"));
                    contentValue.put(PopularMoviesEntry.RELEASE_DATE,
                            jsonMovie.getString("release_date"));
                    String imgUrl = NetworkHelper.buildUriImage("w342",
                            jsonMovie.getString("poster_path"));
                    contentValue.put(PopularMoviesEntry.POSTER, imgUrl);
                    contentValue.put(PopularMoviesEntry.SYNOPSIS,
                            jsonMovie.getString("overview"));
                    contentValue.put(PopularMoviesEntry.RUNTIME, 0);
                    contentValue.put(PopularMoviesEntry.AVERAGE,
                            jsonMovie.getInt("vote_average"));

                    if(context.getString(R.string.popular_path_key)
                            .equals(uri.getLastPathSegment())){
                        contentValue.put(PopularMoviesEntry.CATEGORY, 1);
                    } else {
                        contentValue.put(PopularMoviesEntry.CATEGORY, 2);
                    }

                    contentValue.put(PopularMoviesEntry.AVERAGE,
                            jsonMovie.getInt("vote_average"));
                    try{
                        byte[] data = NetworkHelper.getImageAsByteArray(new URL(imgUrl));
                        contentValue.put(PopularMoviesEntry.IMAGE, data);
                    } catch (MalformedURLException ex){
                        Log.d(TAG, ex.getMessage());
                    }

                    contentValues[index] = contentValue;
                }

                context.getContentResolver().bulkInsert(PopularMoviesEntry.CONTENT_URI, contentValues);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(result);
    }

    public static void retrieveMovieById(Context context, Uri uri){
        StringBuilder sb = NetworkHelper.getContentFromServer(NetworkHelper.uriToUrl(uri));

        try {
            JSONObject jsonResponse = new JSONObject(sb.toString());

            String id = uri.getLastPathSegment();
            Uri uriForUpdate = PopularMoviesContract.buildUriForMovieById(Long.parseLong(id));

            ContentValues values = new ContentValues();
            values.put(PopularMoviesEntry.RUNTIME, jsonResponse.getInt("runtime"));

            context.getContentResolver().update(uriForUpdate, values, null, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intentMovie = new Intent(MovieConstants.ACTION_MOVIE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentMovie);
    }

    public static void retrieveTrailersByMovieId(Context context, URL url){
        StringBuilder sb = NetworkHelper.getContentFromServer(url);

        List<Trailer> trailers = JSONUtils.parseTrailers(sb);

        Intent intentMovie = new Intent(MovieConstants.ACTION_TRAILERS);
        intentMovie.putParcelableArrayListExtra(MovieConstants.TRAILER_KEY,
                (ArrayList<? extends Parcelable>) trailers);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentMovie);
    }

    public static void retrieveReviewsByMovieId(Context context, URL url){
        StringBuilder sb = NetworkHelper.getContentFromServer(url);

        List<Comment> comments = JSONUtils.parseReviews(sb);

        Intent intentMovie = new Intent(MovieConstants.ACTION_REVIEWS);
        intentMovie.putParcelableArrayListExtra(MovieConstants.REVIEW_KEY,
                (ArrayList<? extends Parcelable>) comments);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentMovie);
    }
}
