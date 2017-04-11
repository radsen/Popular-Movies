package com.kzlabs.popularmovies.interfaces;

/**
 * Created by radsen on 11/29/16.
 */
public interface MovieConstants {

    String KEY = "api_key";
    String BASE_URL = "http://api.themoviedb.org/3/movie/";

    String MOVIE_ID_KEY = "com.popular.movies.id";

    String MOVIE_KEY = "com.popular_movies.movie_key";
    String TRAILER_KEY = "com.popular_movies.trailer_key";
    String REVIEW_KEY = "com.popular_movies.review_key";
    String MOVIE_LIST_KEY = "com.popular_movies.movie_list_key";

    String ACTION_MOVIE = "com.popular_movies.MOVIE";
    String ACTION_TRAILERS = "com.popular_movies.TRAILERS";
    String ACTION_REVIEWS = "com.popular_movies.REVIEWS";
    String ACTION_MOVIES = "com.popular_movies.MOVIES";

    String TRAILER_PATH = "videos";
    String REVIEWS_PATH = "reviews";

    int REVIEWS = 2;
    int TRAILER = 1;
    int MOVIE = 0;
    int MOVIE_LIST = 3;

    String SERVICE_KEY = "com.popular_movies.intent_service_key";
    String BASE_URL_IMAGE = "http://image.tmdb.org/t/p";
    String YOUTUBE_ID_KEY = "com.popular_movies.youtube_id_key";
}
