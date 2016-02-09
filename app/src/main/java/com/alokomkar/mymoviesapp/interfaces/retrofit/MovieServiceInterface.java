package com.alokomkar.mymoviesapp.interfaces.retrofit;

import com.alokomkar.mymoviesapp.apimodels.MovieModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by cognitive on 2/8/16.
 */
public interface MovieServiceInterface {

    @GET("/discover/movie")
    void getMoviesList( @Query("sort_by") String filterString, Callback<MovieModel> callback );

}
