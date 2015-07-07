package com.shareqube.moviesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.shareqube.moviesapp.Movie;
import com.shareqube.moviesapp.R;
import com.shareqube.moviesapp.Utility;
import com.shareqube.moviesapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Jude Ben on 6/18/2015.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the movies, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    static List<Movie> movies = new ArrayList<>();
    public Movie movie;
    String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    String API_KEY = "23d233badd61bb79fba75efdee30cc86";

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    public static List getMovieList() {

        return movies;
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


    // Helper method to get the fake account to be used with SyncAdapter,

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String sort_order = Utility.getPreferedMovieSorted(getContext());


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;


        try {
            // Construct the URL for the http://api.themoviedb.org/ api
            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=23d233badd61bb79fba75efdee30cc86

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";

            final String SORT_BY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";


            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sort_order)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(buildUri.toString());


            Log.e(LOG_TAG, "Built URL" + buildUri.toString());


            // Create the request to theMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));


            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }


        // return statement

        try {
            // prepares the data and parse the json
            movies = getMoviesDataFromJson(moviesJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }




    }

    public ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

        String YOUTUBE_BASE_URL = "https://www.youtube.com/embed/";

        final String MOVIE_RESULT = "results";

        final String MOVIE_ID = "id";

        final String MOVIE_TITLE = "title";
        final String MOVIE_POSTER_RELATIVE_PATH = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";

        final String PLOT_SYNOPSIS = "overview";

        final String USER_RATING = "vote_average";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesResultArray = moviesJson.getJSONArray(MOVIE_RESULT);

        ArrayList<Movie> resultMovies = new ArrayList<Movie>(moviesResultArray.length());

        // prepares the data and parse the json
        ArrayList<String> movieTrailerKey = null;
        ArrayList<String> movieReviews = null;

        Vector<ContentValues> moviesVectors = new Vector<>(moviesResultArray.length());
        for (int i = 0; i < moviesResultArray.length(); i++) {

            // get the first json object for the movies

            JSONObject moviesDiscover = moviesResultArray.getJSONObject(i);

            int movie_id = Integer.parseInt(moviesDiscover.getString(MOVIE_ID));


            String movie_title = moviesDiscover.getString(MOVIE_TITLE);

            String movie_poster = Utility.getMoviePosterAbsolutePath(moviesDiscover.getString(MOVIE_POSTER_RELATIVE_PATH));

            String release_date = moviesDiscover.getString(MOVIE_RELEASE_DATE);

            String movie_overview = moviesDiscover.getString(PLOT_SYNOPSIS);

            String user_rating = moviesDiscover.getString(USER_RATING);

            // populate the Movie Objects with the json data


            // populate the new movie data to database


            // test getMovieTrailerbyID its working
            String trailerStr = getMovieData(movie_id, "videos");
            String mReviews = getMovieData(movie_id, "reviews");


            try {
                movieTrailerKey = getTrailerFromJson(trailerStr);
                movieReviews = getReviewsFromJson(mReviews);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            String movie_trailer = "";

            if (null != movieTrailerKey && !movieTrailerKey.isEmpty()) {
                movie_trailer = YOUTUBE_BASE_URL + movieTrailerKey.get(0);
            }
            String movie_review = "No Review Yet";
            if (movieReviews != null && !movieReviews.isEmpty()) {

                movie_review = String.valueOf(movieReviews.get(0));

            }




            movie = new Movie(movie_id, movie_title, movie_poster, release_date, movie_overview, user_rating, movie_review, movie_trailer);


            ContentValues mValues = new ContentValues();

            mValues.put(MovieContract.AllMoviesTable.COLUMN_MOVIE_ID, movie_id);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_MOVIE_TITLE, movie_title);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_MOVIE_POSTER, movie_poster);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_MOVIE_RELEASE_DATE, release_date);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_MOVIE_OVERVIEW, movie_overview);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_USER_RATING, user_rating);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_REVIEWS, movie_review);
            mValues.put(MovieContract.AllMoviesTable.COLUMN_TRAILER_URL, movie_trailer);


            moviesVectors.add(mValues);


            resultMovies.add(movie);
        }

        int insert = 0;
        // convert vectors to array
        if (moviesVectors.size() > 0) {
            ContentValues[] valuesArray = new ContentValues[moviesVectors.size()];
            moviesVectors.toArray(valuesArray);

            // delete first before insert
            getContext().getContentResolver().delete(MovieContract.AllMoviesTable.CONTENT_URI, null, null);

            getContext().getContentResolver().bulkInsert(MovieContract.AllMoviesTable.CONTENT_URI, valuesArray);


        }

        return resultMovies;

    }

    public String getMovieData(int movie_id, String fetchUrl) {

        HttpURLConnection trailerUrlConnection = null;
        BufferedReader reader = null;

        String trailerJsonStr = null;
        ArrayList<String> movieTrailerKey = null;

        try {

            String BASE_TRAILER_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/" + fetchUrl;

            final String API_KEY_PARAM = "api_key";

            Uri buildeUri = Uri.parse(BASE_TRAILER_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY).build();

            Log.e(LOG_TAG, buildeUri.toString());

            URL url = new URL(buildeUri.toString());

            // Create the request to theMovieDB, and open the connection
            trailerUrlConnection = (HttpURLConnection) url.openConnection();
            trailerUrlConnection.setRequestMethod("GET");
            trailerUrlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = trailerUrlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));


            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            trailerJsonStr = buffer.toString();
        } catch (IOException e) {

        } finally {
            if (trailerUrlConnection != null) {
                trailerUrlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing  Trailer stream", e);
                }
            }

        }


        return trailerJsonStr;

    }


    public ArrayList<String> getTrailerFromJson(String trailerJsonStr) throws JSONException {

        String RESULT = "results";
        String TRAILER_KEY = "key";

        String key;


        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(RESULT);

        ArrayList<String> trailerKeyResult = new ArrayList<>(trailerArray.length());

        if (trailerArray.length() == 0) {
            key = " No Trailer";
        } else {
            for (int i = 0; i < trailerArray.length(); i++) {

                JSONObject trailer = trailerArray.getJSONObject(i);
                key = trailer.getString(TRAILER_KEY);


                trailerKeyResult.add(key);


            }
        }
        return trailerKeyResult;

    }

    public ArrayList<String> getReviewsFromJson(String trailerJsonStr) throws JSONException {

        String RESULT = "results";
        String MOVIE_REVIEW_CONTENT = "content";

        String reviews;


        JSONObject reviewsJson = new JSONObject(trailerJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULT);

        ArrayList<String> reviewsContentResult = new ArrayList<>(reviewsArray.length());

        if (reviewsArray.length() == 0) {
            reviews = " No Reviews ";
            reviewsContentResult = new ArrayList<>(1);
        } else {

            for (int i = 0; i < reviewsArray.length(); i++) {

                JSONObject trailer = reviewsArray.getJSONObject(i);
                reviews = trailer.getString(MOVIE_REVIEW_CONTENT);


                reviewsContentResult.add(reviews);

            }
        }

        return reviewsContentResult;

    }
}