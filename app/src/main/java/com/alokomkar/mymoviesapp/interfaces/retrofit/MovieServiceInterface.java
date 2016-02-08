package com.alokomkar.mymoviesapp.interfaces.retrofit;

import com.alokomkar.mymoviesapp.apimodels.MovieModel;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by cognitive on 2/8/16.
 */
public interface MovieServiceInterface {

    @GET("/discover/movie")
    void getMoviesList( Callback<MovieModel> callback );

}
