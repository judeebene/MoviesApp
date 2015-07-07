package com.shareqube.moviesapp;

/**
 * Created by Jude Ben on 6/8/2015.
 */


import android.database.AbstractCursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.shareqube.moviesapp.adapter.MoviesAdapter;
import com.shareqube.moviesapp.data.MovieContract;
import com.shareqube.moviesapp.sync.MovieSyncAdapter;


public class MovieDiscoveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    static final int COL_ID = 0;
    static final int COL_MOV_ID = 1;
    static final int COL_MOV_TITLE = 2;
    static final int COL_MOV_POSTER = 3;
    static final int COL_MOV_REL_DATE = 4;
    static final int COL_MOV_OVERVIEW = 5;
    static final int COL_USER_RATING = 6;
    static final int COL_REVIEWS = 7;
    static final int COL_TRAILER = 8;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MAIN_LOADER = 2;
    private static final int FAVORITE_LOADER = 3;
    private static String[] MOVIES_COLUMN = {

            MovieContract.AllMoviesTable._ID,
            MovieContract.AllMoviesTable.COLUMN_MOVIE_ID,
            MovieContract.AllMoviesTable.COLUMN_MOVIE_TITLE,
            MovieContract.AllMoviesTable.COLUMN_MOVIE_POSTER,
            MovieContract.AllMoviesTable.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.AllMoviesTable.COLUMN_MOVIE_OVERVIEW,
            MovieContract.AllMoviesTable.COLUMN_USER_RATING,
            MovieContract.AllMoviesTable.COLUMN_REVIEWS,
            MovieContract.AllMoviesTable.COLUMN_TRAILER_URL

    };
    private static String[] FAVORITES_MOVIE_COLUMN = {
            MovieContract.FavoriteMoviesTable._ID,
            MovieContract.FavoriteMoviesTable.COLUMN_FMOVIE_ID,
            MovieContract.FavoriteMoviesTable.COLUMN_FMOVIE_TITLE,
            MovieContract.FavoriteMoviesTable.COLUMN_FMOVIE_POSTER,
            MovieContract.FavoriteMoviesTable.COLUMN_FMOVIE_RELEASE_DATE,
            MovieContract.FavoriteMoviesTable.COLUMN_FMOVIE_OVERVIEW,
            MovieContract.FavoriteMoviesTable.COLUMN_FUSER_RATING,
            MovieContract.FavoriteMoviesTable.COLUMN_FREVIEWS,
            MovieContract.FavoriteMoviesTable.COLUMN_FTRAILER_URL

    };
    String LOG_TAG = MovieDiscoveryFragment.class.getSimpleName();
    GridView moviesPosterGrid;
    int mPosition = GridView.INVALID_POSITION;

    MoviesAdapter mMoviesAdapter;


    public MovieDiscoveryFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      // initLoader(FAVORITE_LOADER, null, this, getLoaderManager());
        initLoader(MAIN_LOADER , null , this , getLoaderManager());

        this.moviesPosterGrid.setSelection(R.drawable.touch_selector);


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery_screen, container, false);


        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        // movieArrayAdapter = new MovieArrayAdapter(getActivity(), 0);

        moviesPosterGrid = (GridView) rootView.findViewById(R.id.movie_gridview);
        TextView emptyMovies = (TextView) rootView.findViewById(R.id.movie_data_empty_view);
        moviesPosterGrid.setEmptyView(emptyMovies);

        moviesPosterGrid.setAdapter(mMoviesAdapter);

        moviesPosterGrid.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                       long  col_id =  cursor.getLong(COL_ID) ;
                        Log.e(LOG_TAG , "row id"+ col_id);




                       // Log.e(LOG_TAG  , "id d"+  cursor.getNotificationUri());

                        Uri currentUri;

                       String sorted =  Utility.getPreferedMovieSorted(getActivity());
                        if(sorted.equals("favorite")){


                            currentUri = MovieContract.FavoriteMoviesTable.getFavoriteMoviesUri(position);

                        }
                        else {
                            currentUri = MovieContract.AllMoviesTable.getAllMoviesUri(position);
                        }






                        // Uri uri = Uri.parse(MovieContract.AllMoviesTable.CONTENT_URI + "/" +id) ;
                        if (null != cursor) {


                            ((CallerBack) getActivity()).onMovieSelected(currentUri);
                        }
                        mPosition = position;
                    }


                }
        );
        // to maintain the position on device rotation

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri allMoviesUri = MovieContract.AllMoviesTable.CONTENT_URI;
        Uri favoriteUri = MovieContract.FavoriteMoviesTable.CONTENT_URI;
        CursorLoader loader;

        switch (id) {
            case FAVORITE_LOADER:

                return new CursorLoader(getActivity(), favoriteUri, FAVORITES_MOVIE_COLUMN,
                        null, null, null);


            default:
                return new CursorLoader(getActivity(), allMoviesUri,
                        MOVIES_COLUMN, null, null, null);


        }


    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // loader.getId(); to get the id of Loader, incase of multiple loader

        int id = loader.getId();
        switch (id) {

            case FAVORITE_LOADER:

                Log.e(LOG_TAG, "loading the favorite data");


                mMoviesAdapter.swapCursor(data);






                    if (mPosition != GridView.INVALID_POSITION) {

                        moviesPosterGrid.smoothScrollByOffset(mPosition);
                    }


                break;

            default:

                mMoviesAdapter.swapCursor(data);

                if (mPosition != GridView.INVALID_POSITION) {

                moviesPosterGrid.smoothScrollByOffset(mPosition);
                }

                break;


        }


        updateEmptyView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        int id = loader.getId();

        switch (id) {
            case FAVORITE_LOADER:
                mMoviesAdapter.swapCursor(null);
                break;
            default:
                mMoviesAdapter.swapCursor(null);
                break;

        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // saving the data into Parcelable object
    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (movie != null) {
            outState.putParcelable("mMovie", movie);
        }
        super.onSaveInstanceState(outState);
    }
    */


    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_discovery_screen, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.action_settings:

                Intent settingIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingIntent);
                break;

            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMovies() {

        String sorted = Utility.getPreferedMovieSorted(getActivity());

        if (sorted.equals("favorite")) {
            Log.e(LOG_TAG, "Clear adapter and load favorite");

            initLoader(FAVORITE_LOADER , null , this , getLoaderManager());

            ((MoviesAdapter) moviesPosterGrid.getAdapter()).notifyDataSetChanged();





        } else {

            MovieSyncAdapter.syncImmediately(getActivity());
        }


    }

    public void updateEmptyView() {
        if (mMoviesAdapter.getCount() == 0) {
            TextView textView = (TextView) getView().findViewById(R.id.movie_data_empty_view);

            if (null != textView) {
                int message = R.string.no_movie_data;
                if (Utility.isNetworkAvailable(getActivity())) {
                    message = R.string.no_network_available;
                }
                textView.setText(message);
            }
        }
    }


    public interface CallerBack {

        public void onMovieSelected(Uri uri);
    }

   // should not download data again from the server cause the data is already available on orientaton change

    public static <T> void initLoader(final int loaderId, final Bundle args, final LoaderManager.LoaderCallbacks<T> callbacks,
                                      final LoaderManager loaderManager) {
        final Loader<T> loader = loaderManager.getLoader(loaderId);
        if (loader != null && loader.isReset()) {
            loaderManager.restartLoader(loaderId, args, callbacks);
        } else {
            loaderManager.initLoader(loaderId, args, callbacks);
        }
    }



}

