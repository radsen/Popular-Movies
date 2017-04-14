package com.kzlabs.popularmovies.util;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.kzlabs.popularmovies.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by radsen on 11/29/16.
 */

public class BindingUtils {

    public static final String TAG = BindingUtils.class.getSimpleName();

    @BindingAdapter({"imageUrl"})
    public static void loadImage(final ImageView view, final String url){
        if(TextUtils.isEmpty(url)){
            view.setImageResource(R.drawable.ic_film);
        } else {
            Picasso.with(view.getContext())
                    .load(url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded from cache: " + url);
                        }

                        @Override
                        public void onError() {
                            Picasso.with(view.getContext())
                                    .load(url)
                                    .into(view, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "Image loaded from the web: " + url);
                                        }

                                        @Override
                                        public void onError() {
                                            Log.d(TAG, "Unable to load image");
                                        }
                                    });
                        }
                    });
        }
    }
}
