package com.shareqube.moviesapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Jude Ben on 6/18/2015.
 *
 */


public class Utility {



    public static String getPreferedMovieSorted(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String sort_order = preferences.getString(context.getString(R.string.movie_sort_key),
                context.getString(R.string.movie_sort_order_default_value));


        return sort_order;

    }

    //http://stackoverflow.com/questions/17674634/saving-images-to-internal-memory-in-android
    public static String saveToInternalSorage(Bitmap bitmapImage, String posterName ,  Context c) {
        ContextWrapper cw = new ContextWrapper(c);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String imgName = posterName + ".png";
        // Create imageDir
        File mypath = new File(directory, imgName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    // load local store image

    public static Bitmap loadImageFromStorage(String path, String ImageName) {
        String name = ImageName + ".png";
        Bitmap b = null;
        try {
            File f = new File(path, name);
            b = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;

    }


    public static String getMoviePosterAbsolutePath(String relative_path) {

        final String BASE_MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/";
        String[] IMAGE_SIZES = {"w185", "w92", "w154", "w342", "w500", "w780"};


        return BASE_MOVIE_POSTER_URL + IMAGE_SIZES[0] + relative_path;
    }

    public static Boolean isNetworkAvailable(Context c) {

        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
