package com.kzlabs.popularmovies.data;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.kzlabs.popularmovies.R;

/**
 * Created by radsen on 4/6/17.
 */

public class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.kzlabs.popular_movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static Uri buildUriForMovieById(long id) {
        return BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(id))
                .build();
    }

    public static Uri buildUriPopular(Context context){
        return BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(context.getString(R.string.popular_path_key))
                .build();
    }

    public static Uri buildUriTopRated(Context context){
        return BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(context.getString(R.string.top_path_key))
                .build();
    }

    public static Uri buildUriFavorites(Context context){
        return BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(context.getString(R.string.favorite_key))
                .build();
    }

    public static final class PopularMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String TITLE = "title";
        public static final String POSTER = "poster";
        public static final String IMAGE = "image";
        public static final String RUNTIME = "runtime";
        public static final String SYNOPSIS = "synopsis";
        public static final String AVERAGE = "average";
        public static final String RELEASE_DATE = "release_date";
        public static final String CATEGORY = "category";
        public static final String FAV = "marked_fav";


        public static final String[] PROJECTION =
                new String[]{
                        _ID,
                        TITLE,
                        POSTER,
                        IMAGE,
                        RUNTIME,
                        SYNOPSIS,
                        AVERAGE,
                        RELEASE_DATE,
                        CATEGORY,
                        FAV
        };

    }
}
