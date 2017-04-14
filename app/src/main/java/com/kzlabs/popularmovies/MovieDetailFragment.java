package com.kzlabs.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.kzlabs.popularmovies.sync.DetailQueryHandler;
import com.kzlabs.popularmovies.sync.PMService;
import com.kzlabs.popularmovies.util.IOUtils;
import com.kzlabs.popularmovies.util.NetworkHelper;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Created by radsen on 11/29/16.
 */

public class MovieDetailFragment extends BaseFragment implements MovieConstants,
        MovieDetailAdapter.FavListener, ViewPager.OnPageChangeListener,
        DetailQueryHandler.DetailQueryHandlerListener {

    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    private static final int UPDATE_FAVORITE = 10;
    private static final int QUERY_MOVIE_BY_ID = 11;

    private RecyclerView rvDetail;
    private IntentFilter receiverIntentFilter;
    private Movie mMovie;
    private ProgressBar pbWait;
    private ShareActionProvider mShareActionProvider;
    private TextView tvError;
    private ImageView ivFavButton;
    private DetailQueryHandler detailQueryHandler;
    private int mMovieId;
    private ViewPager.OnPageChangeListener pageListener;
    private MovieDetailAdapter.FavListener favListener;

    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MOVIE)){
                mMovieId = getArguments().getInt(MOVIE_ID_KEY);
                Uri uri = PopularMoviesContract.buildUriForMovieById(mMovieId);
                detailQueryHandler.startQuery(QUERY_MOVIE_BY_ID, null, uri, null, null, null, null);
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
                rvDetail.setAdapter(new MovieDetailAdapter(context, getChildFragmentManager(),
                        mMovie, favListener, pageListener));
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
        } else {
            uri = PopularMoviesContract.buildUriForMovieById(mMovieId);
            detailQueryHandler.startQuery(QUERY_MOVIE_BY_ID, null, uri, null, null, null, null);
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
        pageListener = this;
        favListener = this;
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

        mMovieId = getArguments().getInt(MOVIE_ID_KEY);

        rvDetail.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvDetail.setLayoutManager(layoutManager);

        receiverIntentFilter = new IntentFilter();
        receiverIntentFilter.addAction(MovieConstants.ACTION_MOVIE);
        receiverIntentFilter.addAction(MovieConstants.ACTION_TRAILERS);
        receiverIntentFilter.addAction(MovieConstants.ACTION_REVIEWS);

        detailQueryHandler = new DetailQueryHandler(getContext().getContentResolver(), this);
        getDataByType(mMovieId, MOVIE);
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
    public void onClick(final View view, long id) {
        ivFavButton = (ImageView) view;

        Uri uri = PopularMoviesContract.buildUriForMovieById(id);

        if(mMovie.isFavorite()){
            mMovie.setAsFavorite(false);
        } else {
            mMovie.setAsFavorite(true);
        }

        ContentValues values = new ContentValues();
        values.put(PopularMoviesEntry.FAV, mMovie.isFavorite());
        detailQueryHandler.startUpdate(UPDATE_FAVORITE, null, uri, values, null, null);

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

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        switch (token){
            case QUERY_MOVIE_BY_ID:
                loadMovie(cursor);
                break;
        }
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        switch (token){
            case UPDATE_FAVORITE:
                int resource = 0;
                if(result > 0){
                    resource = (mMovie.isFavorite())?
                            R.drawable.ic_favorite_red:R.drawable.ic_favorite_white;
                } else {
                    mMovie.setAsFavorite(!mMovie.isFavorite());
                }
                ivFavButton.setImageResource(resource);
                break;
        }
    }

    private void loadMovie(Cursor data) {
        if(data != null && data.getCount() > 0){

            mMovie = new Movie();
            data.moveToFirst();
            mMovie.setId(data.getInt(data.getColumnIndex(PopularMoviesEntry._ID)));
            mMovie.setTitle(data.getString(data.getColumnIndex(PopularMoviesEntry.TITLE)));
            mMovie.setYear(getString(R.string.format_date),
                    data.getString(data.getColumnIndex(PopularMoviesEntry.RELEASE_DATE)));
            mMovie.setRuntime(data.getInt(data.getColumnIndex(PopularMoviesEntry.RUNTIME)));
            mMovie.setAverage(data.getFloat(data.getColumnIndex(PopularMoviesEntry.AVERAGE)));
            mMovie.setOverview(data.getString(data.getColumnIndex(PopularMoviesEntry.SYNOPSIS)));
            mMovie.setAsFavorite(data.getInt(data.getColumnIndex(PopularMoviesEntry.FAV)) == 1);
            Bitmap image = IOUtils.byteArrayToBitmap(data.getBlob(data.getColumnIndex(PopularMoviesEntry.IMAGE)));
            mMovie.setBitmap(image);

            data.close();

            if(NetworkHelper.isNetworkAvailable(getContext())){
                getDataByType(mMovieId, TRAILER);
            } else {
                rvDetail.setAdapter(new MovieDetailAdapter(getContext(), getChildFragmentManager(),
                        mMovie, this, this));
            }
        }
    }
}