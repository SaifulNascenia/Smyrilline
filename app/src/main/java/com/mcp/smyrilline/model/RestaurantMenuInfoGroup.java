package com.mcp.smyrilline.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by raqib on 10/1/15.
 */
public class RestaurantMenuInfoGroup implements Serializable {

    private String title;
    // will add the description and image as the only child in the childList
    private ArrayList<RestaurantMenuInfoChild> childList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<RestaurantMenuInfoChild> getChildList() {
        return childList;
    }

    public void setChildList(ArrayList<RestaurantMenuInfoChild> childList) {
        this.childList = childList;
    }
}
