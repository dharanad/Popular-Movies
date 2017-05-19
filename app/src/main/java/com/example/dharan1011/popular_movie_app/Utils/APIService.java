package com.example.dharan1011.popular_movie_app.Utils;

/**
 * Created by dharan1011 on 20/5/17.
 */

import com.example.dharan1011.popular_movie_app.Models.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by dharan1011 on 19/5/17.
 */

public interface APIService {
    String BASE_URL = "http://api.themoviedb.org/3/";
    String API_KEY = "11898a7d9c90cddb95d62371e94df5fc";
    String IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    @GET("movie/{sort}")
    Call<Data> getMovies(@Path("sort") String sort, @Query("api_key") String apiKey);
}
