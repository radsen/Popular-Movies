package com.kzlabs.popularmovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.kzlabs.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by radsen on 4/7/17.
 */

class TrailerAdapter extends FragmentPagerAdapter {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mTrailers;

    public TrailerAdapter(FragmentManager fm, List<Trailer> trailers) {
        super(fm);
        mTrailers = trailers;
    }

    @Override
    public int getCount() {
        return mTrailers.size();
    }

    @Override
    public Fragment getItem(final int position) {
        return FragmentPlayer.newInstance(mTrailers.get(position).getKey());
    }

    public void swapData(List<Trailer> trailers) {
        if(trailers != null){
            mTrailers = trailers;
        }

        notifyDataSetChanged();
    }
}
