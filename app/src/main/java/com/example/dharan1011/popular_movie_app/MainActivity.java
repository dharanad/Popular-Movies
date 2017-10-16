package com.example.dharan1011.popular_movie_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dharan1011.popular_movie_app.Adapters.MoviesAdapter;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.Models.MovieResponse;
import com.example.dharan1011.popular_movie_app.Utils.APIService;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickHandler {

    private static final String EXTRA_OBJECT = "movie-object";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIES_STATE_KEY = "movies_list";

    private static final String SORT_KEY = "sort_key";
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_key";
    private static final String TOP_RATED = "top_rated";
    private static final String POPULAR = "popular";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> mMovieList;
    private String sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        //setup recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.rcv_movie_list);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 2 : 4));

        mMoviesAdapter = new MoviesAdapter(MainActivity.this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        //Shared perefrence to store sort type i.e fetch last sorted list
        sortType = getSharedPreferences(SHARED_PREFERENCE_KEY, 0).getString(SORT_KEY, POPULAR);

        //when deviec configuration changes set adapted data source to persisted movieList
        if (savedInstanceState != null) {
            mMovieList = Parcels.unwrap(savedInstanceState.getParcelable(MOVIES_STATE_KEY));
            mMoviesAdapter.setmMovieList(mMovieList);

        } else {
            // if device is connected to internet
            if (isOnline())
                fetchMovies(sortType);
            else
                //if device offline show AlertDialog
                new AlertDialog.Builder(this).setTitle(R.string.error_connectivity_title).setMessage(R.string.error_connectivity_message)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //persist movieList
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIES_STATE_KEY, Parcels.wrap(mMovieList));
    }
    /*
    * Checks whether device is connected to internet or not
    * */
    public boolean isOnline() {
        Runtime runtime=Runtime.getRuntime();
        try{
            Process ipProcess=runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue=ipProcess.waitFor();
            return (exitValue==0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /*
    * fetch movie based on sort type
    * and create movies list
    * @param sortType -> Popular or Top Rated
    * @returns List<Movie>
    * */
    private void fetchMovies(String sortType) {
        showProgressBar();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);
        Call<MovieResponse> call = service.getMoviesData(sortType, APIService.API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    mMovieList = response.body().getMovieList();
                    if (mMovieList == null || mMovieList.size() == 0) return;
                    mMoviesAdapter.setmMovieList(mMovieList);
                    hideProgressBar();
                }
                if (!response.isSuccessful()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.response_error)
                            .setMessage(response.message() + "\nResponse Code : " + response.code())
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .show();
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.response_failure, Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        });
    }
    /*
    * Callback interface
    * Invoked when movie item from rcv is clicked to start DetailsActivity and pass in movie object as a parcel
    * */
    @Override
    public void onItemClick(Movie movie) {
        Intent i = new Intent(MainActivity.this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, Parcels.wrap(movie));
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //set menu item title based on sort key
        if (sortType.equals(TOP_RATED)) {
            menu.getItem(0).setTitle(getResources().getString(R.string.action_sort_popular));
        } else {
            menu.getItem(0).setTitle(getResources().getString(R.string.action_sort_top_rated));
        }
        return super.onCreateOptionsMenu(menu);
    }
    //update sort key when sort type is changed
    private void updateSortKey() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_KEY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SORT_KEY, sortType);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //fetch movies list based on selected sort ket and persist sort key and update menu item text
        switch (item.getItemId()) {
            case R.id.action_sort:
                if (sortType.equals(TOP_RATED)) {
                    sortType = POPULAR;
                    fetchMovies(sortType);
                    mMoviesAdapter.notifyDataSetChanged();
                    item.setTitle(getResources().getString(R.string.action_sort_top_rated));
                } else {
                    sortType = TOP_RATED;
                    fetchMovies(sortType);
                    mMoviesAdapter.notifyDataSetChanged();
                    item.setTitle(getResources().getString(R.string.action_sort_popular));
                }
                updateSortKey();
                return true;
            case R.id.action_favourite_movies:
                startActivity(new Intent(this, FavouriteMoviesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
