package com.example.dharan1011.popular_movie_app.Models;

import com.example.dharan1011.popular_movie_app.Utils.APIService;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dharan1011 on 20/5/17.
 */

public class MovieInfo {

    @SerializedName("original_title")
    String original_title;
    @SerializedName("overview")
    String overview;
    @SerializedName("vote_average")
    String vote_average;
    @SerializedName("release_date")
    String release_date;
    @SerializedName("poster_path")
    String poster_path;

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return APIService.API_KEY+poster_path;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "original_title='" + original_title + '\'' +
                ", overview='" + overview + '\'' +
                ", vote_average='" + vote_average + '\'' +
                ", release_date='" + release_date + '\'' +
                ", poster_path='" + poster_path + '\'' +
                '}';
    }
}
