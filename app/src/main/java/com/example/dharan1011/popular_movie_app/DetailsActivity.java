package com.example.dharan1011.popular_movie_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.dharan1011.popular_movie_app.Data.MovieContract;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity{
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie movieData;
    private String movieId;
    private boolean isFavorite;

    ActivityDetailsBinding detailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_details);
        getSupportActionBar().setTitle(R.string.title_movie_details);

        if(getIntent().hasExtra(MainActivity.EXTRA_OBJECT)){
            movieData = (Movie) getIntent().getSerializableExtra(MainActivity.EXTRA_OBJECT);
            movieId = movieData.getId();
        }

        if(!isMovieAdded(movieId)) addMovieToDatabase(movieData);

        updateUi(movieData);

        detailsBinding.btnFavouriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    //Add to favourite
                    ContentValues c = new ContentValues();
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movieId);
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,movieData.getTitle());
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV,1);
                    int i = getContentResolver().update(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,Long.parseLong(movieId)),
                            c,
                            null,
                            null);
                    isFavorite = true;
                    toggleFavButton();
                    Log.d(TAG, "rows updated: "+i);
                }else{
                    //Remove from favourite
                    ContentValues c = new ContentValues();
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movieId);
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,movieData.getTitle());
                    c.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV,0);
                    int i = getContentResolver().update(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,Long.parseLong(movieId)),
                            c,
                            null,
                            null);
                    isFavorite = false;
                    toggleFavButton();
                    Log.d(TAG, "rows updated: "+i);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUi(Movie movieInfo) {
        detailsBinding.tvMovieTitle.setText(movieInfo.getOriginal_title());
        detailsBinding.tvMovieRating.setText(movieInfo.getVote_average());
        detailsBinding.tvMovieOverview.setText(movieInfo.getOverview());
        detailsBinding.tvMovieReleaseDate.setText(movieInfo.getRelease_date());
        Picasso.with(this)
                .load(movieInfo.getPoster_path())
                .into(detailsBinding.imvMovieThumbnail);

        isFavorite = isFavourite(movieId);

        toggleFavButton();
    }

    private void toggleFavButton() {
        if(isFavorite)
            detailsBinding.btnFavouriteMovie.setText("Added To Fav");
        else
            detailsBinding.btnFavouriteMovie.setText("Add To Fav");
    }

    private boolean isFavourite(String id) {
        Cursor c = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.parseLong(id)),
                null,
                null,
                null, null);
        return c != null && c.getCount() != 0 && c.moveToNext() && c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV)) == 1;
    }

    //TODO
    private void queryMovie(String id){
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,Long.parseLong(id)),
                null,
                null,
                null,
                null);
        Log.d(TAG, "queryMovie: "+cursor.getCount());
        while (cursor.moveToNext()){
            Log.d(TAG, "queryMovie: "+cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
        }
    }

    private boolean isMovieAdded(String id){
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,Long.parseLong(id)),
                null,
                null,
                null,
                null);
        return cursor != null && cursor.getCount() == 1;
    }

    private void addMovieToDatabase(Movie movie){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,movie.getTitle());
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,values);
    }
}
