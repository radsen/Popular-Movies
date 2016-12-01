package com.kzlabs.popularmovies.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by radsen on 11/28/16.
 */

public class Movie {

    private int id;
    private String poster;
    private String title;
    private int runtime;
    private String overview;
    private float average;
    private int year;

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
}
