package com.alokomkar.mymoviesapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.fragments.MovieDetailsFragment;
import com.alokomkar.mymoviesapp.fragments.MovieGridViewFragment;
import com.alokomkar.mymoviesapp.interfaces.OnMovieClickListener;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener {

    private Fragment mFragment;
    private FragmentTransaction mFragmentTransaction;
    private String MOVIE_DETAILS_TAG = MovieDetailsFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMoviesGridFragment();
    }

    private void loadMoviesGridFragment() {
        mFragment = new MovieGridViewFragment();
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onMovieClick(MovieModel.MovieResult movieResult) {

        Bundle bundle = new Bundle();
        bundle.putParcelable( MovieModel.class.getSimpleName(), movieResult );
        mFragment = new MovieDetailsFragment();
        mFragment.setArguments( bundle );
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.moviesFrameLayout, mFragment, MOVIE_DETAILS_TAG);
        mFragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        if( mFragment instanceof  MovieDetailsFragment ) {
            loadMoviesGridFragment();
            return;
        }
        else {
            super.onBackPressed();
        }
    }
}
