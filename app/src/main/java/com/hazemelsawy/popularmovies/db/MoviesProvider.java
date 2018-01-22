package com.hazemelsawy.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class MoviesProvider extends ContentProvider {
    private MoviesDB moviesDBHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int POPULAR = 10;
    static final int POPULAR_WITH_ID =11;
    static final int RATED = 20;
    static final int RATED_WITH_ID = 21;
    static final int FAVOURITE = 30;
    static final int FAVOURITE_WITH_ID = 31;
    static final int TRAILERS = 40;
    static final int TRAILERS_WITH_MOVIE_ID = 41;
    static final int REVIEWS = 50;
    static final int REVIEWS_WITH_MOVIE_ID = 51;

    private static final String POPULAR_SELECTION =
            MoviesContract.MostPopularEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String RATED_SELECTION =
            MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String FAVOURITE_SELECTION =
            MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String TRAILERS_SELECTION =
            MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " = ? ";
    private static final String REVIEWS_SELECTION =
            MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR, POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR + "/#", POPULAR_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_MOST_POPULAR + "/*", POPULAR_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED, RATED);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED + "/#", RATED_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED + "/*", RATED_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVOURITE, FAVOURITE);
        matcher.addURI(authority, MoviesContract.PATH_FAVOURITE + "/#", FAVOURITE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVOURITE + "/*", FAVOURITE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", TRAILERS_WITH_MOVIE_ID);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", REVIEWS_WITH_MOVIE_ID);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        moviesDBHelper = new MoviesDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case POPULAR: {
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POPULAR_WITH_ID:
                retCursor = getPopularMoviesById(uri, projection, sortOrder);
                break;

            case RATED: {
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RATED_WITH_ID:
                retCursor = getRatedMoviesById(uri, projection, sortOrder);
                break;
            case FAVOURITE:
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.FavouritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder

                );
                break;
            case FAVOURITE_WITH_ID:
                retCursor = getFavouriteMoviesById(uri, projection, sortOrder);
                break;
            case REVIEWS_WITH_MOVIE_ID:
                retCursor = getReviewsWithMovieId(uri, projection, sortOrder);
                break;
            case TRAILERS_WITH_MOVIE_ID:
                retCursor = getTrailerWithMovieId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getPopularMoviesById(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return moviesDBHelper.getReadableDatabase().query(
                MoviesContract.MostPopularEntry.TABLE_NAME,
                projection,
                POPULAR_SELECTION,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getRatedMoviesById(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return moviesDBHelper.getReadableDatabase().query(
                MoviesContract.TopRatedEntry.TABLE_NAME,
                projection,
                RATED_SELECTION,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavouriteMoviesById(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return moviesDBHelper.getReadableDatabase().query(
                MoviesContract.FavouritesEntry.TABLE_NAME,
                projection,
                FAVOURITE_SELECTION,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return moviesDBHelper.getReadableDatabase().query(
                MoviesContract.TrailersEntry.TABLE_NAME,
                projection,
                TRAILERS_SELECTION,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewsWithMovieId(Uri uri, String[] projection, String sortOrder) {
        String id = uri.getPathSegments().get(1);

        return moviesDBHelper.getReadableDatabase().query(
                MoviesContract.ReviewsEntry.TABLE_NAME,
                projection,
                REVIEWS_SELECTION,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POPULAR:
                return MoviesContract.MostPopularEntry.CONTENT_TYPE;
            case POPULAR_WITH_ID:
                return MoviesContract.MostPopularEntry.CONTENT_ITEM_TYPE;
            case RATED:
                return MoviesContract.TopRatedEntry.CONTENT_TYPE;
            case RATED_WITH_ID:
                return MoviesContract.TopRatedEntry.CONTENT_ITEM_TYPE;
            case FAVOURITE:
                return MoviesContract.FavouritesEntry.CONTENT_TYPE;
            case FAVOURITE_WITH_ID:
                return MoviesContract.FavouritesEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MoviesContract.TrailersEntry.CONTENT_TYPE;
            case TRAILERS_WITH_MOVIE_ID:
                return MoviesContract.TrailersEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_WITH_MOVIE_ID:
                return MoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POPULAR: {
                long _id = db.insert(MoviesContract.MostPopularEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MostPopularEntry.buildPopularUri(_id);

                else
                    throw new android.database.SQLException("Can't insert row into " + uri);
                break;
            }
            case RATED: {
                long _id = db.insert(MoviesContract.TopRatedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.TopRatedEntry.buildRatedUri(_id);

                else
                    throw new android.database.SQLException("Can't insert row into " + uri);
                break;
            }
            case FAVOURITE_WITH_ID: {
                long _id = db.insert(MoviesContract.FavouritesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.FavouritesEntry.buildFavouriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = "1";//to return the number of deleted rows
        switch (match) {
            case POPULAR:
                rowsDeleted = db.delete(
                        MoviesContract.MostPopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RATED:
                rowsDeleted = db.delete(
                        MoviesContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MoviesContract.FavouritesEntry.TABLE_NAME,
                        FAVOURITE_SELECTION, new String[]{id});
                break;
            case TRAILERS_WITH_MOVIE_ID:
                String id1 = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MoviesContract.TrailersEntry.TABLE_NAME,
                        TRAILERS_SELECTION, new String[]{id1});
                break;
            case REVIEWS_WITH_MOVIE_ID:
                String id2 = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        REVIEWS_SELECTION, new String[]{id2});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POPULAR:
                rowsUpdated = db.update(MoviesContract.MostPopularEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case RATED:
                rowsUpdated = db.update(MoviesContract.TopRatedEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POPULAR:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MoviesContract.MostPopularEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (returnCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            case RATED:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MoviesContract.TopRatedEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (returnCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            case TRAILERS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.TrailersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (returnCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (returnCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnCount;


            default:
                return super.bulkInsert(uri, values);
        }
    }

    public void shutdown() {
        moviesDBHelper.close();
        super.shutdown();
    }
}
