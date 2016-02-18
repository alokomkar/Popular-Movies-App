package com.alokomkar.mymoviesapp.fragments;

import android.content.Context;
import android.os.AsyncTask;
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
import com.alokomkar.mymoviesapp.generator.NetworkApiGenerator;
import com.alokomkar.mymoviesapp.interfaces.OnItemClickListener;
import com.alokomkar.mymoviesapp.interfaces.OnMovieClickListener;
import com.alokomkar.mymoviesapp.interfaces.retrofit.MovieServiceInterface;
import com.alokomkar.mymoviesapp.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MovieGridViewFragment extends Fragment {

    @Bind(R.id.movieGridRecyclerView)
    RecyclerView mMovieGridRecyclerView;
    @Bind(R.id.progressLayout)
    RelativeLayout mProgressLayout;

    private MovieServiceInterface mMovieServiceInterface;
    private MovieGridRecyclerAdapter mMovieGridRecyclerAdapter;
    private List<MovieModel.MovieResult> mMovieResultList;
    private OnMovieClickListener mOnMovieClickListener;
    private String mFilterString = "";

    public static final String FILTER_SELECTED = "filter_selected";
    public static final String MOVIES_LIST = "movies_list";
    public static final String FAVORITE_MOVIES_LIST = "favorite_movies_list";
    public static final String SCROLL_POSITION = "scroll_position";
    private static final String FILTER_MOST_POPULAR = "popularity.desc";
    private static final String FILTER_HIGHEST_RATED = "vote_average.desc";
    private static final String FILTER_FAVORITE = "favorite";


    private static final String TAG = MovieGridViewFragment.class.getSimpleName();
    private MovieModel mMovieModel;
    private MovieModel mFavoriteMovieModel;
    private int mScrollPosition;
    private List<MovieModel.MovieResult> mFavoriteMoviesList;

    private boolean isOtherTaskFinished = false;
    private AsyncTask<Void, Void, Void> mFavoriteMoviesFetchTask;

    public MovieGridViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        if( savedInstanceState != null ) {
            mFilterString = savedInstanceState.getString(FILTER_SELECTED);
            //mMovieModel = savedInstanceState.getParcelable(MOVIES_LIST);
            //setupAdapter( mMovieModel.getMovieResults() );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_grid_view, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.column_count));
        mMovieGridRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieGridRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMovieGridRecyclerView.setHasFixedSize(true);
        mMovieServiceInterface = NetworkApiGenerator.createService(MovieServiceInterface.class);

        Bundle bundle = getArguments();
        if( bundle == null ) {
            if( savedInstanceState == null ) {
                //View created for the first time
                getFavoriteMovies();
                getMovies( null );
            }
            else {
                mFilterString = savedInstanceState.getString(FILTER_SELECTED, null);
                mMovieModel = savedInstanceState.getParcelable(MOVIES_LIST);
                mFavoriteMovieModel = savedInstanceState.getParcelable(FAVORITE_MOVIES_LIST);

                if( mFavoriteMovieModel != null && mFavoriteMovieModel.getMovieResults() != null )
                    mFavoriteMoviesList = mFavoriteMovieModel.getMovieResults();

                if( mMovieModel != null && mMovieModel.getMovieResults() != null && mMovieModel.getMovieResults().size() > 0 ) {
                    setupAdapter( mMovieModel.getMovieResults() );
                }
                else {
                    getMoviesList(mFilterString);
                }

            }
        }
        else {
            mFilterString = bundle.getString(FILTER_SELECTED, null);
            mMovieModel = bundle.getParcelable( MOVIES_LIST );
            mScrollPosition = bundle.getInt( SCROLL_POSITION, -1 );
            if( mMovieModel != null && mMovieModel.getMovieResults() != null && mMovieModel.getMovieResults().size() > 0 ) {
                setupAdapter( mMovieModel.getMovieResults() );
            }
            else {
                getMoviesList(mFilterString);
            }
        }



        return view;
    }

    private void getMovies( String filterString ) {
        mMovieServiceInterface.getMoviesList(filterString, new Callback<MovieModel>() {
            @Override
            public void success(MovieModel movieModel, Response response) {
                if (movieModel != null) {
                    mMovieModel = movieModel;
                    if (movieModel.getMovieResults() != null && movieModel.getMovieResults().size() > 0) {
                        setupAdapter(movieModel.getMovieResults());
                    } else {
                        dismissProgressView();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_results, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    dismissProgressView();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error : " + error.getMessage());
                dismissProgressView();
            }
        });
    }

    private void getFavoriteMovies() {
        if( mFavoriteMoviesList == null ) {
            mFavoriteMoviesFetchTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    // Fetch from local DB
                    new RushSearch().find(MovieModel.MovieResult.class, new RushSearchCallback<MovieModel.MovieResult>() {
                        @Override
                        public void complete(List<MovieModel.MovieResult> list) {
                            if (list != null) {
                                mFavoriteMoviesList = list;
                            }
                            else {
                                mFavoriteMoviesList = new ArrayList<>();
                            }
                            onPostExecute( null );
                        }
                    });
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    dismissProgressView();
                }
            };

            mFavoriteMoviesFetchTask.execute();
        }
        else {
            if( mFavoriteMoviesList.size() == 0 ) {
                mProgressLayout.setVisibility(View.GONE);
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_favorites, Snackbar.LENGTH_SHORT).show();
            }
            else {
                mMovieModel = new MovieModel();
                mMovieModel.setMovieResults(mFavoriteMoviesList);
                setupAdapter(mMovieModel.getMovieResults());
            }
        }

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

        if( filterString == null || !filterString.equals(FILTER_FAVORITE) ) {
            getMovies( filterString );
        }
        else {
            getFavoriteMovies();
        }

    }

    private void dismissProgressView() {
        if (isOtherTaskFinished) {
            mProgressLayout.setVisibility(View.GONE);
        } else {
            isOtherTaskFinished = true;
        }
    }

    private void setupAdapter(List<MovieModel.MovieResult> movieResults) {

        if (mMovieGridRecyclerAdapter == null) {
            mMovieResultList = new ArrayList<>();
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter = new MovieGridRecyclerAdapter(getActivity(), mMovieResultList, new OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int itemPosition) {
                    MovieModel.MovieResult movieResult = mMovieGridRecyclerAdapter.getItem(itemPosition);
                    mOnMovieClickListener.onMovieClick(movieResult, mFavoriteMoviesList.contains(movieResult));
                    mOnMovieClickListener.storeFragmentParams(mFilterString, mMovieModel, itemPosition);
                    mScrollPosition = itemPosition;
                }
            });
            mMovieGridRecyclerView.setAdapter(mMovieGridRecyclerAdapter);
            mOnMovieClickListener.loadDefaultMovie( mMovieGridRecyclerAdapter.getItem(0), mFavoriteMoviesList.contains(mMovieGridRecyclerAdapter.getItem(0)) );
        } else {
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter.notifyDataSetChanged();
        }
        if( mScrollPosition != -1 ) {
            mMovieGridRecyclerView.getLayoutManager().scrollToPosition(mScrollPosition);
            mScrollPosition = -1;
        }
        mProgressLayout.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mFavoriteMoviesFetchTask != null && mFavoriteMoviesFetchTask.getStatus() == AsyncTask.Status.RUNNING )
            mFavoriteMoviesFetchTask.cancel(true);
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

            case R.id.action_favorite :
                getMoviesList(FILTER_FAVORITE);
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
        mFavoriteMovieModel = new MovieModel();
        mFavoriteMovieModel.setMovieResults( mFavoriteMoviesList );
        outState.putParcelable(FAVORITE_MOVIES_LIST, mFavoriteMovieModel);
    }

    public void onFavoriteClick(MovieModel.MovieResult movieResult, boolean isFavorite) {

        if( isFavorite ) mFavoriteMoviesList.add( movieResult );
        else mFavoriteMoviesList.remove( movieResult );

        if( mFavoriteMovieModel == null ) {
            mFavoriteMovieModel = new MovieModel();
        }
        mFavoriteMovieModel.setMovieResults(mFavoriteMoviesList);

        if( mFilterString.equals(FILTER_FAVORITE) ) {
            if( isFavorite ) {
                mMovieModel.setMovieResults( mFavoriteMoviesList );
                mMovieResultList.add(movieResult);
                mMovieGridRecyclerAdapter.notifyDataSetChanged();
            }
            else {
                mMovieModel.setMovieResults( mFavoriteMoviesList );
                mMovieResultList.remove(movieResult);
                mMovieGridRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }
}
