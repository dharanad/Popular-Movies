package com.example.dharan1011.popular_movie_app.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by dharan1011 on 28/5/17.
 */

public class MovieContentProvider extends ContentProvider {
    private static final int MOVIE = 200;
    private static final int MOVIE_WITH_ID = 201;
    UriMatcher mUriMatcher = builUriMatcher();
    MovieDbHelper movieDbHelper;

    private UriMatcher builUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH, MOVIE);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH + "/#", MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        movieDbHelper = new MovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri " + uri);
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri uri1;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id != -1) uri1 = ContentUris.withAppendedId(uri, id);
                else throw new UnsupportedOperationException("Unsupported Uri " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported Uri " + uri);
        }
        if (uri1 != null) getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return uri1;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleteRows = 0;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                deleteRows = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId});
                break;
            case MOVIE:
                deleteRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported URI " + uri);
        }
        if (deleteRows > 0) getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return deleteRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (mUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                updatedRows = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI " + uri);
        }
        if (updatedRows > 0) getContext().getContentResolver().notifyChange(uri, null);

        return updatedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int insertedRows = 0;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues v : values) {
                        long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, v);
                        if (id != -1) {
                            insertedRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI " + uri);
        }
        if (insertedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return insertedRows;
    }

}
