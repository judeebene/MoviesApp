package com.shareqube.moviesapp;

/**
 * Created by Jude Ben on 6/11/2015.
 */


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.shareqube.moviesapp.data.MovieContract;
import com.shareqube.moviesapp.data.MovieContract.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final int MOVIE_DETAIL_LOADER = 3;
    String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;

    ViewHolder holder;
    String trailer;
    Bitmap posterBitMap;
    String[] DETAIL_COLUMNS = {
            AllMoviesTable.COLUMN_MOVIE_ID,
            AllMoviesTable.COLUMN_MOVIE_TITLE,
            AllMoviesTable.COLUMN_MOVIE_POSTER,
            AllMoviesTable.COLUMN_MOVIE_RELEASE_DATE,
            AllMoviesTable.COLUMN_MOVIE_OVERVIEW,
            AllMoviesTable.COLUMN_USER_RATING,
            AllMoviesTable.COLUMN_REVIEWS,
            AllMoviesTable.COLUMN_TRAILER_URL

    };
    // indices to DETAIL_COLUMNS
    int COL_ID = 0;
    int COL_TITLE = 1;
    int COL_POSTER = 2;
    int COL_RELEASE_DATE = 3;
    int COL_OVERVIEW = 4;
    int COL_USER_RATING = 5;
    int COL_REVIEWS = 6;
    int COL_TRAILER_URL = 7;
    private Uri mUri;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (null != arguments) {
            mUri = arguments.getParcelable("movie");
        }
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        holder = new ViewHolder(rootView);
        rootView.setTag(holder);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);

        getActivity().invalidateOptionsMenu();
        super.onActivityCreated(savedInstanceState);


    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_movie_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingInent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingInent);
                break;

            default:
                return false;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (null != mUri) {
            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        int position = MovieContract.AllMoviesTable.getIDFromUri(mUri);


        if (data != null && data.moveToPosition(position)) {

            final String movie_id = data.getString(COL_ID);
            final String overview = data.getString(COL_OVERVIEW);
            final String release_date = data.getString(COL_RELEASE_DATE);
            final String user_rating = data.getString(COL_USER_RATING);
            final String title = data.getString(COL_TITLE);
            final String poster = data.getString(COL_POSTER);
            final String reviews = data.getString(COL_REVIEWS);
            trailer = data.getString(COL_TRAILER_URL);
            Log.e(LOG_TAG, "trailer from db" + trailer);



            Bitmap b =  Utility.loadImageFromStorage(poster , title);
            Log.e(LOG_TAG , "bit from local" + b) ;

            ///testing to display image from local storage
            String sorted = Utility.getPreferedMovieSorted(getActivity());

            if(b != null) {
                holder.detailPoster.setImageBitmap(b);
            }
            else {
                Glide.with(this)
                        .load(poster)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(50, 50) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                MovieDetailFragment.this.posterBitMap = bitmap;
                                holder.detailPoster.setImageBitmap(bitmap);
                            }
                        });

            }


            holder.movieOverview.setText(overview);
            holder.movieRelease.setText(release_date);
            holder.movieTitle.setText(title);

            Float rating = Float.parseFloat(user_rating);
            holder.movieRating.setRating(rating);

            holder.movieRatingValue.setText(user_rating);

            holder.trailerBtn.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {



                    Intent trailerIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(trailer));

                   ActivityInfo activityInfo = trailerIntent.resolveActivityInfo(getActivity().getPackageManager(),trailerIntent.getFlags());

                    if (activityInfo.exported) {
                        startActivity(trailerIntent);
                    }
                    else {
                        Toast.makeText(getActivity() , "Sorry , No Apps to play Trailer Video on your Device ",Toast.LENGTH_LONG).show();
                    }


                }
            });

            holder.movieReviews.setText(reviews);


            holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String path =  Utility.saveToInternalSorage(posterBitMap, title, getActivity().getApplication());
                    ContentValues favoriteValues = new ContentValues();

                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FMOVIE_ID, movie_id);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FMOVIE_TITLE, title);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FMOVIE_POSTER, path);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FMOVIE_RELEASE_DATE, release_date);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FMOVIE_OVERVIEW, overview);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FUSER_RATING, user_rating);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FREVIEWS, reviews);
                    favoriteValues.put(FavoriteMoviesTable.COLUMN_FTRAILER_URL, trailer);

                    Uri i = getActivity().getContentResolver().insert(FavoriteMoviesTable.CONTENT_URI, favoriteValues);
                    Log.e(LOG_TAG , "insert uri" + i );
                    Toast.makeText(getActivity(), title + "has been added to Favorite List", Toast.LENGTH_LONG).show();


                }
            });

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }


        }


    private Intent createShareIntent( ) {

        Intent shareintent = ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setText(trailer).getIntent();
        shareintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);



        return shareintent;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }







    static class ViewHolder {
        ImageView detailPoster;
        TextView movieTitle;
        TextView movieRelease;
        TextView movieOverview;
        RatingBar movieRating;
        TextView movieRatingValue;
        ImageButton trailerBtn;
        TextView movieReviews;
        ImageButton favoriteBtn;

        public ViewHolder(View v) {
            detailPoster = (ImageView) v.findViewById(R.id.detailPoster_view);
            movieTitle = (TextView) v.findViewById(R.id.movie_title_view);
            movieRelease = (TextView) v.findViewById(R.id.movie_release_view);
            movieOverview = (TextView) v.findViewById(R.id.movie_overview_view);
            movieRating = (RatingBar) v.findViewById(R.id.movieRatingBar);
            movieRatingValue = (TextView) v.findViewById(R.id.movieRatingValue);
            trailerBtn = (ImageButton) v.findViewById(R.id.play_trailer);
            movieReviews = (TextView) v.findViewById(R.id.movie_reviews_view);
            favoriteBtn = (ImageButton) v.findViewById(R.id.favorite_btn);
        }

    }


}