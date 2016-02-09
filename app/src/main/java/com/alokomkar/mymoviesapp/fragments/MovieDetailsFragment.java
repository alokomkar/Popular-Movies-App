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
    TextView titleTextView;
    @Bind(R.id.moviePosterImageView)
    ImageView moviePosterImageView;
    @Bind(R.id.releaseDateTextView)
    TextView releaseDateTextView;
    @Bind(R.id.voteAverageTextView)
    TextView voteAverageTextView;
    @Bind(R.id.synopsisTextView)
    TextView synopsisTextView;
    @Bind(R.id.movieImageView)
    ImageView movieImageView;
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
            titleTextView.setText(mMovieResult.getOriginalTitle());
            Glide.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + mMovieResult.getPosterPath())
                    .asBitmap()
                    .override(150, 220)
                    .fitCenter()
                    .placeholder(ContextCompat.getDrawable(getActivity(), android.R.color.holo_orange_light))
                    .into(moviePosterImageView);
            Glide.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + mMovieResult.getBackdropPath())
                    .asBitmap()
                    .centerCrop()
                    .into(movieImageView);
            releaseDateTextView.setText(mMovieResult.getReleaseDate());
            voteAverageTextView.setText(String.valueOf(mMovieResult.getVoteAverage()) + "/10");
            synopsisTextView.setText(mMovieResult.getOverview());
        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
