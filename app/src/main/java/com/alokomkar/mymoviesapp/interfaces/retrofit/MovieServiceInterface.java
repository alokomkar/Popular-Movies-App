package com.alokomkar.mymoviesapp.interfaces.retrofit;

import com.alokomkar.mymoviesapp.models.MovieModel;
import com.alokomkar.mymoviesapp.models.ReviewsModel;
import com.alokomkar.mymoviesapp.models.TrailerModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by cognitive on 2/8/16.
 */
public interface MovieServiceInterface {

    @GET("/discover/movie")
    void getMoviesList( @Query("sort_by") String filterString, Callback<MovieModel> callback );

    @GET("/movie/{id}/videos")
    void getTrailers( @Path("id") String movieId, Callback<TrailerModel> callback );

    @GET("/movie/{id}/reviews")
    void getReviews( @Path("id") String movieId, Callback<ReviewsModel> callback );

}
