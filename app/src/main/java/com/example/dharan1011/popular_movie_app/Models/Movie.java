package com.example.dharan1011.popular_movie_app.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.dharan1011.popular_movie_app.Utils.APIService;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dharan1011 on 19/5/17.
 */

public class Movie implements Serializable{
    @SerializedName("poster_path")
    private String poster_path;
    @SerializedName("adult")
    private boolean isAdult;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String release_date;
    @SerializedName("id")
    private String id;
    @SerializedName("original_title")
    private String original_title;
    @SerializedName("original_language")
    private String original_language;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backdrop_path;
    @SerializedName("popularity")
    private String popularity;
    @SerializedName("vote_count")
    private String vote_count;
    @SerializedName("video")
    private boolean hasVideo;
    @SerializedName("vote_average")
    private String vote_average;


    public String getPoster_path() {

        return APIService.IMAGE_URL+poster_path;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return "Release Date : "+release_date;
    }

    public String getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getVote_count() {
        return vote_count;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public String getVote_average() {
        return "Rating : "+vote_average+"/10";
    }

    @Override
    public String toString() {
        return "Movie{" +
                "poster_path='" + poster_path + '\'' +
                ", isAdult=" + isAdult +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                ", id='" + id + '\'' +
                ", original_title='" + original_title + '\'' +
                ", original_language='" + original_language + '\'' +
                ", title='" + title + '\'' +
                ", backdrop_path='" + backdrop_path + '\'' +
                ", popularity='" + popularity + '\'' +
                ", vote_count='" + vote_count + '\'' +
                ", hasVideo=" + hasVideo +
                ", vote_average='" + vote_average + '\'' +
                '}';
    }

}