package com.hazemelsawy.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MoviesDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";

    public MoviesDB(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POP_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MostPopularEntry.TABLE_NAME + " (" +
                MoviesContract.MostPopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MostPopularEntry.COLUMN_MOVIE_ID + " LONG NOT NULL, " +
                MoviesContract.MostPopularEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MostPopularEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MoviesContract.MostPopularEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MostPopularEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesContract.MostPopularEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL " +
                " );";


        final String SQL_CREATE_RATED_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.TopRatedEntry.TABLE_NAME + " (" +
                MoviesContract.TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + " LONG NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL " +

                " );";
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.FavouritesEntry.TABLE_NAME + " (" +
                MoviesContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + " LONG NOT NULL, " +
                MoviesContract.FavouritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.FavouritesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MoviesContract.FavouritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavouritesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesContract.FavouritesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL " +

                " );";
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailersEntry.TABLE_NAME + " (" +
                MoviesContract.TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " LONG NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_LINK + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewsEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_POP_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RATED_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MostPopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavouritesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
