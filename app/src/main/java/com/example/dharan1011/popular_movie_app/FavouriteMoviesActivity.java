package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.dharan1011.popular_movie_app.Adapters.MoviesAdapter;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMoviesActivity extends AppCompatActivity implements MoviesAdapter.ItemClickHandler {
    public static final String TAG = FavouriteMoviesActivity.class.getSimpleName();
    private static final String EXTRA_OBJECT = "movie-object";
    private static final String MOVIES_STATE_KEY = "movies_list";
    RecyclerView recyclerView;
    MoviesAdapter mMoviesAdapter;
    List<Movie> mMovieList;

    //variables for retrieving movies to firebase functionality
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);
        getSupportActionBar().setTitle(getString(R.string.title_activity_favourite_movies));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("favoriteMovies");

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
        } else mMovieList = new ArrayList<>();
        searchForFavoritedMoviesInTheFirebaseDatabase();
    }

    private void searchForFavoritedMoviesInTheFirebaseDatabase() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey().toString();
                        String poster_path = child.child("posterPath").getValue().toString();
                        Movie movie = new Movie(id, poster_path);
                        mMovieList.add(movie);
                    }
                }
                mMoviesAdapter.setmMovieList(mMovieList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
