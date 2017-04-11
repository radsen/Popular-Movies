package com.kzlabs.popularmovies.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.kzlabs.popularmovies.data.PopularMoviesContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by radsen on 11/28/16.
 */

public class Movie implements Parcelable {

    private int id;
    private String poster;
    private String title;
    private int runtime;
    private String overview;
    private float average;
    private int year;
    private List<Comment> commentList;
    private List<Trailer> trailerList;
    private boolean favorite; // Not required for the parcel constructor for now.
    private Bitmap image;
    private String releaseDate;

    public Movie(){
        commentList = new ArrayList<>();
        trailerList = new ArrayList<>();
    }

    public Movie(Parcel in) {
        id = in.readInt();
        poster = in.readString();
        title = in.readString();
        runtime = in.readInt();
        overview = in.readString();
        average = in.readFloat();
        releaseDate = in.readString();
        year = in.readInt();
        in.readTypedList(commentList, Comment.CREATOR);
        in.readTypedList(trailerList, Trailer.CREATOR);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public void setPoster(String poster_path) {
        this.poster = poster_path;
    }

    public String getPoster(){
        return  poster;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getAverage() {
        return average;
    }

    public int getYear() {
        return year;
    }

    public void setYear(String format, String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(strDate);
            calendar.setTime(date);
            year = calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void addComments(Comment comment){
        commentList.add(comment);
    }

    public List<Comment> getComments(){
        return commentList;
    }

    public List<Trailer> getTrailers(){
        return trailerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(poster);
        parcel.writeString(title);
        parcel.writeInt(runtime);
        parcel.writeString(overview);
        parcel.writeFloat(average);
        parcel.writeString(releaseDate);
        parcel.writeInt(year);
        parcel.writeList(commentList);
        parcel.writeList(trailerList);
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailerList = trailers;
    }

    public void setComments(List<Comment> comments) {
        this.commentList = comments;
    }

    public void setAsFavorite(boolean isFavorite) {
        this.favorite = isFavorite;
    }

    public boolean isFavorite(){
        return favorite;
    }

    public void setBitmap(Bitmap bitmap){
        image = bitmap;
    }

    public Bitmap getBitmap() {
        return image;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean hasTrailers() {
        return trailerList != null && trailerList.size() > 0;
    }

    public boolean hasComments() {
        return commentList != null && commentList.size() > 0;
    }
}
