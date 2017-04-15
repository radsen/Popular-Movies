package com.kzlabs.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.kzlabs.popularmovies.interfaces.MovieConstants;

/**
 * Created by radsen on 4/10/17.
 */

public class FragmentPlayer extends Fragment implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = FragmentPlayer.class.getSimpleName();
    private YouTubePlayerSupportFragment youtubeFragment;
    private YouTubePlayer youtubePlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate - " + this.toString());
        youtubeFragment = YouTubePlayerSupportFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.youtube_layout, youtubeFragment)
                .commit();
        youtubeFragment.initialize(getString(R.string.youtube_api_key), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView - " + this.toString());
        View view = inflater.inflate(R.layout.view_trailer, container, false);

        return view;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youtubePlayer = youTubePlayer;
            Log.d(TAG, " Youtube video: " + getArguments().getString(MovieConstants.YOUTUBE_ID_KEY));
            youTubePlayer.cueVideo(getArguments().getString(MovieConstants.YOUTUBE_ID_KEY));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        Log.e(TAG, youTubeInitializationResult.toString());
    }

    public static Fragment newInstance(String youtubeId) {
        Log.d(TAG, "newInstance Id: " + youtubeId);
        FragmentPlayer fragmentPlayer = new FragmentPlayer();
        Bundle bundle = new Bundle();
        bundle.putString(MovieConstants.YOUTUBE_ID_KEY, youtubeId);
        fragmentPlayer.setArguments(bundle);
        return fragmentPlayer;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint - " + this.toString());
        if(!isVisibleToUser && youtubePlayer != null){
            Log.d(TAG, "Release player and remove fragment");
            youtubePlayer.release();
            getChildFragmentManager().beginTransaction().remove(youtubeFragment)
                    .commitAllowingStateLoss();
        } else if(isVisibleToUser && youtubeFragment != null) {
            Log.d(TAG, "Add fragment and initialize player");
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.youtube_layout, youtubeFragment).commitAllowingStateLoss();
            youtubeFragment.initialize(getString(R.string.youtube_api_key), this);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart - " + this.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - " + this.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause - " + this.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop - " + this.toString());
    }
}
