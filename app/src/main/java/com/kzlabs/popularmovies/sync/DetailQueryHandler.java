package com.kzlabs.popularmovies.sync;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

/**
 * Created by radsen on 4/13/17.
 */

public class DetailQueryHandler extends AsyncQueryHandler {

    private final DetailQueryHandlerListener listener;

    public interface DetailQueryHandlerListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
        void onUpdateComplete(int token, Object cookie, int result);
    }

    public DetailQueryHandler(ContentResolver cr, DetailQueryHandlerListener listener) {
        super(cr);
        this.listener = listener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if(listener != null){
            listener.onQueryComplete(token, cookie, cursor);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        if(listener != null){
            listener.onUpdateComplete(token, cookie, result);
        }
    }
}
