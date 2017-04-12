package com.kzlabs.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.interfaces.RecyclerViewItemClickListener;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.sync.PMService;
import com.kzlabs.popularmovies.util.IOUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;
import com.kzlabs.popularmovies.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */

public class MovieFragment extends BaseFragment implements RecyclerViewItemClickListener,
        MovieConstants, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MovieFragment.class.getSimpleName();

    private static final int FAV_LOADER_ID = 3466;

    private RecyclerView rvMovies;
    private ProgressBar pbWait;
    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> movieList;
    private IntentFilter receiverIntentFilter;
    private TextView tvError;

    private BroadcastReceiver syncMovies = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MOVIES)){
                movieList.clear();
                movieList = intent.getParcelableArrayListExtra(MOVIE_LIST_KEY);
                mMoviesAdapter.swapData(movieList);
                pbWait.setVisibility(View.GONE);
                showList();
            }
        }
    };

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

        pbWait = (ProgressBar) view.findViewById(R.id.pb_wait);
        tvError = (TextView) view.findViewById(R.id.tv_error);
        rvMovies = (RecyclerView) view.findViewById(R.id.rv_movies);

        rvMovies.setHasFixedSize(true);

        int columns = getResources().getInteger(R.integer.columns);
        mGridLayoutManager = new GridLayoutManager(getContext(), columns);
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

        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(ACTION_MOVIES);

        retrieveBy(PreferenceUtils.getQuery(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(syncMovies, receiverIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(syncMovies);
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
                retrieveBy(getString(R.string.popular_path_key));
                PreferenceUtils.setQuery(getContext(), getString(R.string.popular_path_key));
                break;

            case R.id.action_top_rated:
                retrieveBy(getString(R.string.top_path_key));
                PreferenceUtils.setQuery(getContext(), getString(R.string.top_path_key));
                break;

            case R.id.action_fav:
                getLoaderManager().initLoader(FAV_LOADER_ID, null, this);
                PreferenceUtils.setQuery(getContext(), getString(R.string.favorite_key));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveBy(String param){
        if(getString(R.string.favorite_key).equals(param)){
            getLoaderManager().initLoader(FAV_LOADER_ID, null, this);
            return;
        }

        Uri uri = NetworkHelper.buildUrlForPath(getContext(), param);
        if(NetworkHelper.isNetworkAvailable(getContext()) && uri != null) {
            pbWait.setVisibility(View.VISIBLE);
            Intent intentService = new Intent(getContext(), PMService.class);
            intentService.putExtra(SERVICE_KEY, MOVIE_LIST);
            intentService.setData(uri);
            getActivity().startService(intentService);
        } else {
            showError();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);
        Movie movie = movieList.get(position);
        detailIntent.putExtra(MOVIE_ID_KEY, movie.getId());
        startActivity(detailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = PopularMoviesEntry.CONTENT_URI;
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            return;
        }

        movieList.clear();
        if(data.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(data.getInt(data.getColumnIndex(PopularMoviesEntry._ID)));
                movie.setTitle(data.getString(data.getColumnIndex(PopularMoviesEntry.TITLE)));
                movie.setRuntime(data.getInt(data.getColumnIndex(PopularMoviesEntry.RUNTIME)));
                movie.setOverview(data.getString(data.getColumnIndex(PopularMoviesEntry.SYNOPSIS)));
                movie.setAverage(data.getFloat(data.getColumnIndex(PopularMoviesEntry.AVERAGE)));
                movie.setYear(getString(R.string.format_date),
                        data.getString(data.getColumnIndex(PopularMoviesEntry.RELEASE_DATE)));
                movieList.add(movie);
                Bitmap image = IOUtils.byteArrayToBitmap(data.getBlob(data.getColumnIndex(PopularMoviesEntry.IMAGE)));
                movie.setBitmap(image);
            } while (data.moveToNext());
        }

        mMoviesAdapter.swapData(movieList);
        showList();
    }

    private void showList() {
        rvMovies.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void showError() {
        rvMovies.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapData(null);
    }

}