package com.example.dharan1011.popular_movie_app.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by dharan1011 on 2/6/17.
 */

@Parcel
public class Trailer {
    @SerializedName("key")
    public String key;
    @SerializedName("name")
    public String name;
    @SerializedName("site")
    public String site;
    @SerializedName("type")
    public String type;

    public Trailer() {
    }

    public Trailer(String key, String name, String site, String type) {
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Trailer{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
