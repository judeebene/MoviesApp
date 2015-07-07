package com.shareqube.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jude Ben on 6/8/2015.
 */
public class Movie implements Parcelable {


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    String mMovieposter;
    String mMovieReleaseDate;
    String mMovieOverview;
    String mMovieRating;
    String mReviews;
    String mTrailer;
    private int mMovieId;
    private String mMovietitle;


    public Movie(int id, String title, String poster, String releaseDate, String overview, String user_rating, String reviews, String trailer) {
        this.mMovieId = id;
        this.mMovietitle = title;
        this.mMovieposter = poster;
        this.mMovieReleaseDate = releaseDate;
        this.mMovieOverview = overview;
        this.mMovieRating = user_rating;
        this.mReviews = reviews;
        this.mTrailer = trailer;



    }

    protected Movie(Parcel in) {
        mMovieId = in.readInt();
        mMovietitle = in.readString();
        mMovieposter = in.readString();
        mMovieReleaseDate = in.readString();
        mMovieOverview = in.readString();
        mMovieRating = in.readString();
        mReviews = in.readString();
        mTrailer = in.readString();
    }

    public String getmMovietitle() {
        return mMovietitle;
    }

    public void setmMovietitle(String title) {
        mMovietitle = title;
    }

    public String getmMovieRating() {
        return mMovieRating;
    }

    public void setmMovieRating(String rating) {
        mMovieRating = rating;
    }

    public String getmMovieposter() {
        return mMovieposter;
    }

    public void setmMovieposter(String poster) {
        mMovieposter = poster;

    }

    public String getmMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    //Todo Change it to date later
    public void setmMovieReleaseDate(String date) {
        mMovieReleaseDate = date;
    }

    public String getmMovieOverview() {
        return mMovieOverview;
    }

    public void setmMovieOverview(String overview) {
        mMovieOverview = overview;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    // setters
    public void setmMovieId(int id) {
        mMovieId = id;
    }

    public String getmReviews() {

        return mReviews;
    }

    public String getmTrailer() {

        return mTrailer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieId);
        dest.writeString(mMovietitle);
        dest.writeString(mMovieposter);
        dest.writeString(mMovieReleaseDate);
        dest.writeString(mMovieOverview);
        dest.writeString(mMovieRating);
        dest.writeString(mReviews);
        dest.writeString(mTrailer);
    }
}