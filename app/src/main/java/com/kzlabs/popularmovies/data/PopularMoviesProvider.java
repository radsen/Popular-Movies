package com.kzlabs.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;

/**
 * Created by radsen on 4/6/17.
 */

public class PopularMoviesProvider extends ContentProvider {

    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int CODE_MOVIES_WITH_PATH = 102;

    private PopularMoviesDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_MOVIE, CODE_MOVIE);

        uriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        uriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY,
                PopularMoviesContract.PATH_MOVIE + "/*", CODE_MOVIES_WITH_PATH);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {

        int code = sUriMatcher.match(uri);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;
        switch (code){
            case CODE_MOVIE:
                cursor = db.query(PopularMoviesEntry.TABLE_NAME,
                        PopularMoviesEntry.PROJECTION,
                        null,
                        null,
                        null,
                        null,
                        PopularMoviesEntry.RELEASE_DATE);
                break;
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                cursor = db.query(PopularMoviesEntry.TABLE_NAME,
                        PopularMoviesEntry.PROJECTION,
                        PopularMoviesEntry._ID + " = ?",
                        new String[]{ movieId },
                        null,
                        null,
                        PopularMoviesEntry.RELEASE_DATE);
                break;
            case CODE_MOVIES_WITH_PATH:
                String path = uri.getLastPathSegment();

                String selection = null;
                String[] selectionArgs = new String[0];
                if(getContext().getString(R.string.popular_path_key).equals(path)){
                    selection = PopularMoviesEntry.CATEGORY + " = ?  ";
                    selectionArgs = new String[]{ String.valueOf(1) };
                } else if (getContext().getString(R.string.top_path_key).equals(path)) {
                    selection = PopularMoviesEntry.CATEGORY + " = ?  ";
                    selectionArgs = new String[]{ String.valueOf(2) };
                } else if (getContext().getString(R.string.favorite_key).equals(path)) {
                    selection = PopularMoviesEntry.FAV + " = ?  ";
                    selectionArgs = new String[]{ String.valueOf(1) };
                }

                cursor = db.query(PopularMoviesEntry.TABLE_NAME,
                        PopularMoviesEntry.PROJECTION,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        PopularMoviesEntry.RELEASE_DATE);
                break;

        }

        if(cursor != null && cursor.getCount() > 0){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for(ContentValues value : values){
                        long id = db.insert(PopularMoviesEntry.TABLE_NAME, null, value);

                        if(id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rows = 0;
        int code = sUriMatcher.match(uri);
        switch (code){
            case CODE_MOVIE_WITH_ID:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                rows = db.insert(PopularMoviesEntry.TABLE_NAME, null, contentValues);
                break;
        }

        if(rows > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return PopularMoviesContract.BASE_CONTENT_URI
                .buildUpon()
                .appendPath(contentValues.getAsString(PopularMoviesEntry._ID))
                .build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int rows = 0;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE_WITH_ID:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String id = uri.getLastPathSegment();
                String selection = PopularMoviesEntry._ID + " = ?";
                String[] selectionArgs = new String[]{ id };
                rows = db.delete(PopularMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }

        if(rows > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        int rows = 0;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE_WITH_ID:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                String id = uri.getLastPathSegment();
                String selection = PopularMoviesEntry._ID + " = ?";
                String[] selectionArgs = new String[]{ id };

                rows = db.update(PopularMoviesEntry.TABLE_NAME, contentValues,
                        selection, selectionArgs);

                break;
        }

        if(rows > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}
