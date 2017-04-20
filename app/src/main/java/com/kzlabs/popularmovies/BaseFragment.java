package com.kzlabs.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kzlabs.popularmovies.interfaces.OnDataRequested;
import com.kzlabs.popularmovies.util.BaseActivity;

/**
 * Created by radsen on 11/28/16.
 */
public class BaseFragment extends Fragment implements OnDataRequested {

    private BaseActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BaseActivity){
            activity = (BaseActivity) context;
        } else {
            throw new ClassCastException("The activity does not extends " +
                    BaseActivity.class.getSimpleName());
        }
    }

    public void showProgress(){
        activity.showProgress();
    }

    public void hideProgress(){
        activity.hideProgress();
    }

    public boolean isConnected() { return activity.isConnected(); }

    @Override
    public void loadRequestedData() {

    }
}
