package com.example.dharan1011.popular_movie_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dharan1011.popular_movie_app.Adapters.MoviesAdapter;
import com.example.dharan1011.popular_movie_app.Models.Data;
import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.Utils.APIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickHandler{

    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    MoviesAdapter mMoviesAdapter;
    List<Movie> mMovieList;
    APIService service;
    private String sortType;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY = "SORT_KEY";
    private static final String SHARED_PREFERENCE_KEY = "shared_preference_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.rcv_movie_list);

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMoviesAdapter = new MoviesAdapter(MainActivity.this);
        mRecyclerView.setAdapter(mMoviesAdapter);

//        if(!getSharedPreferences(SHARED_PREFERENCE_KEY,0).contains(KEY)){
//         sortType = "popular";
//        }else{
            sortType = getSharedPreferences(SHARED_PREFERENCE_KEY,0).getString(KEY,"popular");
//        }
        fetchContent(sortType);


    }

    public void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void hideProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchContent(String sortType){
        showProgressBar();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);
        Call<Data> call = service.getMovies(sortType,APIService.API_KEY);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if(response.isSuccessful()) {
                    mMovieList = response.body().getMovieList();
                    mMoviesAdapter.setmMovieList(mMovieList);
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d(TAG, "onFailure: Call Failed");
                //TODO handle Failure
                Toast.makeText(MainActivity.this, "Couldn't Fetch Content", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        });
    }

    @Override
    public void onItemClick(Movie movie) {
        //TODO : Call Detail Activity and pass the movie object as parameter
        Intent i = new Intent(MainActivity.this,DetailsActivity.class);
        //TODO set data to be passed
//        i.putExtra()
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                sortType = "top_rated";
                fetchContent(sortType);
                mMoviesAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_sort:
                sortType = "popular";
                fetchContent(sortType);
                mMoviesAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_KEY,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY,sortType);
        editor.commit();
    }
}
