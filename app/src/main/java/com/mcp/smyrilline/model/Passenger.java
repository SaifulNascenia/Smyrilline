package com.mcp.smyrilline.model;

/**
 * Created by raqib on 4/26/16.
 */
public class Passenger {

    private String name;
    private String sex;
    private String dob;
    private String country;

    public Passenger(String name, String sex, String dob, String country) {
        this.name = name;
        this.sex = sex;
        this.dob = dob;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getDOB() {
        return dob;
    }

    public String getCountry() {
        return country;
    }
}
