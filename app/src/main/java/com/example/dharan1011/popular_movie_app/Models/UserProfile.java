package com.example.dharan1011.popular_movie_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHISHEK RAJ on 9/5/2017.
 */

public class UserProfile implements Parcelable {
    @SuppressWarnings("unused")
    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
    private String usersName;
    private String usersCity;
    private String usersEmail;
    private String usersGender;

    public UserProfile(String name, String city, String gender, String email) {
        usersName = name;
        usersCity = city;
        usersGender = gender;
        usersEmail = email;
    }

    public UserProfile(Parcel in) {
        usersName = in.readString();
        usersCity = in.readString();
        usersEmail = in.readString();
        usersGender = in.readString();
    }

    public String getUsersName() {
        return usersName;
    }

    public String getUsersCity() {
        return usersCity;
    }

    public String getUsersEmail() {
        return usersEmail;
    }

    public String getUsersGender() {
        return usersGender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(usersName);
        dest.writeString(usersCity);
        dest.writeString(usersEmail);
        dest.writeString(usersGender);
    }
}
