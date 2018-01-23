package com.hazemelsawy.popularmovies.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hazemelsawy.popularmovies.BuildConfig;
import com.hazemelsawy.popularmovies.db.MoviesContract;
import com.hazemelsawy.popularmovies.R;
import com.hazemelsawy.popularmovies.activities.MainActivity;
import com.hazemelsawy.popularmovies.adapters.ReviewsAdapter;
import com.hazemelsawy.popularmovies.adapters.TrailersAdapter;
import com.hazemelsawy.popularmovies.webservice.NetworkUtil;
import com.hazemelsawy.popularmovies.webservice.ReviewModel;
import com.hazemelsawy.popularmovies.webservice.TrailerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private static final String API_KEY = BuildConfig.API_KEY;
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieRating;
    private TextView releaseDate;
    private TextView movieOverview;
    private CheckBox favourite;
    private RecyclerView trailersRecycler;
    private RecyclerView reviewsRecycler;
    private Uri mUri;
    private String movieId;
    private Uri favouriteMovieUri;
    private ContentValues contentValues;
    private NetworkUtil networkUtil;
    private Call<ReviewModel> reviewModelCall = null;
    private Call<TrailerModel> trailerModelCall = null;
    private ArrayList<TrailerModel.Youtube> youtubeArrayList = new ArrayList<>();
    private ArrayList<ReviewModel.Result> resultArrayList = new ArrayList<>();
    private Cursor trailersCursor = null;
    private Cursor reviewsCursor = null;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;
    private Uri trailerUriWithId;
    private Uri reviewUriWithId;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        movieTitle = (TextView) rootView.findViewById(R.id.tv_movie_title);
        movieRating = (TextView) rootView.findViewById(R.id.tv_user_rating);
        releaseDate = (TextView) rootView.findViewById(R.id.tv_release_date);
        movieOverview = (TextView) rootView.findViewById(R.id.tv_overview);
        moviePoster = (ImageView) rootView.findViewById(R.id.iv_thumb);
        favourite = (CheckBox) rootView.findViewById(R.id.cb_favourite);
        trailersRecycler = (RecyclerView) rootView.findViewById(R.id.rv_trailers);
        reviewsRecycler = (RecyclerView) rootView.findViewById(R.id.rv_reviews);
        networkUtil = NetworkUtil.getInstance();
        contentValues = new ContentValues();
        setRecyclers();
        return rootView;
    }

    public void getMovie(Uri uri) {
        mUri = uri;
        if (getArguments() != null)
            mUri = getArguments().getParcelable(MainActivity.DETAIL_URI);
        if (mUri != null) {
            movieId = mUri.getPathSegments().get(1);
            if (movieId != null) {
                Cursor movieData = getContext().getContentResolver().query(
                        mUri,
                        null,
                        null,
                        null,
                        null);
                if (!movieData.moveToFirst())
                    return;
                String title = movieData.getString(2);
                String poster = movieData.getString(3);
                String date = movieData.getString(4);
                int rating = movieData.getInt(5);
                String overview = movieData.getString(6);
                movieTitle.setText(title);
                releaseDate.setText(date);
                movieOverview.setText(overview);
                movieRating.setText(rating + " /10");
                Picasso.with(getContext()).
                        load("http://image.tmdb.org/t/p/w185//" + poster).resize(500,
                        500).into(moviePoster);
                DatabaseUtils.cursorRowToContentValues(movieData, contentValues);
                if (movieId != null) {
                    favouriteMovieUri = MoviesContract.FavouritesEntry.buildFavouritesUriWithId(Long.valueOf(movieId));
                }
                Cursor favoriteChecked = getActivity().getContentResolver().query(
                        MoviesContract.FavouritesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                if (favoriteChecked != null && favoriteChecked.getCount() > 0) {
                    favourite.setChecked(favourite.isChecked());
                }
                favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = ((CheckBox) v).isChecked();
                        // Check which checkbox was clicked
                        switch (v.getId()) {
                            case R.id.cb_favourite:
                                if (checked) {
                                    if (favouriteMovieUri != null) {
                                        getActivity().getContentResolver().delete(
                                                favouriteMovieUri,
                                                null,
                                                null);
                                        getActivity().getContentResolver().insert(favouriteMovieUri, contentValues);
                                    }
                                } else {
                                    if (favouriteMovieUri != null) {
                                        getActivity().getContentResolver().delete(
                                                favouriteMovieUri,
                                                null,
                                                null);
                                    }
                                }
                                break;
                        }
                    }
                });
                trailerUriWithId = MoviesContract.TrailersEntry
                        .buildTrailersUriWithId(Long.valueOf(movieId));
                reviewUriWithId = MoviesContract.ReviewsEntry
                        .buildReviewsUriWithId(Long.valueOf(movieId));
                fetchMovieTrailersAndReviews();
            }
        }
    }

    private void setRecyclers() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        trailersRecycler.setLayoutManager(layoutManager);
        reviewsRecycler.setLayoutManager(layoutManager1);
        trailersRecycler.setHasFixedSize(true);
        reviewsRecycler.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter();
        reviewsAdapter = new ReviewsAdapter();
        trailersRecycler.setAdapter(trailersAdapter);
        reviewsRecycler.setAdapter(reviewsAdapter);
        trailersAdapter.setOnItemClickListener(new TrailersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                trailersCursor.move(position);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + trailersCursor.getString(3)));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailersCursor.getString(3)));
                    startActivity(intent);
                }
            }
        });
    }

    private void getTrailers() {
        trailerModelCall = networkUtil.getServices().getTrailers(movieId, API_KEY);
        trailerModelCall.enqueue(new Callback<TrailerModel>() {
            @Override
            public void onResponse(Call<TrailerModel> call, Response<TrailerModel> response) {
                youtubeArrayList = response.body().getYoutube();
                Log.d(TAG, "onResponse: " + youtubeArrayList.size());
                if (youtubeArrayList.size() > 0)
                    insertTrailersToDB();
            }

            @Override
            public void onFailure(Call<TrailerModel> call, Throwable t) {
            }
        });
    }

    private void getReviews() {
        reviewModelCall = networkUtil.getServices().getReviews(movieId, API_KEY);
        reviewModelCall.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                resultArrayList = response.body().getResults();
                Log.d(TAG, "onResponse: " + resultArrayList.size());
                if (resultArrayList.size() > 0)
                    insertReviewsToDB();

            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
            }
        });
    }

    private void insertTrailersToDB() {
        Vector<ContentValues> valuesVector = new Vector<>();
        ContentValues trailerContent = new ContentValues();
        for (int i = 0; i < youtubeArrayList.size(); i++) {
            trailerContent.put(MoviesContract.TrailersEntry.COLUMN_MOVIE_ID, Long.valueOf(movieId));
            trailerContent.put(MoviesContract.TrailersEntry.COLUMN_NAME, youtubeArrayList.get(i).getName());
            trailerContent.put(MoviesContract.TrailersEntry.COLUMN_LINK, youtubeArrayList.get(i).getSource());
            valuesVector.add(trailerContent);
        }
        Uri trailerUri = MoviesContract.TrailersEntry.CONTENT_URI;
        Uri trailerWithIdUri = MoviesContract.TrailersEntry.buildTrailersUriWithId(Long.valueOf(movieId));
        getContext().getContentResolver().delete(trailerWithIdUri, null, null);
        if (valuesVector.size() > 0) {
            ContentValues[] contentArray = new ContentValues[valuesVector.size()];
            Log.d(TAG, "insertTrailersToDB: " + valuesVector.size());
            valuesVector.toArray(contentArray);
            getContext().getContentResolver().bulkInsert(trailerUri, contentArray);
        }
        trailersCursor = getContext().getContentResolver().query(
                trailerUriWithId,
                null,
                null,
                null,
                null);
        trailersAdapter.swapCursor(trailersCursor);
    }

    private void insertReviewsToDB() {
        Vector<ContentValues> valuesVector = new Vector<>();
        ContentValues reviewsContent = new ContentValues();
        for (int i = 0; i < resultArrayList.size(); i++) {
            reviewsContent.put(MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID, Long.valueOf(movieId));
            reviewsContent.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR, resultArrayList.get(i).getAuthor());
            reviewsContent.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT, resultArrayList.get(i).getContent());
            valuesVector.add(reviewsContent);
        }
        Uri reviewUri = MoviesContract.ReviewsEntry.CONTENT_URI;
        Uri reviewWithIdUri = MoviesContract.ReviewsEntry.buildReviewsUriWithId(Long.valueOf(movieId));
        getContext().getContentResolver().delete(reviewWithIdUri, null, null);
        if (valuesVector.size() > 0) {
            Log.d(TAG, "insertReviewsToDB: " + valuesVector.size());
            ContentValues[] contentArray = new ContentValues[valuesVector.size()];
            valuesVector.toArray(contentArray);
            getContext().getContentResolver().bulkInsert(reviewUri, contentArray);
        }
        reviewsCursor = getContext().getContentResolver().query(
                reviewUriWithId,
                null,
                null,
                null,
                null);
        reviewsAdapter.swapCursor(reviewsCursor);

    }

    synchronized private void fetchMovieTrailersAndReviews() {
        Cursor trailersCursor = getContext().getContentResolver().query(
                trailerUriWithId,
                null,
                null,
                null,
                null);
        if (null == trailersCursor || trailersCursor.getCount() == 0) {
            getTrailers();
        }
        trailersCursor.close();
        Cursor reviewsCursor = getContext().getContentResolver().query(
                reviewUriWithId,
                null,
                null,
                null,
                null);
        if (reviewsCursor == null || reviewsCursor.getCount() == 0)
            getReviews();
        reviewsCursor.close();
    }

}
