package com.shareqube.moviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Jude Ben on 6/19/2015.
 */
public class MovieProvider extends ContentProvider {

    static final int ALLMOVIES = 100 ;
    static final int MOVIE_ROW = 150;
    static final int FAVORITEMOVIES =200 ;
    static final int FAVORITE_ROW = 250 ;
    static UriMatcher uriMatcher = buildUriMatcher();
    MovieDBHelper movieDBHelper ;

    static UriMatcher buildUriMatcher(){
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH) ;

        final String authority = MovieContract.CONTENT_AUTHORITY ;

        matcher.addURI(authority , MovieContract.ALL_MOVIES_PATH,ALLMOVIES );
        matcher.addURI(authority, MovieContract.ALL_MOVIES_PATH + "/*", MOVIE_ROW);
        matcher.addURI(authority , MovieContract.FAVORITE_MOVIES_PATH , FAVORITEMOVIES);
        matcher.addURI(authority, MovieContract.FAVORITE_MOVIES_PATH + "/*" , FAVORITE_ROW);


        return  matcher ;
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext()) ;

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        int match = uriMatcher.match(uri) ;

        Cursor returnCursor ;
        switch (match){
            case ALLMOVIES :

                returnCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.AllMoviesTable.TABLE_NAME ,projection,
                        selection ,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case FAVORITEMOVIES:
                returnCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMoviesTable.TABLE_NAME , projection,
                        selection,selectionArgs,null,null ,sortOrder
                );
                break;


            case MOVIE_ROW:



                String sql = " SELECT * FROM " + MovieContract.AllMoviesTable.TABLE_NAME + " WHERE " + MovieContract.AllMoviesTable._ID
                        + " = ?";


                returnCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.AllMoviesTable.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder
                );

                break;

            case FAVORITE_ROW:
               // String sql = " SELECT * FROM " + MovieContract.AllMoviesTable.TABLE_NAME + " WHERE " + MovieContract.AllMoviesTable._ID
                 //       + " = ?";
                returnCursor = movieDBHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMoviesTable.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI" + uri) ;


        }

        returnCursor.setNotificationUri(getContext().getContentResolver() ,uri);

        return returnCursor;
    }

    @Override
    public String getType(Uri uri) {

        int match =  uriMatcher.match(uri) ;

        switch (match){

            case ALLMOVIES:

                return MovieContract.AllMoviesTable.CONTENT_TYPE ;

            case MOVIE_ROW:
                return MovieContract.AllMoviesTable.CONTENT_ITEM_TYPE;


            case FAVORITEMOVIES:
                return  MovieContract.FavoriteMoviesTable.CONTENT_TYPE ;


            default:
                throw new UnsupportedOperationException(" Unknown URi" + uri) ;
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int match = uriMatcher.match(uri);
        Uri returnUri ;

      SQLiteDatabase db =  movieDBHelper.getWritableDatabase() ;

        switch (match){

            case ALLMOVIES: {
                long id = db.insert(MovieContract.AllMoviesTable.TABLE_NAME, null, values);

                if (id > 0) {

                    returnUri = MovieContract.AllMoviesTable.getAllMoviesUri(id);
                } else {
                    throw new UnsupportedOperationException("Unknow URI" + uri);
                }
            }
                break;

            case  FAVORITEMOVIES: {
                long id = db.insert(MovieContract.FavoriteMoviesTable.TABLE_NAME, null, values);
                if( id > 0){
                    returnUri = MovieContract.FavoriteMoviesTable.getFavoriteMoviesUri(id);
                }else {
                    throw new UnsupportedOperationException("Unknow URI" + uri);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown URI" + uri) ;


        }
        getContext().getContentResolver().notifyChange(uri ,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowDeleted ;

        SQLiteDatabase db = movieDBHelper.getWritableDatabase();

        int match = uriMatcher.match(uri) ;

        if( null == selection){ selection = "1" ; }

        switch (match){
            case ALLMOVIES:
                rowDeleted = db.delete(MovieContract.AllMoviesTable.TABLE_NAME , selection ,selectionArgs) ;
                break;
            case FAVORITEMOVIES:

                rowDeleted = db.delete(MovieContract.FavoriteMoviesTable.TABLE_NAME ,selection,selectionArgs);
                break;

                default:
                    throw  new UnsupportedOperationException(" Unknown Uri" + uri) ;

        }
        if(rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = movieDBHelper.getWritableDatabase() ;
        int rowUpdated ;
        int match = uriMatcher.match(uri);
        switch (match){
            case ALLMOVIES:
                rowUpdated = db.update(MovieContract.AllMoviesTable.TABLE_NAME ,values,selection ,selectionArgs) ;
                break;
            case FAVORITEMOVIES:
                rowUpdated = db.update(MovieContract.FavoriteMoviesTable.TABLE_NAME , values , selection, selectionArgs) ;
                break;
            default:
                throw  new UnsupportedOperationException("Unknown Uri" + uri) ;

        }
        if(rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri ,null);
        }
        return rowUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase() ;
        int match = uriMatcher.match(uri) ;

        switch (match){

            case ALLMOVIES:
                db.beginTransaction();
                int returnCount  = 0 ;
                try{
                    for (ContentValues value: values){

                        long id = db.insert(MovieContract.AllMoviesTable.TABLE_NAME ,null , value);
                        if(id != -1){
                            returnCount++ ;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri , null);

                return returnCount ;
            default:
                return super.bulkInsert(uri, values);

        }

    }
}
