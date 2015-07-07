package com.shareqube.moviesapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.FutureTarget;

import com.shareqube.moviesapp.Movie;

import com.shareqube.moviesapp.R;
import com.shareqube.moviesapp.Utility;



import java.io.File;
import java.util.List;

/**
 * Created by Jude Ben on 6/7/2015.
 */
public class MoviesAdapter extends CursorAdapter {

    static Context mContext;
    String LOG_TAG = MoviesAdapter.class.getSimpleName();
    List<Movie> movie_poster;
    List<String> path;



    LayoutInflater minflater;
    ViewHolder holder;



    public MoviesAdapter(Context context, Cursor c, int flag) {
        super(context, c, flag);


        minflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;

    }




    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);

        holder = new ViewHolder(view);

        view.setTag(holder);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        holder = (ViewHolder) view.getTag();

        holder.moviesPoster.setAdjustViewBounds(true);




        String poster = cursor.getColumnName(3);
        String title_col = cursor.getColumnName(2) ;
        String title  = cursor.getString(cursor.getColumnIndex(title_col));

        String url = cursor.getString(cursor.getColumnIndex(poster));

        // test if its favorite movies and display local images also refactor ur codes to utilty

         String sorted = Utility.getPreferedMovieSorted(context) ;

        if(sorted.equals("favorite")){

            Bitmap bitmap = Utility.loadImageFromStorage(url, title);


             holder.moviesPoster.setImageBitmap(bitmap);

        } else {

            Glide.with(mContext).load(url).error(R.drawable.no_poster).into(holder.moviesPoster);
        }

    }

    static class ViewHolder {


        ImageView moviesPoster;

        public ViewHolder(View v) {
            moviesPoster = (ImageView) v.findViewById(R.id.poster);
        }


    }


    private class DownloadPosterToCacheTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... params) {
            FutureTarget<File> future = Glide.with(mContext)
                    .load(params[0])
                    .downloadOnly(256, 256);

            File file = null;
            try {
                file = future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }
    }
}
