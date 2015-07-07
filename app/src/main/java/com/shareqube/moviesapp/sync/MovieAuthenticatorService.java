package com.shareqube.moviesapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Jude Ben on 6/18/2015.
 */
public class MovieAuthenticatorService extends Service {

    MoviesAuthenticator moviesAuthenticator;


    @Override
    public void onCreate() {
        moviesAuthenticator = new MoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return moviesAuthenticator.getIBinder();
    }
}
