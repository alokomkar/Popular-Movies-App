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
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.alokomkar.mymoviesapp.models.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cognitive on 2/11/16.
 */
public class TrailerRecyclerAdapter extends RecyclerView.Adapter<TrailerRecyclerAdapter.TrailerViewHolder> {



    private List<TrailerModel.TrailerResult> mTrailerResultList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private String TAG = TrailerRecyclerAdapter.class.getSimpleName();

    public TrailerRecyclerAdapter(Context mContext, List<TrailerModel.TrailerResult> trailerResultList, OnItemClickListener onItemClickListener) {
        this.mTrailerResultList = trailerResultList;
        this.mContext = mContext;
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View gridView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_view_item, parent, false);
        return new TrailerViewHolder(gridView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        TrailerModel.TrailerResult trailerResult = getItem(position);
        Picasso.with(mContext)
                .load(NetworkApiGenerator.YOU_TUBE_TRILER_BASE_URL + trailerResult.getKey() + "/0.jpg")
                .placeholder(ContextCompat.getDrawable(mContext, android.R.color.holo_blue_dark))
                .error(ContextCompat.getDrawable(mContext, android.R.color.holo_red_dark))
                .into(holder.movieGridItemImageView);
        holder.titleTextView.setText(trailerResult.getName());

    }

    public TrailerModel.TrailerResult getItem(int position) {

        return mTrailerResultList.get(position);
    }

    @Override
    public int getItemCount() {
        return mTrailerResultList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movieGridItemImageView)
        ImageView movieGridItemImageView;

        @Bind(R.id.titleTextView)
        TextView titleTextView;

        @Bind(R.id.movieGridCardView)
        CardView movieGridCardView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //mSelectedItem = getItem( getAdapterPosition() ).getId();
            mOnItemClickListener.onItemClick(v, getAdapterPosition());
            //notifyDataSetChanged();
        }
    }

}

