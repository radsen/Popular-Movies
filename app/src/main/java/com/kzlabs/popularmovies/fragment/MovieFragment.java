package com.kzlabs.popularmovies.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.activity.MovieDetailActivity;
import com.kzlabs.popularmovies.adapter.MoviesAdapter;
import com.kzlabs.popularmovies.data.PopularMoviesContract;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.interfaces.RecyclerViewItemClickListener;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.sync.PMSyncUtils;
import com.kzlabs.popularmovies.util.MoviesLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */

public class MovieFragment extends BaseFragment implements RecyclerViewItemClickListener,
        MovieConstants, LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String TAG = MovieFragment.class.getSimpleName();

    private static final int FAV_LOADER_ID = 3466;
    private static final int POPULAR_LOADER_ID = 3464;
    private static final int TOP_RATED_LOADER_ID = 3465;

    private RecyclerView rvMovies;
    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private IntentFilter receiverIntentFilter;
    private LoaderManager.LoaderCallbacks<List<Movie>> listener;
    private OnItemSelectedListener itemSelectedListener;
    private boolean mMultiPane;
    private int mSelectedLoader = 0;
    private int mScrollPosition = 0;
    private int mSelectedPosition = 0;

    public boolean hasMovies() {
        return mMoviesAdapter.getItemCount() > 0;
    }

    public interface OnItemSelectedListener {
        void onMovieSelected(int movieId);
    }

    private BroadcastReceiver syncMovies = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MOVIES)){
                getActivity()
                        .getSupportLoaderManager()
                        .restartLoader(POPULAR_LOADER_ID, null, listener)
                        .forceLoad();
                showList();
            } else if (intent.getAction().equals(ACTION_ERROR)){
                PMSyncUtils.setInitialized(false);
                showError();
            }
            hideProgress();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        listener = this;
        mMultiPane = getResources().getBoolean(R.bool.twoPaneMode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnItemSelectedListener){
            itemSelectedListener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement MovieFragment.OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        rvMovies = (RecyclerView) view.findViewById(R.id.rv_movies);
        rvMovies.setHasFixedSize(true);

        int columns = calculateNoOfColumns(getContext());
        mGridLayoutManager = new GridLayoutManager(getContext(), columns);
        rvMovies.setLayoutManager(mGridLayoutManager);
        setScrollChangedListener();
        return view;
    }

    private void setScrollChangedListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvMovies.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    mSelectedPosition = NO_SELECTION;
                }
            });
        } else {
            rvMovies.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mSelectedPosition = NO_SELECTION;
                }
            });
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        if(savedInstanceState != null){
            mSelectedLoader = savedInstanceState.getInt(LOADER_ID_KEY, POPULAR_LOADER_ID);
            mScrollPosition = savedInstanceState.getInt(LAST_SCROLLED_POSITION, 0);
            mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION, NO_SELECTION);
        } else {
            mSelectedLoader = POPULAR_LOADER_ID;
            mSelectedPosition = NO_SELECTION;
        }

        List<Movie> movieList = new ArrayList<>();
        mMoviesAdapter = new MoviesAdapter(movieList);
        mMoviesAdapter.setOnItemClickListener(this);
        rvMovies.setAdapter(mMoviesAdapter);

        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(ACTION_MOVIES);
        receiverIntentFilter.addAction(ACTION_ERROR);

        loadRequestedData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(LOADER_ID_KEY, mSelectedLoader);
        int first = ((GridLayoutManager)rvMovies.getLayoutManager())
                .findFirstVisibleItemPosition();
        outState.putInt(LAST_SCROLLED_POSITION, first);

        outState.putInt(SELECTED_POSITION, mSelectedPosition);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        mSelectedPosition = 0;
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

        getLoaderManager()
                .restartLoader(mSelectedLoader, null, this)
                .forceLoad();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        mSelectedPosition = position;
        Movie movie = mMoviesAdapter.get(position);

        if(mMultiPane){
            itemSelectedListener.onMovieSelected(movie.getId());
        } else {
            Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);
            detailIntent.putExtra(MOVIE_ID_KEY, movie.getId());
            startActivity(detailIntent);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        showProgress();

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

        return new MoviesLoader(getContext(),uri);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mMoviesAdapter.swapData(data);
        if(mMoviesAdapter.getItemCount() > 0 ){
            int position = (mSelectedPosition != NO_SELECTION)?mSelectedPosition:
                    mScrollPosition;
            rvMovies.scrollToPosition(position);
            itemSelectedListener.onMovieSelected(mMoviesAdapter.get(position).getId());
        }
        showList();
        hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMoviesAdapter.swapData(null);
    }

    private void showList() {
        rvMovies.setVisibility(View.VISIBLE);
    }

    private void showError() {
        rvMovies.setVisibility(View.GONE);
    }


    public static MovieFragment newInstance(Bundle extras) {
        MovieFragment movieFragment = new MovieFragment();
        movieFragment.setArguments(extras);
        return movieFragment;
    }

    /*
     *  Taken from one of my instructors suggestions when reviewing the code
     *  the only modification is the scale factor value
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = context.getResources().getInteger(R.integer.scale_factor);
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }

    @Override
    public void loadRequestedData() {
        super.loadRequestedData();
        Log.d(TAG, "loadRequestedData");
        if(!PMSyncUtils.isInitialized()){
            Log.d(TAG, "Not initialized");
            showProgress();
            PMSyncUtils.initialize(getContext());
        } else {
            Log.d(TAG, "Initialized");
            getActivity().getSupportLoaderManager()
                    .restartLoader(mSelectedLoader, null, this)
                    .forceLoad();
        }
    }
}