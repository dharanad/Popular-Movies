package com.example.dharan1011.popular_movie_app.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dharan1011.popular_movie_app.Models.Review;
import com.example.dharan1011.popular_movie_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharan1011 on 2/6/17.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private Context context;

    public MovieReviewAdapter(Context context) {
        reviewList = new ArrayList<>();
        this.context = context;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.movie_review_item, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviewList.get(position).getAuthor(), reviewList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
            contentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
        }

        public void bind(String author, String content) {
            authorTextView.setText(author);
            contentTextView.setText(content);
        }
    }
}
