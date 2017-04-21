package com.kzlabs.popularmovies.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kzlabs.popularmovies.fragment.FragmentPlayer;
import com.kzlabs.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by radsen on 4/7/17.
 */

class TrailerAdapter extends FragmentStatePagerAdapter {

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
        return FragmentPlayer.newInstance(mTrailers.get(position).getKey(), position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void swapData(List<Trailer> trailers) {
        if(trailers != null){
            mTrailers = trailers;
        }

        notifyDataSetChanged();
    }
}
