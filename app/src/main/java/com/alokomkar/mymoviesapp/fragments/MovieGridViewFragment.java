package com.alokomkar.mymoviesapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alokomkar.mymoviesapp.R;
import com.alokomkar.mymoviesapp.adapter.MovieGridRecyclerAdapter;
import com.alokomkar.mymoviesapp.apimodels.MovieModel;
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.alokomkar.mymoviesapp.interfaces.OnMovieClickListener;
import com.alokomkar.mymoviesapp.interfaces.retrofit.MovieServiceInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MovieGridViewFragment extends Fragment {

    private static String TAG = MovieGridViewFragment.class.getSimpleName();

    @Bind(R.id.movieGridRecyclerView)
    RecyclerView mMovieGridRecyclerView;
    @Bind(R.id.progressLayout)
    RelativeLayout progressLayout;

    private GridLayoutManager mGridLayoutManager;
    private MovieServiceInterface mMovieServiceInterface;
    private MovieGridRecyclerAdapter mMovieGridRecyclerAdapter;
    private List<MovieModel.MovieResult> mMovieResultList;
    private OnMovieClickListener mOnMovieClickListener;

    public MovieGridViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_grid_view, container, false);
        ButterKnife.bind(this, view);

        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mMovieGridRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieServiceInterface = NetworkApiGenerator.createService(MovieServiceInterface.class);
        getMoviesList();

        return view;
    }

    private void getMoviesList() {
        progressLayout.setVisibility(View.VISIBLE);
        mMovieServiceInterface.getMoviesList(new Callback<MovieModel>() {
            @Override
            public void success(MovieModel movieModel, Response response) {
                if (movieModel != null) {
                    if ( movieModel.getMovieResults() != null && movieModel.getMovieResults().size() > 0 ) {
                        Log.d(TAG, "Movies size : " + movieModel.getMovieResults().size());
                        setupAdapter(movieModel.getMovieResults());
                    }
                    else {
                        //TODO Error message
                    }
                }
                else {
                    progressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error : " + error.getMessage());
                progressLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setupAdapter(List<MovieModel.MovieResult> movieResults) {

        if (mMovieGridRecyclerAdapter == null) {
            mMovieResultList = new ArrayList<>();
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter = new MovieGridRecyclerAdapter(getActivity(), mMovieResultList, new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int itemPosition) {
                    MovieModel.MovieResult movieResult = mMovieGridRecyclerAdapter.getItem(itemPosition);
                    Log.d(TAG, "Movie Result : OnItemClick : " + movieResult.getOriginalTitle());
                    mOnMovieClickListener.onMovieClick(movieResult);

                }
            });
            mMovieGridRecyclerView.setAdapter(mMovieGridRecyclerAdapter);
        } else {
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter.notifyDataSetChanged();
        }
        progressLayout.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnMovieClickListener = (OnMovieClickListener) context;
        if (context instanceof OnMovieClickListener) {
            mOnMovieClickListener = (OnMovieClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMovieClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnMovieClickListener = null;
    }
}
