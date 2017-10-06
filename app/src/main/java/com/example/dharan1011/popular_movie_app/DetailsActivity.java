package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dharan1011.popular_movie_app.Adapters.MovieReviewAdapter;
import com.example.dharan1011.popular_movie_app.Adapters.MovieTrailerAdapter;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.Models.Review;
import com.example.dharan1011.popular_movie_app.Models.ReviewResponse;
import com.example.dharan1011.popular_movie_app.Models.Trailer;
import com.example.dharan1011.popular_movie_app.Models.TrailerResponse;
import com.example.dharan1011.popular_movie_app.REST.APIService;
import com.example.dharan1011.popular_movie_app.databinding.ActivityDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity implements MovieTrailerAdapter.ItemClickListener {

    public static final String TRAILERS_STATE_KEY = "trailers-key";
    public static final String REVIEWS_STATE_KEY = "review-key";
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String EXTRA_OBJECT = "movie-object";
    Vibrator vibrator;
    List<Review> reviewList;
    List<Trailer> trailerList;
    ActivityDetailsBinding detailsBinding;
    MovieReviewAdapter movieReviewAdapter;
    MovieTrailerAdapter movieTrailerAdapter;
    RecyclerView reviewsRecyclerView, trailersRecyclerView;
    private Movie movieData;
    private String movieId;
    private boolean isFavorite;

    //variables for saving movies to firebase functionality
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setTitle(R.string.title_activity_movie_details);

        //initialize firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("favoriteMovies");

        searchForTheMovieInTheFirebaseDatabase();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        setupRecyclerViewUi();

        // get parcel from passed in intent
        if (getIntent().getExtras() != null) {
            movieData = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_OBJECT));
            movieId = movieData.getId();
        }
        updateUi(movieData);
        /*
        * When device configuration changes and populates the rcv with persisted data
         */
        if (savedInstanceState != null) {
            trailerList = Parcels.unwrap(savedInstanceState.getParcelable(TRAILERS_STATE_KEY));
            reviewList = Parcels.unwrap(savedInstanceState.getParcelable(REVIEWS_STATE_KEY));
            movieReviewAdapter.setReviewList(reviewList);
            movieTrailerAdapter.setTrailerList(trailerList);
            reviewsRecyclerView.setVisibility(View.VISIBLE);
            trailersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            fetchReviews(movieId);
            fetchTrailers(movieId);
        }
    }

    private void searchForTheMovieInTheFirebaseDatabase() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (movieId.equals(child.getKey().toString())) {
                            isFavorite = true;

                            break;

                        } else {
                            isFavorite = false;
                        }
                    }
                }
                toggleFavButton();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerViewUi() {
        //RCV setup
        reviewsRecyclerView = (RecyclerView) findViewById(R.id.rcv_reviews_list);
        movieReviewAdapter = new MovieReviewAdapter(this);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(movieReviewAdapter);

        trailersRecyclerView = (RecyclerView) findViewById(R.id.rcv_trailers_list);
        movieTrailerAdapter = new MovieTrailerAdapter(this);
        trailersRecyclerView.setHasFixedSize(true);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailersRecyclerView.setAdapter(movieTrailerAdapter);
    }

    /*
    * Saves the trailerList and reviewsList
    * */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TRAILERS_STATE_KEY, Parcels.wrap(trailerList));
        outState.putParcelable(REVIEWS_STATE_KEY, Parcels.wrap(reviewList));
    }

    /*
    * Fetches Reviews data corresponding to movieId
    * Response body is a list
    * List is set to adapter as a data source
    * Populates the rcv
    * @param movieId
    */
    private void fetchTrailers(String movieId) {
        detailsBinding.pbTrailersLoading.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<TrailerResponse> call = service.getMovieTrailers(movieId, APIService.API_KEY);
        call.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                detailsBinding.pbTrailersLoading.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    detailsBinding.tvTrailersError.setVisibility(View.VISIBLE);
                    return;
                }
                if (response.body().getResults().size() != 0) {
                    trailersRecyclerView.setVisibility(View.VISIBLE);
                    trailerList = response.body().getResults();
                    movieTrailerAdapter.setTrailerList(trailerList);
                } else
                    detailsBinding.tvTrailersError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                detailsBinding.pbTrailersLoading.setVisibility(View.INVISIBLE);
                detailsBinding.tvTrailersError.setVisibility(View.VISIBLE);
            }
        });

    }

    /*
    * Fetches trailer data corresponding to movieId
    * Response body is a list
    * List is set to adapter as a data source
    * Populates the rcv
    * @param movieId
    * */
    private void fetchReviews(String movieId) {
        detailsBinding.pbReviewsLoading.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<ReviewResponse> call = service.getMovieReviews(movieId, APIService.API_KEY);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                detailsBinding.pbReviewsLoading.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    detailsBinding.tvReviewsError.setVisibility(View.VISIBLE);
                    return;
                }
                if (response.body().getResults().size() != 0) {
                    reviewsRecyclerView.setVisibility(View.VISIBLE);
                    reviewList = response.body().getResults();
                    movieReviewAdapter.setReviewList(reviewList);
                } else
                    detailsBinding.tvReviewsError.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                detailsBinding.pbReviewsLoading.setVisibility(View.INVISIBLE);
                detailsBinding.tvReviewsError.setVisibility(View.VISIBLE);
            }
        });
    }

    /*
    * Updated the UI based on Movie object data*/
    private void updateUi(Movie movieInfo) {
        detailsBinding.tvMovieTitle.setText(movieInfo.getTitle());
        detailsBinding.tvMovieRating.setText(movieInfo.getVote_average());
        detailsBinding.tvMovieOverview.setText(movieInfo.getOverview());
        detailsBinding.tvMovieReleaseDate.setText(movieInfo.getRelease_date());
        Picasso.with(this)
                .load(APIService.IMAGE_URL + movieInfo.getPoster_path())
                .into(detailsBinding.imvMovieThumbnail);
        toggleFavButton();
    }

    /*
    * When favorite button clicked
    * If movie is not favourite and movie is added to database and its isFav column is toogled to 1
    * If movies is favorite, then isFav column in the database is toogled
    * */
    public void toggleFavourites(View v) {
        if (isFavorite) {
            mDatabaseReference.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appSnapshot : dataSnapshot.getChildren()) {
                        appSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(DetailsActivity.this, "Couldn't fetch the data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
            isFavorite = false;
        } else {
            mDatabaseReference.child(movieId).push().setValue(movieId);
            isFavorite = true;
        }
        toggleFavButton();
    }

    /*
    * Toogle the favourite button text label
    * if the movie is favorite list it show label to remove it and vise versa
    * */
    private void toggleFavButton() {
        if (isFavorite) {
            detailsBinding.btnFavouriteMovie.setImageResource(R.drawable.fav);
        } else {
            detailsBinding.btnFavouriteMovie.setImageResource(R.drawable.unfav);
        }
    }


    /*
    * Interface callback
    * Invoked when Movie trailer is clicked
    * Launched youtube to play trailer
    * */
    @Override
    public void onItemClick(String key) {
        String url = getString(R.string.youtube_base_url) + key;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            vibrator.vibrate(100);
            startActivity(intent);
        }
    }
}
