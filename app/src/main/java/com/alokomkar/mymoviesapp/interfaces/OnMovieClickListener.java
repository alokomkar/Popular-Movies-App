package com.alokomkar.mymoviesapp.interfaces;

import com.alokomkar.mymoviesapp.models.MovieModel;

/**
 * Created by cognitive on 2/8/16.
 */
public interface OnMovieClickListener {
    void onMovieClick( MovieModel.MovieResult movieResult, boolean isFavorite );
    void loadDefaultMovie( MovieModel.MovieResult movieResult, boolean isFavorite );
    void storeFragmentParams( String filterString, MovieModel movieModel, int scrollPosition );
}
