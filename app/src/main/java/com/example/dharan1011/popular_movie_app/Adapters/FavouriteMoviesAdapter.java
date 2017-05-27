package com.example.dharan1011.popular_movie_app.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dharan1011.popular_movie_app.Data.MovieContract;
import com.example.dharan1011.popular_movie_app.R;

import java.util.zip.Inflater;

/**
 * Created by dharan1011 on 1/6/17.
 */

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.MovieViewHolder>{
    private Context context;
    private Cursor cursor;

    public FavouriteMoviesAdapter(Context context,Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.favourite_movie_item,parent,false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)) return;
        holder.bind(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)),null);

        holder.itemView.setTag(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
    }

    @Override
    public int getItemCount() {
        return (cursor != null)?cursor.getCount():0;
    }

    public void swapCursor(Cursor cursor){
        if(this.cursor != null) this.cursor.close();
        this.cursor = cursor;
        if(this.cursor != null )this.notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView movieTitle;
        public MovieViewHolder(View itemView) {
            super(itemView);
            movieTitle = (TextView) itemView.findViewById(R.id.tv_item_movie_title);
        }

        public void bind(String title,String imageUrl){
            movieTitle.setText(title);
        }
    }
}
