package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.example.dharan1011.popular_movie_app.Adapters.MoviesAdapter;
import com.example.dharan1011.popular_movie_app.Data.MovieContract;
import com.example.dharan1011.popular_movie_app.Models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.ItemClickHandler {
    public static final int LOADER_ID = 3000;
    public static final String TAG = FavouriteMoviesActivity.class.getSimpleName();
    private static final String EXTRA_OBJECT = "movie-object";
    private static final String MOVIES_STATE_KEY = "movies_list";
    RecyclerView recyclerView;
    MoviesAdapter mMoviesAdapter;
    List<Movie> mMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);
        getSupportActionBar().setTitle(getString(R.string.title_activity_favourite_movies));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rcv_favourite_movies_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));

        mMoviesAdapter = new MoviesAdapter(this);

        recyclerView.setAdapter(mMoviesAdapter);

        //When device configuration changes populate rcv with persisted data source
        //else fetch data from database
        if (savedInstanceState != null) {
            mMovieList = Parcels.unwrap(savedInstanceState.getParcelable(MOVIES_STATE_KEY));
            mMoviesAdapter.setmMovieList(mMovieList);
        } else {

            if (getSupportLoaderManager().getLoader(LOADER_ID) == null)
                getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            else
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }

    }

    //Persist movieList
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIES_STATE_KEY, Parcels.wrap(mMovieList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // Fetch favourite movies from database
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
        mMovieList = parseCursor(data);
        mMoviesAdapter.setmMovieList(mMovieList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.setmMovieList(null);
    }

    /*
    * Parses the cursor and creats and list of Movie objects
    * @params Movie object
    * @return List<Movie>
    * */
    private List<Movie> parseCursor(Cursor cursor) {
        if (cursor == null) return null;
        List<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            Log.d(TAG, "parseCursor: " + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
            movie.setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)));
            movie.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movie.setRelease_date(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setVote_average(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));

            movieList.add(movie);
        }
        return movieList;
    }

    /*
    * Interface callback.
    * Invoked when Movie item in clicked the FavouriteMovieActivity to start DetailsActivity
    * */
    @Override
    public void onItemClick(Movie movie) {
        Intent i = new Intent(FavouriteMoviesActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, Parcels.wrap(movie));
        i.putExtras(bundle);
        startActivity(i);
    }

}
