package com.hazemelsawy.popularmovies.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hazemelsawy.popularmovies.BuildConfig;
import com.hazemelsawy.popularmovies.db.MoviesContract;
import com.hazemelsawy.popularmovies.PrefUtil;
import com.hazemelsawy.popularmovies.R;
import com.hazemelsawy.popularmovies.adapters.GridAdapter;
import com.hazemelsawy.popularmovies.webservice.MovieModel;
import com.hazemelsawy.popularmovies.webservice.NetworkUtil;

import java.util.ArrayList;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String CATEGORY_KEY = "category";
    private static final String POPULAR_VALUE = "popular";
    private static final String TOP_RATED_VALUE = "top_rated";
    private static final String FAVOURITE_VALUE = "favourite";
    private static final int ID_KEY = 1;
    private static final int LOADER_NUMBER = 0;
    private RecyclerView moviesGrid;
    private GridAdapter gridAdapter;
    private NetworkUtil networkUtil;
    private Call<MovieModel> moviesCall = null;
    private ArrayList<MovieModel.Result> moviesResponse = new ArrayList<>();
    private String category;
    private static boolean init = false;
    private Cursor mCursor = null;
    private Uri movieUri;
    private MovieClickListener movieClickListener;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        moviesGrid = (RecyclerView) rootView.findViewById(R.id.rv_posters);
        networkUtil = NetworkUtil.getInstance();
        moviesGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        moviesGrid.setHasFixedSize(true);
        gridAdapter = new GridAdapter(getContext());
        moviesGrid.setAdapter(gridAdapter);
        PrefUtil.with(getContext());
        gridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (mCursor != null && mCursor.moveToFirst()) {
                    mCursor.moveToPosition(position);
                    if (category.equals(POPULAR_VALUE))
                        movieClickListener.onMovieSelected(MoviesContract.MostPopularEntry.buildPopularUriWithId(
                                mCursor.getLong(ID_KEY)));
                    if (category.equals(TOP_RATED_VALUE))
                        movieClickListener.onMovieSelected(MoviesContract.TopRatedEntry.buildRatedUriWithId(
                                mCursor.getLong(ID_KEY)));
                    if (category.equals(FAVOURITE_VALUE))
                        movieClickListener.onMovieSelected(MoviesContract.FavouritesEntry.buildFavouritesUriWithId(
                                mCursor.getLong(ID_KEY)));
                }
            }
        });
        return rootView;
    }

    synchronized public void setInit() {
        if (init) return;
        init = true;
        category = POPULAR_VALUE;
        PrefUtil.saveStringToPref(category, CATEGORY_KEY);
        Thread invalidateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                movieUri = MoviesContract.MostPopularEntry.CONTENT_URI;
                Cursor cursor = getContext().getContentResolver().query(
                        movieUri,
                        null,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0)
                    fetchMoviesByCategory();
                cursor.close();
            }
        });
        invalidateThread.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_NUMBER, null, this);
        setInit();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_NUMBER, null, this);
    }

    public void setMovieCall(MovieClickListener movieClickListener) {
        this.movieClickListener = movieClickListener;
    }

    public void getMovies(String category) {
        moviesCall = networkUtil.getServices().getMovies(category, API_KEY);
        moviesCall.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                moviesResponse = response.body().getResults();
                moviesGrid.scrollToPosition(0);
                insertValuesToDB();
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular) {
            PrefUtil.saveStringToPref(POPULAR_VALUE, CATEGORY_KEY);
            fetchMoviesByCategory();
            return true;
        }
        if (item.getItemId() == R.id.rated) {
            PrefUtil.saveStringToPref(TOP_RATED_VALUE, CATEGORY_KEY);
            fetchMoviesByCategory();
            return true;
        }
        if (item.getItemId() == R.id.favourite) {
            PrefUtil.saveStringToPref(FAVOURITE_VALUE, CATEGORY_KEY);
            fetchMoviesByCategory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchMoviesByCategory() {
        category = PrefUtil.getStringFromPref(CATEGORY_KEY);
        Cursor cursor;
        switch (category) {
            case POPULAR_VALUE:
                movieUri = MoviesContract.MostPopularEntry.CONTENT_URI;
                cursor = getContext().getContentResolver().query(
                        movieUri,
                        null,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0)
                    getMovies(POPULAR_VALUE);
                else
                    getLoaderManager().restartLoader(LOADER_NUMBER, null, this);
                break;
            case TOP_RATED_VALUE:
                movieUri = MoviesContract.TopRatedEntry.CONTENT_URI;
                cursor = getContext().getContentResolver().query(
                        movieUri,
                        null,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0)
                    getMovies(TOP_RATED_VALUE);
                else
                    getLoaderManager().restartLoader(LOADER_NUMBER, null, this);
                break;
            case FAVOURITE_VALUE:
                getLoaderManager().restartLoader(LOADER_NUMBER, null, this);
                break;
        }
        moviesResponse = new ArrayList<>();

    }

    private void insertValuesToDB() {
        Vector<ContentValues> valuesVector = new Vector<>();
        for (int i = 0; i < moviesResponse.size(); i++) {
            ContentValues moviesValues = new ContentValues();
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_MOVIE_ID, moviesResponse.get(i).getId());
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_TITLE, moviesResponse.get(i).getTitle());
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_POSTER_URL, moviesResponse.get(i).getPosterPath());
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_RELEASE_DATE, moviesResponse.get(i).getReleaseDate());
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_RATING, moviesResponse.get(i).getVoteAverage());
            moviesValues.put(MoviesContract.MostPopularEntry.COLUMN_DESCRIPTION, moviesResponse.get(i).getOverview());
            valuesVector.add(moviesValues);
        }
        Uri moviesUri = null;
        if (PrefUtil.getStringFromPref(CATEGORY_KEY).equals(POPULAR_VALUE)) {
            moviesUri = MoviesContract.MostPopularEntry.CONTENT_URI;
            getActivity().getContentResolver().delete(moviesUri, null, null);
            if (valuesVector.size() > 0) {
                ContentValues[] contentArray = new ContentValues[valuesVector.size()];
                valuesVector.toArray(contentArray);
                getActivity().getContentResolver().bulkInsert(moviesUri, contentArray);
            }
        } else if (PrefUtil.getStringFromPref(CATEGORY_KEY).equals(TOP_RATED_VALUE)) {
            moviesUri = MoviesContract.TopRatedEntry.CONTENT_URI;
            getActivity().getContentResolver().delete(moviesUri, null, null);
            if (valuesVector.size() > 0) {
                ContentValues[] contentArray = new ContentValues[valuesVector.size()];
                valuesVector.toArray(contentArray);
                getActivity().getContentResolver().bulkInsert(moviesUri, contentArray);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        category = PrefUtil.getStringFromPref(CATEGORY_KEY);
        switch (id) {
            case LOADER_NUMBER:
                if (category.equals(POPULAR_VALUE))
                    movieUri = MoviesContract.MostPopularEntry.CONTENT_URI;
                if (category.equals(TOP_RATED_VALUE))
                    movieUri = MoviesContract.TopRatedEntry.CONTENT_URI;
                if (category.equals(FAVOURITE_VALUE))
                    movieUri = MoviesContract.FavouritesEntry.CONTENT_URI;
                return new CursorLoader(getContext(),
                        movieUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mCursor = data;
            gridAdapter.swapCursor(data);
        } else {
            gridAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        gridAdapter.swapCursor(null);
    }

    public interface MovieClickListener {
        void onMovieSelected(Uri uri);
    }
}
