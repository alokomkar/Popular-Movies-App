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
        }

    }

    private void loadMoviesGridFragment() {

        mFragment = new MovieGridViewFragment();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment).addToBackStack(null);
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
            if(  mTwoPaneMode ) {
                mFragmentTransaction.replace(R.id.moviesDetailFrameLayout, mFragment, MOVIE_DETAILS_TAG);
            }
            else {
                mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment, MOVIE_DETAILS_TAG);
            }
            mFragmentTransaction.commit();
        }

    }


    @Override
    public void onBackPressed() {

        if( mTwoPaneMode ) {
            super.onBackPressed();
        }
        else {
            if( mFragment instanceof MovieDetailsFragment ) {
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
