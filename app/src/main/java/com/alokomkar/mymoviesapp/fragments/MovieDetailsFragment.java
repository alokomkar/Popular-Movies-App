package com.alokomkar.mymoviesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailsFragment extends Fragment {

    @Bind(R.id.titleTextView)
    TextView mTitleTextView;
    @Bind(R.id.moviePosterImageView)
    ImageView mMoviePosterImageView;
    @Bind(R.id.releaseDateTextView)
    TextView mReleaseDateTextView;
    @Bind(R.id.voteAverageTextView)
    TextView mVoteAverageTextView;
    @Bind(R.id.synopsisTextView)
    TextView mSynopsisTextView;
    @Bind(R.id.movieImageView)
    ImageView mMovieImageView;
    private MovieModel.MovieResult mMovieResult;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieResult = bundle.getParcelable(MovieModel.class.getSimpleName());
            mTitleTextView.setText(mMovieResult.getOriginalTitle());
            Glide.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + mMovieResult.getPosterPath())
                    .asBitmap()
                    .override(150, 220)
                    .fitCenter()
                    .placeholder(ContextCompat.getDrawable(getActivity(), android.R.color.holo_orange_light))
                    .into(mMoviePosterImageView);
            Glide.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + mMovieResult.getBackdropPath())
                    .asBitmap()
                    .centerCrop()
                    .into(mMovieImageView);
            mReleaseDateTextView.setText(mMovieResult.getReleaseDate());
            mVoteAverageTextView.setText(String.valueOf(mMovieResult.getVoteAverage()) + "/10");
            mSynopsisTextView.setText(mMovieResult.getOverview());
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
