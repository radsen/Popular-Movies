package com.kzlabs.popularmovies.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.model.Trailer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by radsen on 11/30/16.
 */
public class NetworkHelper {

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Uri buildUrlForPath(Context context, String path){
        Uri uri = Uri.parse(MovieConstants.BASE_URL)
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter(MovieConstants.KEY, context.getString(R.string.tmdb_api_key))
                .build();

        return uri;
    }

    public static Uri buildUriForMovie(Context context, String movieId){
        Uri uri = Uri.parse(MovieConstants.BASE_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(MovieConstants.KEY, context.getString(R.string.tmdb_api_key))
                .build();

        return uri;
    }

    public static Uri buildUriForTrailers(Context context, String movieId) {
        Uri uri = Uri.parse(MovieConstants.BASE_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(MovieConstants.TRAILER_PATH)
                .appendQueryParameter(MovieConstants.KEY, context.getString(R.string.tmdb_api_key))
                .build();

        return uri;
    }

    public static Uri buildUriForReviews(Context context, String movieId) {
        Uri uri = Uri.parse(MovieConstants.BASE_URL)
                .buildUpon()
                .appendPath(movieId)
                .appendPath(MovieConstants.REVIEWS_PATH)
                .appendQueryParameter(MovieConstants.KEY, context.getString(R.string.tmdb_api_key))
                .build();

        return uri;
    }

    public static StringBuilder getContentFromServer(URL url) {
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(MovieConstants.TIMEOUT);
            connection.setReadTimeout(MovieConstants.READ_TIMEOUT);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb;
    }

    public static byte[] getImageAsByteArray(URL url) {

        byte[] image = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(MovieConstants.TIMEOUT);
            connection.setReadTimeout(MovieConstants.READ_TIMEOUT);

            image = IOUtils.bitmapToByteArray(
                    BitmapFactory.decodeStream(connection.getInputStream()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static String buildUriImage(String poster) {
        return buildUriImage(null, poster);
    }

    public static String buildUriImage(String size, String poster) {
        size = (size == null)?"w185":size;
        String fixPath = poster.substring(1, poster.length());
        Uri uri = Uri.parse(MovieConstants.BASE_URL_IMAGE)
                .buildUpon()
                .appendPath(size)
                .appendPath(fixPath)
                .build();

        return uri.toString();
    }

    public static URL uriToUrl(Uri uri){
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
