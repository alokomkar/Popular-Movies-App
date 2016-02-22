package com.alokomkar.mymoviesapp.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.alokomkar.mymoviesapp.activity.MainActivity;
import com.alokomkar.mymoviesapp.adapter.TrailerRecyclerAdapter;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnFavoriteClickListener;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.alokomkar.mymoviesapp.interfaces.retrofit.MovieServiceInterface;
import com.alokomkar.mymoviesapp.models.MovieModel;
import com.alokomkar.mymoviesapp.models.ReviewsModel;
import com.alokomkar.mymoviesapp.models.TrailerModel;
import com.alokomkar.mymoviesapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushCallback;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailsFragment extends Fragment {

    private static final String MOVIE_RESULT = "movie_result";
    private static final String MOVIE_REVIEWS = "movie_reviews";
    private static final String MOVIE_TRAILERS = "movie_trailers";

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

    private boolean isFavorite = false;
    private OnFavoriteClickListener mOnFavoriteClickListener;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMovieResult = savedInstanceState.getParcelable(MOVIE_RESULT);
            mTrailerModel = savedInstanceState.getParcelable(MOVIE_TRAILERS);
            mReviewsModel = savedInstanceState.getParcelable(MOVIE_REVIEWS);
            isFavorite = savedInstanceState.getBoolean(MainActivity.IS_FAVORITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mMovieResult = bundle.getParcelable(MovieModel.class.getSimpleName());
                isFavorite = bundle.getBoolean(MainActivity.IS_FAVORITE, false);
                setValues(mMovieResult);
            }
        } else {

            mMovieResult = savedInstanceState.getParcelable(MOVIE_RESULT);
            mTrailerModel = savedInstanceState.getParcelable(MOVIE_TRAILERS);
            mReviewsModel = savedInstanceState.getParcelable(MOVIE_REVIEWS);
            isFavorite = savedInstanceState.getBoolean(MainActivity.IS_FAVORITE);
            setValues(mMovieResult);

            if( mReviewsModel != null && mReviewsModel.getReviewsResults() != null ) {
                mReviewsLayout.removeAllViews();
                setupReviews();
            }


            if( mTrailerModel != null && mTrailerModel.getTrailerResults() != null ) {
                mTrailerRecyclerAdapter = null;
                if( mTrailerResultList == null ) {
                    mTrailerResultList = new ArrayList<>();
                }
                mTrailerResultList.clear();
                setupTrailerAdapter(mTrailerModel);
            }


        }


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.action_share ) {
            shareTrailer();
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTrailer() {
        if( mTrailerResultList != null && mTrailerResultList.size() > 0 ) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Movie Night :\n"
                            + mMovieResult.getOriginalTitle()
                            +" Trailer : \n"
                            + "http://www.youtube.com/watch?v=" + mTrailerResultList.get(0).getKey());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_trailers_to_share, Snackbar.LENGTH_SHORT).show();
        }

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

        Animation mFadeInAnimation = new AlphaAnimation(0, 1);
        mFadeInAnimation.setInterpolator(new DecelerateInterpolator());
        mFadeInAnimation.setDuration(1000);

        Animation mFadeOutAnimation = new AlphaAnimation(1, 0);
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

                if (isFavorite) {

                    mFavoriteMovieButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_normal));
                    mMovieResult.delete(new RushCallback() {
                        @Override
                        public void complete() {

                        }
                    });
                    isFavorite = false;
                    mOnFavoriteClickListener.onFavoriteClick(mMovieResult, isFavorite);
                } else {
                    mFavoriteTrailerLayout.startAnimation(mFavoriteAnimationSet);
                    mFavoriteMovieButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_selected));
                    mMovieResult.save(new RushCallback() {
                        @Override
                        public void complete() {

                        }
                    });
                    isFavorite = true;
                    mOnFavoriteClickListener.onFavoriteClick(mMovieResult, isFavorite);
                }


            }
        });

        mFavoriteMovieButton.setImageDrawable( isFavorite ?
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_selected) :
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_normal));



    }

    private void fetchReviews() {
        if( !NetworkUtils.isConnected(getActivity()) ) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
            mMovieTrailerRecyclerView.setVisibility(View.GONE);
            mNoTrailersTextView.setVisibility(View.VISIBLE);
            mNoReviewsTextView.setVisibility(View.VISIBLE);
            return;
        }
        else {
            mMovieTrailerRecyclerView.setVisibility(View.VISIBLE);
            mNoReviewsTextView.setVisibility(View.GONE);
            mNoTrailersTextView.setVisibility(View.GONE);
        }
        mMovieServiceInterface.getReviews(String.valueOf(mMovieResult.getMovieId()), new Callback<ReviewsModel>() {
            @Override
            public void success(ReviewsModel reviewsModel, Response response) {
                if( mNoReviewsTextView != null ) { //To handle fast navigation between movies
                    if (reviewsModel != null && reviewsModel.getReviewsResults() != null && reviewsModel.getReviewsResults().size() > 0) {
                        mReviewsModel = reviewsModel;

                        mNoReviewsTextView.setVisibility(View.GONE);
                        setupReviews();

                    } else {
                        mNoReviewsTextView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if( mNoReviewsTextView != null ) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.unable_to_fetch_results, Snackbar.LENGTH_SHORT).show();
                    error.printStackTrace();
                    mNoReviewsTextView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void setupReviews() {


        if( mReviewsLayout != null ) {
            mReviewsLayout.removeAllViews();
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 16);
            for( ReviewsModel.ReviewsResult reviewsResult : mReviewsModel.getReviewsResults()) {
                View reviewDetailsLayout = layoutInflater.inflate(R.layout.movie_review_item, null);
                ReviewViewHolder reviewViewHolder = new ReviewViewHolder( reviewDetailsLayout );
                reviewViewHolder.authorTextView.setText( reviewsResult.getAuthor() );
                reviewViewHolder.reviewUrlTextView.setText( reviewsResult.getUrl() );
                reviewViewHolder.reviewTextView.setText(reviewsResult.getContent());
                mReviewsLayout.addView( reviewDetailsLayout, layoutParams );
            }
        }

    }

    private void fetchTrailers() {

        if( !NetworkUtils.isConnected(getActivity()) ) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }
        else {
            mMovieTrailerRecyclerView.setVisibility(View.VISIBLE);
            mNoReviewsTextView.setVisibility(View.GONE);
            mNoTrailersTextView.setVisibility(View.GONE);
        }
        mMovieServiceInterface.getTrailers(String.valueOf(mMovieResult.getMovieId()), new Callback<TrailerModel>() {
            @Override
            public void success(TrailerModel trailerModel, Response response) {
                if( mNoTrailersTextView != null ) {
                    if (trailerModel != null && trailerModel.getTrailerResults() != null && trailerModel.getTrailerResults().size() > 0) {
                        mTrailerModel = trailerModel;
                        setupTrailerAdapter(trailerModel);
                    } else {
                        mNoTrailersTextView.setVisibility(View.VISIBLE);
                        mMovieTrailerRecyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if( mNoTrailersTextView != null ) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.unable_to_fetch_results, Snackbar.LENGTH_SHORT).show();
                    mNoTrailersTextView.setVisibility(View.VISIBLE);
                    mMovieTrailerRecyclerView.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }
        });
    }

    private void setupTrailerAdapter(TrailerModel trailerModel) {

        if( mNoTrailersTextView != null ) {
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

                if( mTrailerResultList == null ) {
                    mTrailerResultList = new ArrayList<>();
                    mTrailerResultList.addAll(trailerModel.getTrailerResults());
                }

                mTrailerRecyclerAdapter.notifyDataSetChanged();
            }
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
        outState.putParcelable(MOVIE_REVIEWS, mReviewsModel);
        outState.putParcelable(MOVIE_TRAILERS, mTrailerModel);
        outState.putBoolean(MainActivity.IS_FAVORITE, isFavorite);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteClickListener) {
            mOnFavoriteClickListener = (OnFavoriteClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnFavoriteClickListener = null;
    }
}
