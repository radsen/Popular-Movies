package com.kzlabs.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kzlabs.popularmovies.data.PopularMoviesContract;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.model.Comment;
import com.kzlabs.popularmovies.model.Movie;
import com.kzlabs.popularmovies.model.Trailer;
import com.kzlabs.popularmovies.sync.PMService;
import com.kzlabs.popularmovies.util.IOUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;
import java.util.List;

import static android.R.id.message;
import static android.view.View.VISIBLE;

/**
 * Created by radsen on 11/29/16.
 */

public class MovieDetailFragment extends BaseFragment implements MovieConstants,
        MovieDetailAdapter.FavListener, LoaderManager.LoaderCallbacks<Cursor>,
        ViewPager.OnPageChangeListener {

    private static final int FAV_LOADER_MOVIE_ID = 3009;

    private RecyclerView rvDetail;
    private MovieDetailAdapter mAdapter;
    private IntentFilter receiverIntentFilter;
    private Movie mMovie;
    private boolean mIsFavorite;
    private ProgressBar pbWait;
    private ShareActionProvider mShareActionProvider;
    private TextView tvError;

    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MOVIE)){
                mMovie = intent.getParcelableExtra(MovieConstants.MOVIE_KEY);
                getDataByType(mMovie.getId(), TRAILER);
            } else if (intent.getAction().equals(ACTION_TRAILERS)) {
                List<Trailer> trailers = intent.getParcelableArrayListExtra(TRAILER_KEY);
                mMovie.setTrailers(trailers);

                if(mShareActionProvider != null){
                    mShareActionProvider.setShareIntent(prepareShareIntent(0));
                }

                getDataByType(mMovie.getId(), REVIEWS);
            } else if (intent.getAction().equals(ACTION_REVIEWS)) {
                List<Comment> comments = intent.getParcelableArrayListExtra(REVIEW_KEY);
                mMovie.setComments(comments);
                mAdapter.swap(mMovie);
                rvDetail.setAdapter(mAdapter);
            }

            if(mMovie != null && mMovie.getId() != 0){
                showList();
            } else {
                showError();
            }

            pbWait.setVisibility(View.GONE);
        }
    };

    private void getDataByType(int movieId, int movieSection) {
        Uri uri = null;
        switch (movieSection){
            case MOVIE:
                uri = NetworkHelper.buildUriForMovie(getContext(), String.valueOf(movieId));
                break;
            case TRAILER:
                uri = NetworkHelper.buildUriForTrailers(getContext(), String.valueOf(movieId));
                break;
            case REVIEWS:
                uri = NetworkHelper.buildUriForReviews(getContext(), String.valueOf(movieId));
                break;
        }

        if(NetworkHelper.isNetworkAvailable(getContext()) && uri != null) {
            pbWait.setVisibility(VISIBLE);
            Intent intentService = new Intent(getContext(), PMService.class);
            intentService.putExtra(MovieConstants.SERVICE_KEY, movieSection);
            intentService.setData(uri);
            getActivity().startService(intentService);
        } else if(mMovie.getId() == 0) {
            showError();
        } else {
            showList();
            mAdapter.swap(mMovie);
            rvDetail.setAdapter(mAdapter);
        }
    }

    public static MovieDetailFragment newInstance(int movieId){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_KEY, movieId);
        movieDetailFragment.setArguments(bundle);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        pbWait = (ProgressBar) view.findViewById(R.id.pb_wait);
        tvError = (TextView) view.findViewById(R.id.tv_error);
        rvDetail = (RecyclerView) view.findViewById(R.id.rv_detail);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMovie = new Movie();

        rvDetail.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvDetail.setLayoutManager(layoutManager);
        mAdapter = new MovieDetailAdapter(getContext(), getChildFragmentManager(), mMovie,
                this, this);

        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(MovieConstants.ACTION_MOVIE);
        receiverIntentFilter.addAction(MovieConstants.ACTION_TRAILERS);
        receiverIntentFilter.addAction(MovieConstants.ACTION_REVIEWS);

        getLoaderManager().restartLoader(FAV_LOADER_MOVIE_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(syncReceiver, receiverIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(syncReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    private Intent prepareShareIntent(int position) {

        String key = "";
        if(mMovie.hasTrailers()){
            Trailer shareTrailer = (Trailer) mMovie.getTrailers().get(position);
            key = shareTrailer.getKey();
        }

        String title = mMovie.getTitle();
        String year = String.valueOf(mMovie.getYear());
        String link = getString(R.string.youtube_link) + key;
        String shareMsg = String.format(getString(R.string.share_message), title, year, link);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(getString(R.string.mime_type));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        return shareIntent;
    }

    @Override
    public void onClick(View view, long id, Bitmap bitmap) {
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = PopularMoviesContract.buildUriForFavorite(id);
        int resource;
        if(mIsFavorite){
            resolver.delete(uri, null, null);
            resource = R.drawable.ic_favorite_white;
            mIsFavorite = false;
        } else {
            resolver.insert(uri, getValues(bitmap));
            resource = R.drawable.ic_favorite_red;
            mIsFavorite = true;
        }
        ((ImageView)view).setImageResource(resource);
    }

    private ContentValues getValues(Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PopularMoviesEntry._ID, mMovie.getId());
        contentValues.put(PopularMoviesEntry.TITLE, mMovie.getTitle());
        contentValues.put(PopularMoviesEntry.RELEASE_DATE, mMovie.getReleaseDate());
        contentValues.put(PopularMoviesEntry.POSTER, mMovie.getPoster());
        contentValues.put(PopularMoviesEntry.SYNOPSIS, mMovie.getOverview());
        contentValues.put(PopularMoviesEntry.RUNTIME, mMovie.getRuntime());
        contentValues.put(PopularMoviesEntry.AVERAGE, mMovie.getAverage());
        byte[] data = IOUtils.bitmapToByteArray(bitmap);
        contentValues.put(PopularMoviesEntry.IMAGE, data);

        return contentValues;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case FAV_LOADER_MOVIE_ID:
                int movieId = getArguments().getInt(MOVIE_ID_KEY);
                Uri uri = PopularMoviesContract.buildUriForFavorite(movieId);
                return new CursorLoader(getContext(), uri, null, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0){
            mIsFavorite = true;

            mMovie = new Movie();
            data.moveToFirst();
            mMovie.setId(data.getInt(data.getColumnIndex(PopularMoviesEntry._ID)));
            mMovie.setTitle(data.getString(data.getColumnIndex(PopularMoviesEntry.TITLE)));
            mMovie.setYear(getString(R.string.format_date),
                    data.getString(data.getColumnIndex(PopularMoviesEntry.RELEASE_DATE)));
            mMovie.setRuntime(data.getInt(data.getColumnIndex(PopularMoviesEntry.RUNTIME)));
            mMovie.setAverage(data.getFloat(data.getColumnIndex(PopularMoviesEntry.AVERAGE)));
            mMovie.setOverview(data.getString(data.getColumnIndex(PopularMoviesEntry.SYNOPSIS)));
            mMovie.setAsFavorite(mIsFavorite);
            Bitmap image = IOUtils.byteArrayToBitmap(data.getBlob(data.getColumnIndex(PopularMoviesEntry.IMAGE)));
            mMovie.setBitmap(image);

            data.close();

            getDataByType(mMovie.getId(), TRAILER);

        } else {
            int movieId = getArguments().getInt(MOVIE_ID_KEY);
            getDataByType(movieId, MOVIE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swap(null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(prepareShareIntent(position));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void showList() {
        rvDetail.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void showError() {
        rvDetail.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
    }
}