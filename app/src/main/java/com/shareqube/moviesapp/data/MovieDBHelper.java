package com.shareqube.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import  com.shareqube.moviesapp.data.MovieContract.AllMoviesTable ;
import  com.shareqube.moviesapp.data.MovieContract.FavoriteMoviesTable ;

/**
 * Created by Jude Ben on 6/19/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db" ;


    public  MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        final String SQL_CREATE_ALL_MOVIES = " CREATE TABLE "  + AllMoviesTable.TABLE_NAME + " ( " +
                AllMoviesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                AllMoviesTable.COLUMN_MOVIE_ID + " TEXT NOT NULL , " +
                AllMoviesTable.COLUMN_MOVIE_TITLE  + " TEXT NOT NULL , "+
                AllMoviesTable.COLUMN_MOVIE_POSTER + " TEXT NOT NULL ,"  +
                AllMoviesTable.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL , " +
                AllMoviesTable.COLUMN_MOVIE_OVERVIEW  + " TEXT NOT NULL ," +
                AllMoviesTable.COLUMN_USER_RATING + " REAL NOT NULL ," +
                AllMoviesTable.COLUMN_REVIEWS + " TEXT ," +
                AllMoviesTable.COLUMN_TRAILER_URL  +  " TEXT " +
                " ) ;" ;

        final String SQL_CREATE_FAVORITE_MOVIE = " CREATE TABLE " + FavoriteMoviesTable.TABLE_NAME + " ( " +
                FavoriteMoviesTable._ID + " INTEGER PRIMARY KEY  AUTOINCREMENT , " +
                 FavoriteMoviesTable.COLUMN_FMOVIE_ID + " TEXT NOT NULL , " +
                FavoriteMoviesTable.COLUMN_FMOVIE_TITLE  + " TEXT NOT NULL , "+
                FavoriteMoviesTable.COLUMN_FMOVIE_POSTER + " TEXT NOT NULL ,"  +
                FavoriteMoviesTable.COLUMN_FMOVIE_RELEASE_DATE + " TEXT NOT NULL , " +
                FavoriteMoviesTable.COLUMN_FMOVIE_OVERVIEW  + " TEXT NOT NULL ," +
                FavoriteMoviesTable.COLUMN_FUSER_RATING + " REAL NOT NULL ," +
                FavoriteMoviesTable.COLUMN_FREVIEWS + " TEXT ," +
                FavoriteMoviesTable.COLUMN_FTRAILER_URL  +  " TEXT " +
                " ) ;" ;


        db.execSQL(SQL_CREATE_ALL_MOVIES);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + AllMoviesTable.TABLE_NAME);

        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesTable.TABLE_NAME);

        onCreate(db);
    }


}
