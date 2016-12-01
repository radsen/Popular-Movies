package com.kzlabs.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.util.BindingUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by radsen on 11/29/16.
 */

public class MovieDetailFragment extends BaseFragment implements MovieConstants {

    private ImageView ivThumbnail;
    private TextView tvYear;
    private TextView tvDuration;
    private TextView tvRating;
    private TextView tvOverview;
    private TextView tvTitle;

    public static MovieDetailFragment newInstance(int movieId){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_KEY, movieId);
        movieDetailFragment.setArguments(bundle);
        return movieDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumb);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvYear = (TextView) view.findViewById(R.id.tv_year);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        tvRating = (TextView) view.findViewById(R.id.tv_rating);
        tvOverview = (TextView) view.findViewById(R.id.tv_overview);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(NetworkHelper.isNetworkAvailable(getContext())){
            int movieId = getArguments().getInt(MOVIE_ID_KEY);
            String detailUrl = BASE_URL + String.valueOf(movieId) + "?" + KEY +
                    getString(R.string.tmdb_api_key);
            new FetchMovieDetail().execute(detailUrl);
        }
    }

    private class FetchMovieDetail extends AsyncTask<String, Integer, Movie>{

        @Override
        protected Movie doInBackground(String... strings) {
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

            Movie movie = new Movie();
            try {
                JSONObject jsonResponse = new JSONObject(sb.toString());

                movie.setId(jsonResponse.getInt("id"));
                movie.setTitle(jsonResponse.getString("title"));
                movie.setRuntime(jsonResponse.getInt("runtime"));
                movie.setAverage(jsonResponse.getInt("vote_average"));
                movie.setYear(getString(R.string.format_date),
                        jsonResponse.getString("release_date"));
                movie.setPoster(jsonResponse.getString("poster_path"));
                movie.setOverview(jsonResponse.getString("overview"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            tvTitle.setText(movie.getTitle());
            String imgUrl = "http://image.tmdb.org/t/p/w185" + movie.getPoster();
            BindingUtils.loadImage(ivThumbnail, imgUrl);
            String year = (movie.getYear() > 0)?
                    String.valueOf(movie.getYear()):getString(R.string.txt_unknown);
            tvYear.setText(year);
            tvDuration.setText(String.format(getString(R.string.format_dur), movie.getRuntime()));
            tvRating.setText(String.format(getString(R.string.format_rat), movie.getAverage()));
            tvOverview.setText(movie.getOverview());
        }
    }
}
