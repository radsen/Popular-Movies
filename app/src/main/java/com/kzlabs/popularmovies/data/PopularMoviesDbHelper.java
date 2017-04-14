package com.kzlabs.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.kzlabs.popularmovies.data.PopularMoviesContract.PopularMoviesEntry;
/**
 * Created by radsen on 4/6/17.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 9;

    private static final String ALTER_TABLE_ADD_COLUMN_FAV = "ALTER TABLE " +
            PopularMoviesEntry.TABLE_NAME + " ADD COLUMN " + PopularMoviesEntry.FAV +
            " INTEGER NOT NULL DEFAULT 0 ";

    private static final String ALTER_TABLE_ADD_COLUMN_CATEGORY = "ALTER TABLE " +
            PopularMoviesEntry.TABLE_NAME + " ADD COLUMN " + PopularMoviesEntry.CATEGORY +
            " INTEGER NOT NULL DEFAULT 1 ";

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
                PopularMoviesEntry.CATEGORY     + " INTEGER NOT NULL DEFAULT 1, " +
                PopularMoviesEntry.FAV          + " INTEGER NOT NULL DEFAULT 0, " +
                " UNIQUE (" + PopularMoviesEntry._ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(oldVersion < 8){
            sqLiteDatabase.execSQL(ALTER_TABLE_ADD_COLUMN_FAV);
        }

        if(oldVersion < 9){
            sqLiteDatabase.execSQL(ALTER_TABLE_ADD_COLUMN_CATEGORY);
        }
    }
}
