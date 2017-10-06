package com.example.dharan1011.popular_movie_app.Utils;

/**
 * Created by dharan1011 on 20/5/17.
 */

import com.example.dharan1011.popular_movie_app.Models.MovieResponse;
import com.example.dharan1011.popular_movie_app.Models.ReviewResponse;
import com.example.dharan1011.popular_movie_app.Models.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dharan1011 on 19/5/17.
 */

public interface APIService {
    String BASE_URL = "http://api.themoviedb.org/3/";
    String API_KEY = "PUT_YOUR_KEY_HERE";
    String IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    @GET("movie/{sort}")
    Call<MovieResponse> getMoviesData(@Path("sort") String sort, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getMovieReviews(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getMovieTrailers(@Path("id") String movieId, @Query("api_key") String apiKey);
}
