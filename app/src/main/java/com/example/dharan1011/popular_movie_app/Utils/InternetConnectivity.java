package com.example.dharan1011.popular_movie_app.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ABHISHEK RAJ on 9/5/2017.
 */

public class InternetConnectivity {
    private static boolean isConnected;

    public static boolean isInternetConnected(Context context) {

        isConnected = false;

        //code below referenced from: https://stackoverflow.com/a/32771164/5770629

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                isConnected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                isConnected = true;
            }
            return isConnected;
        } else {
            // not connected to the internet
            return false;
        }
    }
}