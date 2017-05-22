package com.mcp.smyrilline.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raqib on 4/26/16.
 */
public class Meal implements Comparable<Meal> {

    private boolean mUsed;
    private String mCount;
    private String mDescription;
    private String mDateString;
    private Date mDate;
//    private long mDateMillis;

    public Meal(String count, String description, String dateString) {
        this.mCount = count;
        this.mDescription = description;
        this.mUsed = false;
        this.mDateString = dateString;

        // get long millis, we'll add 23:59
        SimpleDateFormat givenFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            mDate = givenFormat.parse(mDateString + " " + "23:59");
//            mDateMillis = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCount() {
        return mCount;
    }

    public String getDescription() {
        return mDescription;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDateString() {
        return mDateString;
    }

//    public long getDateMillis() {
//        return mDateMillis;
//    }

    public boolean isUsed() {
        return mUsed;
    }

    public void setUsed(boolean used) {
        this.mUsed = used;
    }

    @Override
    public int compareTo(Meal another) {
        return mDate.compareTo(another.getDate());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true; //if both pointing towards same object on heap

        boolean retVal = false;
        if (o instanceof Meal) {
            Meal compareMeal = (Meal) o;
            retVal = mDate.equals(compareMeal.getDate());
        }

        return retVal;
    }
}
