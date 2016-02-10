package com.alokomkar.mymoviesapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Bind(R.id.movieGridRecyclerView)
    RecyclerView mMovieGridRecyclerView;
    @Bind(R.id.progressLayout)
    RelativeLayout mProgressLayout;

    private GridLayoutManager mGridLayoutManager;
    private MovieServiceInterface mMovieServiceInterface;
    private MovieGridRecyclerAdapter mMovieGridRecyclerAdapter;
    private List<MovieModel.MovieResult> mMovieResultList;
    private OnMovieClickListener mOnMovieClickListener;
    private String mFilterString = "";

    private static final String FILTER_SELECTED = "filter_selected";

    private static final String FILTER_MOST_POPULAR = "popularity.desc";
    private static final String FILTER_HIGHEST_RATED = "vote_average.desc";
    private static final String MOVIES_LIST = "movies_list";

    private static final String TAG = MovieGridViewFragment.class.getSimpleName();
    private MovieModel mMovieModel;

    public MovieGridViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            mFilterString = savedInstanceState.getString(FILTER_SELECTED);
            //mMovieModel = savedInstanceState.getParcelable(MOVIES_LIST);
        }
        //setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_grid_view, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.column_count));
        mMovieGridRecyclerView.setLayoutManager(mGridLayoutManager);
        mMovieGridRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMovieGridRecyclerView.setHasFixedSize(true);
        mMovieServiceInterface = NetworkApiGenerator.createService(MovieServiceInterface.class);

        if( savedInstanceState == null ) {
            getMoviesList(null);
        }
        else {
            mFilterString = savedInstanceState.getString(FILTER_SELECTED, null);
            mMovieModel = savedInstanceState.getParcelable( MOVIES_LIST );
            if( mMovieModel != null && mMovieModel.getMovieResults() != null && mMovieModel.getMovieResults().size() > 0 ) {
                setupAdapter( mMovieModel.getMovieResults() );
            }
            else {
                getMoviesList(mFilterString);
            }

        }


        return view;
    }

    private void getMoviesList( String filterString ) {
        mFilterString = filterString;
        if( filterString != null ) {
            if( mMovieResultList != null ) {
                mMovieResultList.clear();
            }
            mMovieGridRecyclerAdapter = null;
        }

        mProgressLayout.setVisibility(View.VISIBLE);
        mMovieServiceInterface.getMoviesList(filterString, new Callback<MovieModel>() {
            @Override
            public void success(MovieModel movieModel, Response response) {
                if (movieModel != null) {
                    mMovieModel = movieModel;
                    if (movieModel.getMovieResults() != null && movieModel.getMovieResults().size() > 0) {
                        setupAdapter(movieModel.getMovieResults());
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.custom), R.string.no_results, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    mProgressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error : " + error.getMessage());
                mProgressLayout.setVisibility(View.GONE);
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
                    mOnMovieClickListener.onMovieClick(movieResult);
                }
            });
            mMovieGridRecyclerView.setAdapter(mMovieGridRecyclerAdapter);
            mOnMovieClickListener.loadDefaultMovie( mMovieGridRecyclerAdapter.getItem(0) );
        } else {
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter.notifyDataSetChanged();
        }
        mProgressLayout.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {

            case R.id.action_highest_rated :
                getMoviesList(FILTER_HIGHEST_RATED);
                return true;

            case R.id.action_most_popular :
                getMoviesList(FILTER_MOST_POPULAR);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //Reference : http://code.hootsuite.com/orientation-changes-on-android/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILTER_SELECTED, mFilterString);
        outState.putParcelable(MOVIES_LIST, mMovieModel);
    }
}
