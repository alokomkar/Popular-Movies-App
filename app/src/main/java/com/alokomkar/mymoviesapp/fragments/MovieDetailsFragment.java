package com.alokomkar.mymoviesapp.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    @Bind(R.id.favoriteTrailerLayout)
    FrameLayout mFavoriteTrailerLayout;
    @Bind(R.id.favoriteMovieButton)
    FloatingActionButton mFavoriteMovieButton;
    @Bind(R.id.noReviewsTextView)
    TextView mNoReviewsTextView;
    @Bind(R.id.reviewsLayout)
    LinearLayout mReviewsLayout;

    private MovieModel.MovieResult mMovieResult;
    private ReviewsModel mReviewsModel;
    private TrailerModel mTrailerModel;
    private MovieServiceInterface mMovieServiceInterface;
    private List<TrailerModel.TrailerResult> mTrailerResultList;

    private TrailerRecyclerAdapter mTrailerRecyclerAdapter;
    private AnimationSet mFavoriteAnimationSet;
    private Animation mFadeInAnimation;
    private Animation mFadeOutAnimation;

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

        mFadeInAnimation = new AlphaAnimation(0, 1);
        mFadeInAnimation.setInterpolator(new DecelerateInterpolator());
        mFadeInAnimation.setDuration(1000);

        mFadeOutAnimation = new AlphaAnimation(1, 0);
        mFadeOutAnimation.setInterpolator(new AccelerateInterpolator());
        mFadeOutAnimation.setStartOffset(1000);
        mFadeOutAnimation.setDuration(1000);

        mFavoriteAnimationSet = new AnimationSet(false);
        mFavoriteAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFavoriteTrailerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFavoriteTrailerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFavoriteAnimationSet.addAnimation(mFadeInAnimation);
        mFavoriteAnimationSet.addAnimation(mFadeOutAnimation);

        mFavoriteMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoriteTrailerLayout.startAnimation(mFavoriteAnimationSet);
            }
        });


    }

    private void fetchReviews() {
        mMovieServiceInterface.getReviews(String.valueOf(mMovieResult.getId()), new Callback<ReviewsModel>() {
            @Override
            public void success(ReviewsModel reviewsModel, Response response) {
                if (reviewsModel != null && reviewsModel.getReviewsResults() != null && reviewsModel.getReviewsResults().size() > 0) {
                    mReviewsModel = reviewsModel;
                    mNoReviewsTextView.setVisibility(View.GONE);
                    setupReviews();
                } else {
                    mNoReviewsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.unable_to_fetch_results, Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
                mNoReviewsTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupReviews() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0 ,0, 0, 16);
        for( ReviewsModel.ReviewsResult reviewsResult : mReviewsModel.getReviewsResults()) {
            View reviewDetailsLayout = layoutInflater.inflate(R.layout.movie_review_item, null);
            ReviewViewHolder reviewViewHolder = new ReviewViewHolder( reviewDetailsLayout );
            reviewViewHolder.authorTextView.setText( reviewsResult.getAuthor() );
            reviewViewHolder.reviewUrlTextView.setText( reviewsResult.getUrl() );
            reviewViewHolder.reviewTextView.setText( reviewsResult.getContent() );
            mReviewsLayout.addView( reviewDetailsLayout, layoutParams );
        }

    }

    private void fetchTrailers() {
        mMovieServiceInterface.getTrailers(String.valueOf(mMovieResult.getId()), new Callback<TrailerModel>() {
            @Override
            public void success(TrailerModel trailerModel, Response response) {
                if (trailerModel != null && trailerModel.getTrailerResults() != null && trailerModel.getTrailerResults().size() > 0) {
                    mTrailerModel = trailerModel;
                    setupTrailerAdapter(trailerModel);
                } else {
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
        if (mTrailerRecyclerAdapter == null) {
            mTrailerResultList = trailerModel.getTrailerResults();
            mTrailerRecyclerAdapter = new TrailerRecyclerAdapter(getActivity(), mTrailerResultList, new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int itemPosition) {
                    //Play in youtube / browser
                    watchYoutubeVideo(mTrailerResultList.get(itemPosition).getKey());
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mMovieTrailerRecyclerView.setLayoutManager(linearLayoutManager);
            mMovieTrailerRecyclerView.setAdapter(mTrailerRecyclerAdapter);
        } else {
            mTrailerResultList.addAll(trailerModel.getTrailerResults());
            mTrailerRecyclerAdapter.notifyDataSetChanged();
        }

    }

    // http://stackoverflow.com/a/12439378/2648035
    public void watchYoutubeVideo(String id) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
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

    static class ReviewViewHolder {

        @Bind(R.id.authorTextView)
        TextView authorTextView;
        @Bind(R.id.reviewUrlTextView)
        TextView reviewUrlTextView;
        @Bind(R.id.reviewTextView)
        TextView reviewTextView;

        ReviewViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
