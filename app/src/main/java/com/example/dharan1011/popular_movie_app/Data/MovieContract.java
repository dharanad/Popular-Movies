package com.example.dharan1011.popular_movie_app.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dharan1011 on 28/5/17.
 */

public class MovieContract {
    public static final String AUTHORITY = "com.example.dharan1011.popular_movie_app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH = "movie";

    private MovieContract() {
    }

    public static class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH).build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_IS_FAV = "is_favourite";
    }
}
