package com.example.dharan1011.popular_movie_app.Adapters;

/**
 * Created by dharan1011 on 20/5/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dharan1011.popular_movie_app.Models.Movie;
import com.example.dharan1011.popular_movie_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dharan1011 on 19/5/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieItemViewHolder> {
    private final static String TAG = MoviesAdapter.class.getSimpleName();

    public interface ItemClickHandler {
        void onItemClick(Movie movie);
    }

    private List<Movie> mMovieList;
    private ItemClickHandler itemClickHandler;

    public MoviesAdapter(ItemClickHandler itemClickHandler) {
        this.itemClickHandler = itemClickHandler;
        mMovieList = new ArrayList<Movie>();
    }


    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        holder.bind(getMovieList().get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    private List<Movie> getMovieList() {
        return mMovieList;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
        notifyDataSetChanged();
    }

    public class MovieItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieItemImageView;

        public MovieItemViewHolder(View itemView) {
            super(itemView);
            movieItemImageView = (ImageView) itemView.findViewById(R.id.imv_movie_item);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            Picasso.with(itemView.getContext())
                    .load(movie.getPoster_path())
                    .placeholder(R.drawable.ic_place_holder)
                    .into(movieItemImageView);
        }

        @Override
        public void onClick(View v) {
            itemClickHandler.onItemClick(mMovieList.get(getAdapterPosition()));
        }
    }
}
