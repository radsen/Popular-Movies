package com.kzlabs.popularmovies.util;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kzlabs.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by radsen on 11/29/16.
 */

public class BindingUtils {
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url){
        if(TextUtils.isEmpty(url)){
            view.setImageResource(R.drawable.ic_film);
        } else {
            Picasso.with(view.getContext())
                    .load(url)
                    .error(R.drawable.ic_film)
                    .into(view);
        }
    }
}
