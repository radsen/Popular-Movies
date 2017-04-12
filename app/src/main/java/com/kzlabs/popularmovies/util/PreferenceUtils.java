package com.kzlabs.popularmovies.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.interfaces.MovieConstants;

/**
 * Created by radsen on 4/11/17.
 */

public class PreferenceUtils implements MovieConstants {

    public static String getQuery(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(context.getString(R.string.preferences_file),
                        Context.MODE_PRIVATE);
        String sQuery = preferences.getString(MENU_KEY, context.getString(R.string.popular_path_key));
        return sQuery;
    }

    public static void setQuery(Context context, String value){
        SharedPreferences preferences =
                context.getSharedPreferences(context.getString(R.string.preferences_file),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MENU_KEY, value);
        editor.apply();
    }
}
