package com.kzlabs.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.util.NetworkHelper;

import java.util.concurrent.TimeUnit;

/**
 * Created by radsen on 4/13/17.
 */

public class PMSyncUtils {

    public static final String TAG = "pm_sync_job";

    private static final int SYNC_INTERVAL_DAYS = 5;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.DAYS.toSeconds(SYNC_INTERVAL_DAYS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 5;

    private static boolean sInitialized;

    static void scheduleJobDispatcher(Context context){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job syncMoviesJob = dispatcher.newJobBuilder()
                .setService(PopularMoviesFirebaseJob.class)
                .setTag(TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMoviesJob);
    }

    synchronized public static void initialize(final Context context){
        if(sInitialized){
            return;
        }

        sInitialized = true;

        scheduleJobDispatcher(context);

        Thread checkForData = new Thread(){
            @Override
            public void run() {
                super.run();

                Cursor cursor = context.getContentResolver().query(
                        PopularMoviesEntry.CONTENT_URI,
                        PopularMoviesEntry.PROJECTION,
                        null,
                        null,
                        PopularMoviesEntry.RELEASE_DATE);

                if(cursor == null || cursor.getCount() == 0){
                    startSyncByType(context, context.getString(R.string.popular_path_key));
                    startSyncByType(context, context.getString(R.string.top_path_key));
                } else {
                    Intent result = new Intent();
                    result.setAction(MovieConstants.ACTION_MOVIES);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(result);
                }

                cursor.close();
            }
        };

        checkForData.start();
    }

    public static void startSyncByType(Context context, String path) {
        Intent intentService = new Intent(context, PMService.class);
        intentService.putExtra(MovieConstants.SERVICE_KEY, MovieConstants.MOVIE_LIST);
        intentService.setData(NetworkHelper.buildUrlForPath(context, path));
        context.startService(intentService);
    }

    public static boolean isInitialized() {
        return sInitialized;
    }

    public static void setInitialized(boolean initialized) {
        PMSyncUtils.sInitialized = initialized;
    }
}
