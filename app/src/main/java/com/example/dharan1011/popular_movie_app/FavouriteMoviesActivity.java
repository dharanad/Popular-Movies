package com.example.dharan1011.popular_movie_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.dharan1011.popular_movie_app.Adapters.FavouriteMoviesAdapter;
import com.example.dharan1011.popular_movie_app.Data.MovieContract;

public class FavouriteMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = FavouriteMoviesActivity.class.getSimpleName();
    public static final int LOADER_ID = 3000;
    RecyclerView recyclerView;
    FavouriteMoviesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);
        getSupportActionBar().setTitle(R.string.title_activity_favourite_movies);

        recyclerView = (RecyclerView) findViewById(R.id.rcv_favourite_movie_list);
        recyclerView.setHasFixedSize(true);
        adapter = new FavouriteMoviesAdapter(this, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String movieId = (String) viewHolder.itemView.getTag();
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV, 0);
                getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build()
                        , values, null, null);
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV + "=?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
