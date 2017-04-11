package com.kzlabs.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
/**
 * Created by radsen on 4/6/17.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 4;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MOVIE_TABLE = " CREATE TABLE " + PopularMoviesEntry.TABLE_NAME + " (" +
                PopularMoviesEntry._ID          + " INTEGER PRIMARY KEY, " +
                PopularMoviesEntry.TITLE        + " TEXT NOT NULL, " +
                PopularMoviesEntry.POSTER       + " TEXT NOT NULL, " +
                PopularMoviesEntry.IMAGE        + " BLOB, " +
                PopularMoviesEntry.RUNTIME      + " INTEGER NOT NULL, " +
                PopularMoviesEntry.SYNOPSIS     + " TEXT NOT NULL, " +
                PopularMoviesEntry.AVERAGE      + " REAL NOT NULL, " +
                PopularMoviesEntry.RELEASE_DATE + " DATETIME NOT NULL, " +
                " UNIQUE (" + PopularMoviesEntry._ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                PopularMoviesContract.PopularMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
