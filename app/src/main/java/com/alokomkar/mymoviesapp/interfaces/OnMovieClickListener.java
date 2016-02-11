package com.alokomkar.mymoviesapp.interfaces;

import com.alokomkar.mymoviesapp.models.MovieModel;

/**
 * Created by cognitive on 2/8/16.
 */
public interface OnMovieClickListener {
    void onMovieClick( MovieModel.MovieResult movieResult );
    void loadDefaultMovie( MovieModel.MovieResult movieResult );
    void storeFragmentParams( String filterString, MovieModel movieModel, int scrollPosition );
}
