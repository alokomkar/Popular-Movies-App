package com.alokomkar.mymoviesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailsFragment extends Fragment {

    private static final String MOVIE_RESULT = "movie_result";
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
    @Bind(R.id.movieRatingBar)
    AppCompatRatingBar mMovieRatingBar;
    @Bind(R.id.languageTextView)
    TextView mLanguageTextView;

    private MovieModel.MovieResult mMovieResult;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            mMovieResult = savedInstanceState.getParcelable(MOVIE_RESULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMovieResult = bundle.getParcelable(MovieModel.class.getSimpleName());
                setValues(mMovieResult);
            }
        } else {
            mMovieResult = savedInstanceState.getParcelable(MOVIE_RESULT);
            setValues(mMovieResult);
        }


        return view;
    }

    private void setValues(MovieModel.MovieResult movieResult) {

        mTitleTextView.setText(movieResult.getOriginalTitle());
        Picasso.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + movieResult.getPosterPath())
                .placeholder(ContextCompat.getDrawable(getActivity(), android.R.color.holo_blue_dark))
                .error(ContextCompat.getDrawable(getActivity(), android.R.color.holo_red_dark))
                .into(mMoviePosterImageView);
        Picasso.with(getActivity()).load(NetworkApiGenerator.IMAGE_BASE_URL + movieResult.getBackdropPath())
                .fit()
                .centerCrop()
                .placeholder(ContextCompat.getDrawable(getActivity(), android.R.color.holo_blue_dark))
                .error(ContextCompat.getDrawable(getActivity(), android.R.color.holo_red_dark))
                .into(mMovieImageView);
        mMovieRatingBar.setRating(Float.valueOf(movieResult.getVoteAverage().toString()));
        mReleaseDateTextView.setText(movieResult.getReleaseDate());
        mVoteAverageTextView.setText(String.valueOf(movieResult.getVoteAverage()) + "/10");
        mSynopsisTextView.setText(movieResult.getOverview());
        mLanguageTextView.setText(movieResult.getOriginalLanguage());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //Reference : http://code.hootsuite.com/orientation-changes-on-android/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_RESULT, mMovieResult);
    }
}
