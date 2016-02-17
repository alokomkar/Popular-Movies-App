package com.alokomkar.mymoviesapp.interfaces;

import com.alokomkar.mymoviesapp.models.MovieModel;

/**
 * Created by cognitive on 2/16/16.
 */
public interface OnFavoriteClickListener {
    void onFavoriteClick( MovieModel.MovieResult movieResult, boolean isFavorite );
}
