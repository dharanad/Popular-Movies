package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.dharan1011.popular_movie_app.Models.MovieInfo;
import com.example.dharan1011.popular_movie_app.Utils.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    TextView mMovieTitleTextView;
    private String movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);

        if(getIntent().hasExtra(Intent.EXTRA_TEXT)){
            movieId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchMovieInfo(movieId);
    }

    private void fetchMovieInfo(String movieId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<MovieInfo> call = service.getMovieInfo(movieId,APIService.API_KEY);
        call.enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(@NonNull Call<MovieInfo> call, @NonNull Response<MovieInfo> response) {
                if(response.isSuccessful()){
                    updateUi(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieInfo> call, @NonNull Throwable t) {

            }
        });
    }

    private void updateUi(MovieInfo movieInfo){
        mMovieTitleTextView.setText(movieInfo.getOriginal_title());
    }
}
