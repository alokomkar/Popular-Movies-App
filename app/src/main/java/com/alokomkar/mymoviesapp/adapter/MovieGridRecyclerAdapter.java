package com.alokomkar.mymoviesapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.models.MovieModel;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cognitive on 2/8/16.
 */
public class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridRecyclerAdapter.MovieGridViewHolder> {



    private List<MovieModel.MovieResult> mMovieResultArrayList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private String TAG = MovieGridRecyclerAdapter.class.getSimpleName();
    private Integer mSelectedItem = null;

    public MovieGridRecyclerAdapter(Context mContext, List<MovieModel.MovieResult> mMovieResultArrayList, OnItemClickListener onItemClickListener) {
        this.mMovieResultArrayList = mMovieResultArrayList;
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gridView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_view_item, parent, false);
        return new MovieGridViewHolder(gridView);
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        MovieModel.MovieResult movieResult = getItem(position);
        Picasso.with(mContext)
                .load(NetworkApiGenerator.IMAGE_BASE_URL + movieResult.getPosterPath())
                .placeholder(ContextCompat.getDrawable(mContext, android.R.color.holo_blue_dark))
                .error(ContextCompat.getDrawable(mContext, android.R.color.holo_red_dark))
                .into(holder.movieGridItemImageView);
        holder.titleTextView.setText(movieResult.getTitle());
        /*if( mSelectedItem != null ) {
            holder.movieGridCardView.setCardBackgroundColor( movieResult.getMovieId().equals( mSelectedItem ) ? android.R.color.darker_gray : android.R.color.transparent );
        }*/
    }

    public MovieModel.MovieResult getItem(int position) {

        return mMovieResultArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        if( mMovieResultArrayList == null ) {
            return 0;
        }
        return mMovieResultArrayList.size();
    }

    public class MovieGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movieGridItemImageView)
        ImageView movieGridItemImageView;

        @Bind(R.id.titleTextView)
        TextView titleTextView;

        @Bind(R.id.movieGridCardView)
        CardView movieGridCardView;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //mSelectedItem = getItem( getAdapterPosition() ).getMovieId();
            mOnItemClickListener.onItemClick(v, getAdapterPosition());
            //notifyDataSetChanged();
        }
    }

}
