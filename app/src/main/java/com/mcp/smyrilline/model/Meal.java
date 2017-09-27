package com.mcp.smyrilline.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raqib on 4/26/16.
 */
public class Meal implements Comparable<Meal> {

    private boolean used;
    private String count;
    private String description;
    private String dateString;
    private Date date;
//    private long mDateMillis;

    public Meal(String count, String description, String dateString) {
        this.count = count;
        this.description = description;
        this.used = false;
        this.dateString = dateString;

        // get long millis, we'll add 23:59
        SimpleDateFormat givenFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            date = givenFormat.parse(this.dateString + " " + "23:59");
//            mDateMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return dateString;
    }

//    public long getDateMillis() {
//        return mDateMillis;
//    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public int compareTo(Meal another) {
        return date.compareTo(another.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true; //if both pointing towards same object on heap

        boolean retVal = false;
        if (o instanceof Meal) {
            Meal compareMeal = (Meal) o;
            retVal = date.equals(compareMeal.getDate());
        }

        return retVal;
    }
}
