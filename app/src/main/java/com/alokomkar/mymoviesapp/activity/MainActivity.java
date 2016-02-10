package com.alokomkar.mymoviesapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.fragments.MovieDetailsFragment;
import com.alokomkar.mymoviesapp.fragments.MovieGridViewFragment;
import com.alokomkar.mymoviesapp.interfaces.OnMovieClickListener;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener {

    private static final String TWO_PANE_MODE = "two_pane_mode";
    private static final String MOVIE_TITLE = "movie_title";
    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;
    private String MOVIE_DETAILS_TAG = MovieDetailsFragment.class.getSimpleName();
    private boolean mTwoPaneMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( findViewById(R.id.moviesDetailFrameLayout) != null ) {
            mTwoPaneMode = true;
        }
        else {
            if( savedInstanceState == null ) {
                loadMoviesGridFragment();
            }
            else {
                mTwoPaneMode = savedInstanceState.getBoolean(TWO_PANE_MODE, false);
                if( mTwoPaneMode ) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    setTitle(savedInstanceState.getString(MOVIE_TITLE, getResources().getString(R.string.app_name)));
                }
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TWO_PANE_MODE, mTwoPaneMode);
        outState.putString(MOVIE_TITLE, getTitle().toString());
    }

    private void loadMoviesGridFragment() {


        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = getSupportFragmentManager().findFragmentByTag(MovieGridViewFragment.class.getSimpleName());
        if( mFragment == null ) {
            mFragment = new MovieGridViewFragment();
        }
        mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment, MovieGridViewFragment.class.getSimpleName());
        mFragmentTransaction.commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(R.string.app_name);

    }

    @Override
    public void onMovieClick(MovieModel.MovieResult movieResult) {

        Bundle bundle = new Bundle();
        bundle.putParcelable( MovieModel.class.getSimpleName(), movieResult );
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
    public void loadDefaultMovie(MovieModel.MovieResult movieResult) {

        if( mTwoPaneMode ) {
            Bundle bundle = new Bundle();
            bundle.putParcelable( MovieModel.class.getSimpleName(), movieResult );
            mFragment = new MovieDetailsFragment();
            mFragment.setArguments( bundle );
            mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction.replace(R.id.moviesDetailFrameLayout, mFragment, MOVIE_DETAILS_TAG);
            mFragmentTransaction.commit();
        }

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


}
