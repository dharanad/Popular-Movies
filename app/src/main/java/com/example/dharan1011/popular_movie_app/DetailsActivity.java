package com.example.dharan1011.popular_movie_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.dharan1011.popular_movie_app.Adapters.MovieReviewAdapter;
import com.example.dharan1011.popular_movie_app.Adapters.MovieTrailerAdapter;
import com.example.dharan1011.popular_movie_app.Data.MovieContract;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.Models.ReviewResponse;
import com.example.dharan1011.popular_movie_app.Models.TrailerResponse;
import com.example.dharan1011.popular_movie_app.Utils.APIService;
import com.example.dharan1011.popular_movie_app.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity implements MovieTrailerAdapter.ItemClickListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie movieData;
    private String movieId;
    private boolean isFavorite;
    Vibrator vibrator;

    ActivityDetailsBinding detailsBinding;
    MovieReviewAdapter movieReviewAdapter;
    MovieTrailerAdapter movieTrailerAdapter;
    RecyclerView reviewsRecyclerView, trailersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setTitle(R.string.title_activity_movie_details);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

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


        if (getIntent().hasExtra(MainActivity.EXTRA_OBJECT)) {
            movieData = (Movie) getIntent().getSerializableExtra(MainActivity.EXTRA_OBJECT);
            movieId = movieData.getId();
        } else {
            finish();
        }

        updateUi(movieData);

        fetchReviews(movieId);
        fetchTrailers(movieId);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

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
                    movieTrailerAdapter.setTrailerList(response.body().getResults());
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
                    movieReviewAdapter.setReviewList(response.body().getResults());
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

    public void toggleFavourites(View v) {
        int val;
        if (!isFavorite) {
            //Add to favourite
            if (!isMovieAdded(movieId)) addMovieToDatabase(movieData);
            val = 1;
            isFavorite = true;
        } else {
            //Remove from favourite
            val = 0;
            isFavorite = false;
        }
        ContentValues c = new ContentValues();
        c.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        c.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieData.getTitle());
        c.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV, val);
        getContentResolver().update(ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.parseLong(movieId)),
                c,
                null,
                null);
        toggleFavButton();
    }


    private void updateUi(Movie movieInfo) {
        detailsBinding.tvMovieTitle.setText(movieInfo.getOriginal_title());
        detailsBinding.tvMovieRating.setText(movieInfo.getVote_average());
        detailsBinding.tvMovieOverview.setText(movieInfo.getOverview());
        detailsBinding.tvMovieReleaseDate.setText(movieInfo.getRelease_date());
        Picasso.with(this)
                .load(movieInfo.getPoster_path())
                .into(detailsBinding.imvMovieThumbnail);

        isFavorite = (isMovieAdded(movieInfo.getId())) && isFavourite(movieId);
        toggleFavButton();
    }

    private void toggleFavButton() {
        if (isFavorite)
            detailsBinding.btnFavouriteMovie.setText(getString(R.string.remove_favorite_label));
        else
            detailsBinding.btnFavouriteMovie.setText(R.string.add_favourite_label);
    }

    private boolean isFavourite(String id) {
        Cursor c = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.parseLong(id)),
                null,
                null,
                null, null);
        return c != null && c.getCount() != 0 && c.moveToNext() && c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAV)) == 1;
    }

    private boolean isMovieAdded(String id) {
        Cursor cursor = getContentResolver().query(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Long.parseLong(id)),
                null,
                null,
                null,
                null);
        return cursor != null && cursor.getCount() == 1;
    }

    private void addMovieToDatabase(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

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
