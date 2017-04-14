package com.kzlabs.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.kzlabs.popularmovies.interfaces.MovieConstants;
import com.kzlabs.popularmovies.util.NetworkHelper;

/**
 * Created by radsen on 4/13/17.
 */

public class PopularMoviesFirebaseJob extends JobService implements MovieConstants {

    private AsyncTask<Void, Void, Void> fetchMoviesTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        fetchMoviesTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                PMTask.retrieveMovies(getApplicationContext(),
                        NetworkHelper.buildUrlForPath(context, POPULAR));
                PMTask.retrieveMovies(getApplicationContext(),
                        NetworkHelper.buildUrlForPath(context, TOP_RATED));
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(job, false);
            }
        }.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (fetchMoviesTask != null) {
            fetchMoviesTask.cancel(true);
        }
        return true;
    }
}
