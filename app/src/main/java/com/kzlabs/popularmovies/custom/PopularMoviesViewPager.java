package com.kzlabs.popularmovies.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by radsen on 4/15/17.
 */

public class PopularMoviesViewPager extends ViewPager {

    public PopularMoviesViewPager(Context context) {
        super(context);
    }

    public PopularMoviesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     *  This code was taken from a question on stackoverflow
     *  http://stackoverflow.com/questions/8394681/android-i-am-unable-to-have-viewpager-wrap-content
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
