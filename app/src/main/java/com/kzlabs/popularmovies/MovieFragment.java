package com.kzlabs.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.interfaces.RecyclerViewItemClickListener;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */

public class MovieFragment extends BaseFragment implements RecyclerViewItemClickListener,
        MovieConstants {

    private static final String TAG = MovieFragment.class.getSimpleName();

    private RecyclerView rvMovies;
    private GridLayoutManager mGridLayoutManager;

    private MoviesAdapter mMoviesAdapter;
    private List<Movie> movieList;

    private AsyncTask<String, Integer, List<Movie>> fetchMovieData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        rvMovies = (RecyclerView) view.findViewById(R.id.rv_movies);

        rvMovies.setHasFixedSize(true);

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvMovies.setLayoutManager(mGridLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        movieList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(movieList);
        mMoviesAdapter.setOnItemClickListener(this);
        rvMovies.setAdapter(mMoviesAdapter);

        if(NetworkHelper.isNetworkAvailable(getContext())){
            String strPopularUrl = BASE_URL + "popular?" + KEY + getString(R.string.tmdb_api_key);
            fetchMovieData = new FetchMovieData().execute(strPopularUrl);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = null;
        switch (item.getItemId()){
            case R.id.action_popular:
                url = BASE_URL + "popular?" + KEY + getString(R.string.tmdb_api_key);
                break;

            case R.id.action_top_rated:
                url = BASE_URL + "top_rated?" + KEY + getString(R.string.tmdb_api_key);
                break;
        }

        if(NetworkHelper.isNetworkAvailable(getContext()) && url != null){
            fetchMovieData = new FetchMovieData().execute(url);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);
        Movie movie = movieList.get(position);
        detailIntent.putExtra(MOVIE_ID_KEY, movie.getId());
        startActivity(detailIntent);
    }

    private class FetchMovieData extends AsyncTask<String, Integer, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {

            StringBuilder sb = new StringBuilder();

            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
                if(reader == null){
                    try{
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            List<Movie> movies = new ArrayList<>();
            try {
                JSONObject jsonResponse = new JSONObject(sb.toString());

                JSONArray jsonMovieArray = jsonResponse.getJSONArray("results");

                for(int index = 0; index < jsonMovieArray.length(); index++){
                    JSONObject jsonMovie = jsonMovieArray.getJSONObject(index);

                    Movie movie = new Movie();
                    movie.setId(jsonMovie.getInt("id"));
                    String imgUrl = "http://image.tmdb.org/t/p/w342" + jsonMovie.getString("poster_path");
                    movie.setPoster(imgUrl);

                    movies.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);

            movieList.clear();
            movieList.addAll(movies);
            mMoviesAdapter.notifyDataSetChanged();;
        }
    }
}
