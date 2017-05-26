package com.example.dharan1011.popular_movie_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    TextView movieTitleTextView,movieRatingTextView,movieOverviewTextView,movieReleaseDataTextView;
    ImageView movieThumbnailImageView;
    private Movie movieData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setupUi();
        if(getIntent().hasExtra(MainActivity.EXTRA_OBJECT)){
            movieData = (Movie) getIntent().getSerializableExtra(MainActivity.EXTRA_OBJECT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi(movieData);
    }

    private void updateUi(Movie movieInfo) {
        movieTitleTextView.setText(movieInfo.getOriginal_title());
        movieRatingTextView.setText(movieInfo.getVote_average());
        movieOverviewTextView.setText(movieInfo.getOverview());
        movieReleaseDataTextView.setText(movieInfo.getRelease_date());
        Picasso.with(this)
                .load(movieInfo.getPoster_path())
                .into(movieThumbnailImageView);
    }
    private void setupUi() {
        movieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        movieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        movieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        movieThumbnailImageView = (ImageView) findViewById(R.id.imv_movie_thumbnail);
        movieReleaseDataTextView = (TextView) findViewById(R.id.tv_movie_release_date);
    }
}
