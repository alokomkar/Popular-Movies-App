package com.alokomkar.mymoviesapp.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.adapter.TrailerRecyclerAdapter;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.alokomkar.mymoviesapp.interfaces.retrofit.MovieServiceInterface;
import com.alokomkar.mymoviesapp.models.MovieModel;
import com.alokomkar.mymoviesapp.models.ReviewsModel;
import com.alokomkar.mymoviesapp.models.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    @Bind(R.id.noTrailersTextView)
    TextView mNoTrailersTextView;
    @Bind(R.id.movieTrailerRecyclerView)
    RecyclerView mMovieTrailerRecyclerView;

    private MovieModel.MovieResult mMovieResult;
    private ReviewsModel mReviewsModel;
    private TrailerModel mTrailerModel;
    private MovieServiceInterface mMovieServiceInterface;
    private List<TrailerModel.TrailerResult> mTrailerResultList;

    private TrailerRecyclerAdapter mTrailerRecyclerAdapter;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
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
        mMovieServiceInterface = NetworkApiGenerator.createService(MovieServiceInterface.class);
        fetchTrailers();
        fetchReviews();

    }

    private void fetchReviews() {
        mMovieServiceInterface.getReviews(String.valueOf(mMovieResult.getId()), new Callback<ReviewsModel>() {
            @Override
            public void success(ReviewsModel reviewsModel, Response response) {
                if (reviewsModel != null && reviewsModel.getReviewsResults() != null && reviewsModel.getReviewsResults().size() > 0) {
                    mReviewsModel = reviewsModel;
                    //TODO
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_reviews, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.unable_to_fetch_results, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void fetchTrailers() {
        mMovieServiceInterface.getTrailers(String.valueOf(mMovieResult.getId()), new Callback<TrailerModel>() {
            @Override
            public void success(TrailerModel trailerModel, Response response) {
                if (trailerModel != null && trailerModel.getTrailerResults() != null && trailerModel.getTrailerResults().size() > 0) {
                    mTrailerModel = trailerModel;
                    setupTrailerAdapter(trailerModel);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_trailers, Snackbar.LENGTH_SHORT).show();
                    mNoTrailersTextView.setVisibility(View.VISIBLE);
                    mMovieTrailerRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.unable_to_fetch_results, Snackbar.LENGTH_SHORT).show();
                mNoTrailersTextView.setVisibility(View.VISIBLE);
                mMovieTrailerRecyclerView.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
    }

    private void setupTrailerAdapter(TrailerModel trailerModel) {

        mNoTrailersTextView.setVisibility(View.GONE);
        mMovieTrailerRecyclerView.setVisibility(View.VISIBLE);
        if( mTrailerRecyclerAdapter == null ) {
            mTrailerResultList = trailerModel.getTrailerResults();
            mTrailerRecyclerAdapter = new TrailerRecyclerAdapter(getActivity(), mTrailerResultList, new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int itemPosition) {
                    //Play in youtube / browser
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.HORIZONTAL, false );
            mMovieTrailerRecyclerView.setLayoutManager(linearLayoutManager);
            mMovieTrailerRecyclerView.setAdapter( mTrailerRecyclerAdapter );
        }
        else {
            mTrailerResultList.addAll(trailerModel.getTrailerResults());
            mTrailerRecyclerAdapter.notifyDataSetChanged();
        }

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
