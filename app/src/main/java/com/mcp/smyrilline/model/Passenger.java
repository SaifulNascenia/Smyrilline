package com.mcp.smyrilline.model;

/**
 * Created by raqib on 4/26/16.
 */
public class Passenger {
    private String mName;
    private String mSex;
    private String mDOB;
    private String mNationality;

    public Passenger(String name, String sex, String dob, String nationality) {
        this.mName = name;
        this.mSex = sex;
        this.mDOB = dob;
        this.mNationality = nationality;
    }

    public String getName() {
        return mName;
    }

    public String getSex() {
        return mSex;
    }

    public String getDOB() {
        return mDOB;
    }

    public String getNationality() {
        return mNationality;
    }
}
