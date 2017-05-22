package com.mcp.smyrilline.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by raqib on 4/27/16.
 */
public class MealDate implements Comparable<MealDate> {
    private Date mDate;
    private String mDateString;
    private ArrayList<Meal> mMealList;

    public MealDate(String dateString) {
        mDateString = dateString;

        // From booking system getting eg. 30-04-2016
        // we will add 23:59 at the end
        SimpleDateFormat givenFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            mDate = givenFormat.parse(mDateString + " " + "23:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getDate() {
        return mDate;
    }

    public ArrayList<Meal> getMealList() {
        return mMealList;
    }

    public void setMealList(ArrayList<Meal> mealList) {
        mMealList = mealList;
    }

    @Override
    public int compareTo(MealDate o) {
        return mDate.compareTo(o.getDate());
    }

    public String getDateString() {
        return mDateString;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true; //if both pointing towards same object on heap

        boolean retVal = false;
        if (o instanceof MealDate) {
            MealDate compareMeal = (MealDate) o;
            retVal = mDate.equals(compareMeal.getDate());
        }

        return retVal;
    }
}
