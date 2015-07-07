package com.shareqube.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jude Ben on 6/19/2015.
 */
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.shareqube.moviesapp.data" ;

    public  static  final Uri  BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY) ;


    // path

    public static final String ALL_MOVIES_PATH = "allmovies" ;
    public static final String FAVORITE_MOVIES_PATH = "favoritemovies" ;

    public static final class AllMoviesTable implements BaseColumns{

        public static final  Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(ALL_MOVIES_PATH).build() ;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + ALL_MOVIES_PATH ;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY  + "/" + ALL_MOVIES_PATH ;

        public static final String TABLE_NAME = "allMovies";


        // table column name
        public static final String COLUMN_MOVIE_ID = "movie_id" ;
        public static final String COLUMN_MOVIE_TITLE = " movie_title" ;
        public static  final String COLUMN_MOVIE_POSTER = "poster" ;
        public static final String COLUMN_MOVIE_RELEASE_DATE= "release_date" ;
        public static final String COLUMN_MOVIE_OVERVIEW =  "overview" ;
        public static final String COLUMN_USER_RATING = "user_rating" ;
        public static final String COLUMN_REVIEWS = "reviews" ;
        public static final String COLUMN_TRAILER_URL = "trailer_url" ;

        public static final Uri getAllMoviesUri(long id){

            return ContentUris.withAppendedId(CONTENT_URI , id) ;
        }

        public static Integer getIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    public static final class FavoriteMoviesTable implements  BaseColumns{


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIES_PATH).build() ;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVORITE_MOVIES_PATH ;

        public  static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVORITE_MOVIES_PATH ;

        public static final String TABLE_NAME = "favoriteMovies" ;


        // fovorite movies column name
        public static final String COLUMN_FMOVIE_ID = "movie_id" ;
        public static final String COLUMN_FMOVIE_TITLE = " movie_title" ;
        public static  final String COLUMN_FMOVIE_POSTER = "poster" ;
        public static final String COLUMN_FMOVIE_RELEASE_DATE= "release_date" ;
        public static final String COLUMN_FMOVIE_OVERVIEW =  "overview" ;
        public static final String COLUMN_FUSER_RATING = "user_rating" ;
        public static final String COLUMN_FREVIEWS = "reviews" ;
        public static final String COLUMN_FTRAILER_URL = "trailer_url" ;



   public static final Uri getFavoriteMoviesUri(long id){

       return ContentUris.withAppendedId(CONTENT_URI, id);
   }

        public static Integer getIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
