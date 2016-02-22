package com.alokomkar.mymoviesapp.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.fragments.MovieDetailsFragment;
import com.alokomkar.mymoviesapp.fragments.MovieGridViewFragment;
import com.alokomkar.mymoviesapp.interfaces.OnFavoriteClickListener;
import com.alokomkar.mymoviesapp.interfaces.OnMovieClickListener;
import com.alokomkar.mymoviesapp.models.MovieModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener, OnFavoriteClickListener {

    private static final String TWO_PANE_MODE = "two_pane_mode";
    private static final String MOVIE_TITLE = "movie_title";
    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;
    private String MOVIE_DETAILS_TAG = MovieDetailsFragment.class.getSimpleName();
    private boolean mTwoPaneMode = false;
    private String mFilterString;
    private MovieModel mMovieModel;
    private MovieModel mFavoriteMovieModel;
    private int mScrollPosition = -1;
    private int mFavroiteScrollPosition = -1;
    public static final String IS_FAVORITE = "isFavorite";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( findViewById(R.id.moviesDetailFrameLayout) != null ) {
            mTwoPaneMode = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            if( savedInstanceState == null ) {
                loadMoviesGridFragment();
            }
            else {
                mTwoPaneMode = savedInstanceState.getBoolean(TWO_PANE_MODE, false);
                if( mTwoPaneMode ) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    setTitle(savedInstanceState.getString(MOVIE_TITLE, getResources().getString(R.string.app_name)));
                    mFilterString = savedInstanceState.getString(MovieGridViewFragment.FILTER_SELECTED, null);
                    mMovieModel = savedInstanceState.getParcelable(MovieGridViewFragment.MOVIES_LIST);
                    mFavoriteMovieModel = savedInstanceState.getParcelable(MovieGridViewFragment.FAVORITE_MOVIES_LIST);
                    mScrollPosition = savedInstanceState.getInt(MovieGridViewFragment.SCROLL_POSITION, -1);
                }
            }
        }

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TWO_PANE_MODE, mTwoPaneMode);
        outState.putString(MOVIE_TITLE, getTitle().toString());
        outState.putString(MovieGridViewFragment.FILTER_SELECTED, mFilterString);
        outState.putParcelable(MovieGridViewFragment.MOVIES_LIST, mMovieModel);
        outState.putParcelable(MovieGridViewFragment.FAVORITE_MOVIES_LIST, mFavoriteMovieModel);
        outState.putInt(MovieGridViewFragment.SCROLL_POSITION, mScrollPosition);
    }

    private void loadMoviesGridFragment() {

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = getSupportFragmentManager().findFragmentByTag(MovieGridViewFragment.class.getSimpleName());
        if( mFragment == null ) {
            mFragment = new MovieGridViewFragment();
        }
        Bundle bundle = new Bundle();
        if( mMovieModel != null ) {
            bundle.putParcelable( MovieGridViewFragment.MOVIES_LIST, mMovieModel );
            mFragment.setArguments(bundle);
        }
        if( mFavoriteMovieModel != null && mFavoriteMovieModel.getMovieResults() != null && mFavoriteMovieModel.getMovieResults().size() > 0) {
            bundle.putParcelable( MovieGridViewFragment.FAVORITE_MOVIES_LIST, mFavoriteMovieModel );
            mFragment.setArguments(bundle);
        }
        if( mFilterString != null ) {
            bundle.putString(MovieGridViewFragment.FILTER_SELECTED, mFilterString);
            mFragment.setArguments(bundle);
        }
        if( mScrollPosition != -1 ) {
            bundle.putInt(MovieGridViewFragment.SCROLL_POSITION, mScrollPosition);
            mFragment.setArguments(bundle);
        }
        if( mFilterString != null && mFilterString.equals(MovieGridViewFragment.FILTER_FAVORITE) ) {
            if( mFavroiteScrollPosition != -1 ) {
                bundle.putInt(MovieGridViewFragment.SCROLL_POSITION, mFavroiteScrollPosition);
                mFragment.setArguments(bundle);
            }
        }
        mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment, MovieGridViewFragment.class.getSimpleName());
        mFragmentTransaction.commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(R.string.app_name);

    }

    @Override
    public void onMovieClick(MovieModel.MovieResult movieResult, boolean isFavorite) {

        Bundle bundle = new Bundle();
        bundle.putParcelable( MovieModel.class.getSimpleName(), movieResult );
        bundle.putBoolean(IS_FAVORITE, isFavorite);
        mFragment = new MovieDetailsFragment();
        mFragment.setArguments( bundle );
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(  mTwoPaneMode ) {
            mFragmentTransaction.replace(R.id.moviesDetailFrameLayout, mFragment, MOVIE_DETAILS_TAG);
        }
        else {
            mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment, MOVIE_DETAILS_TAG);
        }
        mFragmentTransaction.commit();
        if( !mTwoPaneMode ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(movieResult.getTitle());
        }

    }

    @Override
    public void loadDefaultMovie(MovieModel.MovieResult movieResult, boolean isFavorite) {

        if( mTwoPaneMode ) {
            Bundle bundle = new Bundle();
            bundle.putParcelable( MovieModel.class.getSimpleName(), movieResult );
            bundle.putBoolean(IS_FAVORITE, isFavorite);
            mFragment = new MovieDetailsFragment();
            mFragment.setArguments( bundle );
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.moviesDetailFrameLayout, mFragment, MOVIE_DETAILS_TAG);
            mFragmentTransaction.commit();
        }

    }

    @Override
    public void storeFragmentParams(String filterString, int scrollPosition) {
        this.mFilterString = filterString;
        this.mScrollPosition = scrollPosition;
        if( mFilterString != null && mFilterString.equals(MovieGridViewFragment.FILTER_FAVORITE) ) {
            mFavroiteScrollPosition = scrollPosition;
        }
        else {
            mFavroiteScrollPosition = -1;
        }
    }

    @Override
    public void storeMovies(MovieModel movieModel) {
        this.mMovieModel = movieModel;
    }

    @Override
    public void storeFavorites(MovieModel favoriteMovieModel) {
        this.mFavoriteMovieModel = favoriteMovieModel;
    }


    @Override
    public void onBackPressed() {

        if( mTwoPaneMode ) {
            super.onBackPressed();
        }
        else {

            if( !getTitle().toString().equals(getResources().getString(R.string.app_name)) ) {
                loadMoviesGridFragment();
            }
            else {
                super.onBackPressed();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFavoriteClick(MovieModel.MovieResult movieResult, boolean isFavorite) {

        if( mFavoriteMovieModel == null ) {
            mFavoriteMovieModel = new MovieModel();
        }

        if( mFavoriteMovieModel.getMovieResults() == null ) {
            mFavoriteMovieModel.setMovieResults( new ArrayList<MovieModel.MovieResult>() );
        }

        if( isFavorite ) mFavoriteMovieModel.getMovieResults().add( movieResult );
        else mFavoriteMovieModel.getMovieResults().remove(movieResult);

        if(  mTwoPaneMode ) {
            mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_movie_grid);
        }
        else {
            mFragment = getSupportFragmentManager().findFragmentByTag(MovieGridViewFragment.class.getSimpleName());
        }

        if( mFragment != null ) {
            ((MovieGridViewFragment)mFragment).onFavoriteClick( movieResult, isFavorite );
        }

    }
}
