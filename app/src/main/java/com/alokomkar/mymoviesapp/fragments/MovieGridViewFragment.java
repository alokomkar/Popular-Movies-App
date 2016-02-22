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
    public static final String FILTER_FAVORITE = "favorite";


    private static final String TAG = MovieGridViewFragment.class.getSimpleName();
    private MovieModel mMovieModel;
    private MovieModel mFavoriteMovieModel;
    private int mScrollPosition;
    private List<MovieModel.MovieResult> mFavoriteMoviesList;

    private boolean isOtherTaskFinished = false;
    private AsyncTask<Void, Void, List<MovieModel.MovieResult>> mFavoriteMoviesFetchTask;

    public MovieGridViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        if( savedInstanceState != null ) {
            mFilterString = savedInstanceState.getString(FILTER_SELECTED);
            mMovieModel = savedInstanceState.getParcelable(MOVIES_LIST);
            mFavoriteMovieModel = savedInstanceState.getParcelable(FAVORITE_MOVIES_LIST);
            mScrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
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

        mFavoriteMovieModel = new MovieModel();
        Bundle bundle = getArguments();
        if( bundle == null ) {
            if( savedInstanceState == null ) {
                //View created for the first time
                getFavoriteMovies();
                getMovies(null);
            }
            else {
                parseBundle(savedInstanceState);
            }
        }
        else {
            mScrollPosition = bundle.getInt(SCROLL_POSITION, -1);
            parseBundle(bundle);
        }

        return view;
    }

    private void parseBundle(Bundle bundle) {
        mFilterString = bundle.getString(FILTER_SELECTED, null);
        mMovieModel = bundle.getParcelable(MOVIES_LIST);
        mFavoriteMovieModel = bundle.getParcelable(FAVORITE_MOVIES_LIST);

        if( mFavoriteMovieModel != null && mFavoriteMovieModel.getMovieResults() != null )
            mFavoriteMoviesList = mFavoriteMovieModel.getMovieResults();

        if( mFilterString != null && mFilterString.equals(FILTER_FAVORITE) ) {
            if( mFavoriteMoviesList != null && mFavoriteMoviesList.size() > 0 ) {
                mMovieModel = new MovieModel();
                mMovieModel.setMovieResults(mFavoriteMoviesList);
                setupAdapter(mMovieModel.getMovieResults());
            }
            else {
                mFavoriteMoviesList = null;
                getFavoriteMovies();
            }
        }
        else {
            if( mMovieModel != null && mMovieModel.getMovieResults() != null && mMovieModel.getMovieResults().size() > 0 ) {
                setupAdapter( mMovieModel.getMovieResults() );
            }
            else {
                getMoviesList(mFilterString);
            }
        }
    }

    private void getMovies( String filterString ) {
        mMovieServiceInterface.getMoviesList(filterString, new Callback<MovieModel>() {
            @Override
            public void success(MovieModel movieModel, Response response) {
                if (movieModel != null) {
                    mMovieModel = movieModel;
                    if( mMovieResultList == null ) {
                        mMovieResultList = new ArrayList<MovieModel.MovieResult>();
                    }
                    mMovieResultList.clear();
                    if( movieModel.getMovieResults() != null ) {
                        mMovieResultList.addAll(movieModel.getMovieResults());
                    }
                    mOnMovieClickListener.storeMovies( mMovieModel );
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

            mFavoriteMoviesFetchTask = new AsyncTask<Void, Void, List<MovieModel.MovieResult>>() {

                private List<MovieModel.MovieResult> movieList;
                @Override
                protected List<MovieModel.MovieResult> doInBackground(Void... params) {
                    // Fetch from local DB
                    movieList = new RushSearch().find(MovieModel.MovieResult.class);
                    return movieList;
                }

                @Override
                protected void onPostExecute(List<MovieModel.MovieResult> movieResults) {
                    super.onPostExecute(movieResults);
                    if( movieResults != null ) {
                        mFavoriteMoviesList = movieResults;
                    }
                    else {
                        mFavoriteMoviesList = new ArrayList<>();
                    }
                    mFavoriteMovieModel = new MovieModel();
                    mFavoriteMovieModel.setMovieResults( mFavoriteMoviesList );
                    if( mFavoriteMoviesList.size() == 0 && mFilterString.equals(FILTER_FAVORITE) ) {
                        mProgressLayout.setVisibility(View.GONE);
                    }
                    mOnMovieClickListener.storeFavorites( mFavoriteMovieModel );
                    dismissProgressView();
                    if( mFilterString.equals(FILTER_FAVORITE) ) {
                        mMovieModel = new MovieModel();
                        mMovieModel.setMovieResults( mFavoriteMoviesList );
                        mOnMovieClickListener.storeMovies( mMovieModel );
                        setupAdapter(mMovieModel.getMovieResults());
                    }
                }
            };

            mFavoriteMoviesFetchTask.execute();

        }
        else {
            if( mFavoriteMoviesList.size() == 0 ) {
                mProgressLayout.setVisibility(View.GONE);
                mOnMovieClickListener.storeFragmentParams(mFilterString, -1);
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_favorites, Snackbar.LENGTH_SHORT).show();
            }
            else {
                mMovieModel = new MovieModel();
                mMovieModel.setMovieResults(mFavoriteMoviesList);
                mOnMovieClickListener.storeMovies( mMovieModel );
                mOnMovieClickListener.storeFavorites( mMovieModel );
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
            if( mMovieGridRecyclerAdapter != null ) {
                mMovieGridRecyclerAdapter.notifyDataSetChanged();
            }
            mMovieGridRecyclerAdapter = null;
        }
        mProgressLayout.setVisibility(View.VISIBLE);

        if( filterString == null || !filterString.equals(FILTER_FAVORITE) ) {
            getMovies(filterString);
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
                    if( mFavoriteMoviesList != null ) {
                        mOnMovieClickListener.onMovieClick(movieResult, mFavoriteMoviesList.contains(movieResult));
                    }
                    else {
                        mOnMovieClickListener.onMovieClick(movieResult, (mFilterString != null && mFilterString.equals(FILTER_FAVORITE)));
                    }

                    storeParameters( mFilterString, itemPosition );
                    mScrollPosition = itemPosition;
                }
            });
            storeParameters( mFilterString, -1 );
            mMovieGridRecyclerView.setAdapter(mMovieGridRecyclerAdapter);
            if( mMovieGridRecyclerAdapter.getItemCount() > 0 ) {
                if( mFavoriteMoviesList != null ) {
                    mOnMovieClickListener.loadDefaultMovie( mMovieGridRecyclerAdapter.getItem(0), mFavoriteMoviesList.contains(mMovieGridRecyclerAdapter.getItem(0)) );
                }
                else {
                    mOnMovieClickListener.loadDefaultMovie( mMovieGridRecyclerAdapter.getItem(0), false );
                }
            }


        } else {
            mMovieResultList.addAll(movieResults);
            mMovieGridRecyclerAdapter.notifyDataSetChanged();
            storeParameters(mFilterString, -1);

        }
        if( mScrollPosition != -1 ) {
            mMovieGridRecyclerView.getLayoutManager().scrollToPosition(mScrollPosition);
            mScrollPosition = -1;
        }
        mProgressLayout.setVisibility(View.GONE);

    }

    private void storeParameters(String filterString, int i) {
        mOnMovieClickListener.storeFragmentParams(filterString, i);
        if( filterString != null && filterString.equals(FILTER_FAVORITE) ) {
            if( mFavoriteMovieModel == null ) {
                mFavoriteMovieModel = new MovieModel();
            }
            mFavoriteMovieModel.setMovieResults(mMovieResultList);
            if( mFavoriteMoviesList == null ) {
                mFavoriteMoviesList = new ArrayList<>();
            }
            mFavoriteMoviesList.clear();
            mFavoriteMoviesList.addAll( mMovieResultList );
            mOnMovieClickListener.storeFavorites( mFavoriteMovieModel );
        }
        else {
            if( mMovieModel == null ) {
                mMovieModel = new MovieModel();
            }
            mMovieModel.setMovieResults(mMovieResultList);
            mOnMovieClickListener.storeMovies( mMovieModel );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mFavoriteMoviesFetchTask != null && mFavoriteMoviesFetchTask.getStatus() == AsyncTask.Status.RUNNING)
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
        if( mFavoriteMovieModel == null ) {
            mFavoriteMovieModel = new MovieModel();
        }
        mFavoriteMovieModel.setMovieResults( mFavoriteMoviesList );
        outState.putParcelable(FAVORITE_MOVIES_LIST, mFavoriteMovieModel);
    }

    public void onFavoriteClick(MovieModel.MovieResult movieResult, boolean isFavorite) {

        if( isFavorite ) {
            if( !mFavoriteMoviesList.contains(movieResult) )
                mFavoriteMoviesList.add( movieResult );
        }
        else mFavoriteMoviesList.remove( movieResult );

        if( mFavoriteMovieModel == null ) {
            mFavoriteMovieModel = new MovieModel();
        }
        mFavoriteMovieModel.setMovieResults(mFavoriteMoviesList);

        if( mFilterString.equals(FILTER_FAVORITE) ) {
            if( isFavorite ) {
                mMovieModel.setMovieResults(mFavoriteMoviesList);
                mMovieResultList.add(movieResult);
                if( mMovieGridRecyclerAdapter != null ) {
                    mMovieGridRecyclerAdapter.notifyDataSetChanged();
                }
                else {
                    setupAdapter(mMovieModel.getMovieResults());
                }
            }
            else {
                mMovieModel.setMovieResults(mFavoriteMoviesList);
                mMovieResultList.remove(movieResult);
                if( mMovieGridRecyclerAdapter != null ) {
                    mMovieGridRecyclerAdapter.notifyDataSetChanged();
                    if( mMovieGridRecyclerAdapter.getItemCount() > 0 )
                        mOnMovieClickListener.loadDefaultMovie( mMovieGridRecyclerAdapter.getItem(0), true );
                }
                else {
                    setupAdapter(mMovieModel.getMovieResults());
                }
            }
        }
    }
}
