package com.kzlabs.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.kzlabs.popularmovies.data.PopularMoviesContract;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.interfaces.RecyclerViewItemClickListener;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.sync.PopularMoviesSyncUtils;
import com.kzlabs.popularmovies.util.IOUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */

public class MovieFragment extends BaseFragment implements RecyclerViewItemClickListener,
        MovieConstants, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MovieFragment.class.getSimpleName();

    private static final int FAV_LOADER_ID = 3466;
    private static final int POPULAR_LOADER_ID = 3464;
    private static final int TOP_RATED_LOADER_ID = 3465;

    private RecyclerView rvMovies;
    private ProgressBar pbWait;
    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> movieList;
    private IntentFilter receiverIntentFilter;
    private TextView tvError;
    private LoaderManager.LoaderCallbacks<Cursor> listener;
    private int mSelectedLoader = 0;

    private BroadcastReceiver syncMovies = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MOVIES)){
                getActivity().getSupportLoaderManager()
                        .restartLoader(POPULAR_LOADER_ID, null, listener);
                showList();
            } else if (intent.getAction().equals(ACTION_ERROR)){
                showError();
            }
            pbWait.setVisibility(View.GONE);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        listener = this;
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

        if(savedInstanceState != null){
            mSelectedLoader = savedInstanceState.getInt(LOADER_ID_KEY, POPULAR_LOADER_ID);
        } else {
            mSelectedLoader = POPULAR_LOADER_ID;
        }

        movieList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(movieList);
        mMoviesAdapter.setOnItemClickListener(this);
        rvMovies.setAdapter(mMoviesAdapter);

        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(ACTION_MOVIES);
        receiverIntentFilter.addAction(ACTION_ERROR);

        pbWait.setVisibility(View.VISIBLE);
        getActivity().getSupportLoaderManager().initLoader(mSelectedLoader, null, this);
        PopularMoviesSyncUtils.initialize(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(LOADER_ID_KEY, mSelectedLoader);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(syncMovies, receiverIntentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                mSelectedLoader = POPULAR_LOADER_ID;
                break;

            case R.id.action_top_rated:
                mSelectedLoader = TOP_RATED_LOADER_ID;
                break;

            case R.id.action_fav:
                mSelectedLoader = FAV_LOADER_ID;
                break;
        }

        getLoaderManager().restartLoader(mSelectedLoader, null, this);

        return super.onOptionsItemSelected(item);
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
        pbWait.setVisibility(View.VISIBLE);

        Uri uri = null;
        switch (id){
            case POPULAR_LOADER_ID:
                uri = PopularMoviesContract.buildUriPopular(getContext());
             break;
            case TOP_RATED_LOADER_ID:
                uri = PopularMoviesContract.buildUriTopRated(getContext());
                break;
            case FAV_LOADER_ID:
                uri = PopularMoviesContract.buildUriFavorites(getContext());
                break;
        }

        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            return;
        }

        movieList.clear();
        new AsyncTask<Cursor, Void, Void>(){
            @Override
            protected Void doInBackground(Cursor... cursors) {
                Cursor data = cursors[0];
                if(data.moveToFirst()){
                    do {
                        Movie movie = new Movie();
                        movie.setId(data.getInt(data.getColumnIndex(PopularMoviesEntry._ID)));
                        movie.setTitle(data.getString(data.getColumnIndex(PopularMoviesEntry.TITLE)));
                        movie.setRuntime(data.getInt(data.getColumnIndex(PopularMoviesEntry.RUNTIME)));
                        movie.setPoster(data.getString(data.getColumnIndex(PopularMoviesEntry.POSTER)));
                        movie.setOverview(data.getString(data.getColumnIndex(PopularMoviesEntry.SYNOPSIS)));
                        movie.setAverage(data.getFloat(data.getColumnIndex(PopularMoviesEntry.AVERAGE)));
                        movie.setYear(getString(R.string.format_date),
                                data.getString(data.getColumnIndex(PopularMoviesEntry.RELEASE_DATE)));
                        movieList.add(movie);
                        Bitmap image = IOUtils.byteArrayToBitmap(
                                data.getBlob(data.getColumnIndex(PopularMoviesEntry.IMAGE)));
                        movie.setBitmap(image);
                    } while (data.moveToNext());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mMoviesAdapter.swapData(movieList);
                showList();
                pbWait.setVisibility(View.GONE);
            }
        }.execute(data);

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