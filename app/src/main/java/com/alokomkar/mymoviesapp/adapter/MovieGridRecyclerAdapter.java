package com.alokomkar.mymoviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.bumptech.glide.Glide;

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

    public MovieGridRecyclerAdapter( Context mContext, List<MovieModel.MovieResult> mMovieResultArrayList, OnItemClickListener onItemClickListener) {
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
        Glide.with(mContext).load( NetworkApiGenerator.IMAGE_BASE_URL + movieResult.getPosterPath() ).asBitmap().into(holder.movieGridItemImageView);
    }

    public MovieModel.MovieResult getItem(int position) {

        return mMovieResultArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return mMovieResultArrayList.size();
    }

    public class MovieGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movieGridItemImageView)
        ImageView movieGridItemImageView;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick( v, getAdapterPosition() );
        }
    }

}
