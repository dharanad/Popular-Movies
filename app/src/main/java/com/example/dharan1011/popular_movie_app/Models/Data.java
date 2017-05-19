package com.example.dharan1011.popular_movie_app.Models;

/**
 * Created by dharan1011 on 20/5/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Data {
    @SerializedName("page")
    String pageNumber;
    @SerializedName("results")
    List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }
}
