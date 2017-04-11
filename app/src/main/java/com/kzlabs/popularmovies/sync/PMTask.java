package com.kzlabs.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.model.Comment;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.model.Trailer;
import com.kzlabs.popularmovies.util.JSONUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 4/7/17.
 */

public class PMTask {

    public static void retrieveMovies(Context context, URL url){
        StringBuilder sb = NetworkHelper.getContentFromServer(url);

        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(sb.toString());

            JSONArray jsonMovieArray = jsonResponse.getJSONArray("results");

            for(int index = 0; index < jsonMovieArray.length(); index++){
                JSONObject jsonMovie = jsonMovieArray.getJSONObject(index);

                Movie movie = new Movie();
                movie.setId(jsonMovie.getInt("id"));
                String imgUrl = NetworkHelper.buildUriImage("w342",
                        jsonMovie.getString("poster_path"));
                movie.setPoster(imgUrl);

                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intentMovie = new Intent(MovieConstants.ACTION_MOVIES);
        intentMovie.putParcelableArrayListExtra(MovieConstants.MOVIE_LIST_KEY,
                (ArrayList<? extends Parcelable>) movies);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentMovie);
    }

    public static void retrieveMovieById(Context context, URL url){
        StringBuilder sb = NetworkHelper.getContentFromServer(url);

        Movie movie = new Movie();
        try {
            JSONObject jsonResponse = new JSONObject(sb.toString());

            movie.setId(jsonResponse.getInt("id"));
            movie.setTitle(jsonResponse.getString("title"));
            movie.setRuntime(jsonResponse.getInt("runtime"));
            movie.setAverage(jsonResponse.getInt("vote_average"));
            movie.setReleaseDate(jsonResponse.getString("release_date"));
            movie.setYear(context.getString(R.string.format_date),
                    jsonResponse.getString("release_date"));
            movie.setPoster(NetworkHelper.buildUriImage("w342", jsonResponse.getString("poster_path")));
            movie.setOverview(jsonResponse.getString("overview"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intentMovie = new Intent(MovieConstants.ACTION_MOVIE);
        intentMovie.putExtra(MovieConstants.MOVIE_KEY, movie);
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
