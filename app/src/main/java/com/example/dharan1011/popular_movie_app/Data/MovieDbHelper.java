package com.example.dharan1011.popular_movie_app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dharan1011.popular_movie_app.Data.MovieContract.MovieEntry;

/**
 * Created by dharan1011 on 28/5/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "+ MovieEntry.TABLE_NAME+" ("+
                MovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_MOVIE_ID+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_TITLE+" TEXT NOT NULL,"+
                MovieEntry.COLUMN_MOVIE_IS_FAV+" INTEGER NOT NULL DEFAULT 0"+
                ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
