package com.mcp.smyrilline.model;

import java.io.Serializable;

/**
 * Created by raqib on 10/1/15.
 */
public class RestaurantMenuInfoChild implements Serializable {
    private String description;
    private String imageURL;
    private boolean applyCss;

    public RestaurantMenuInfoChild(String imageURL, String description, boolean applyCss) {
        this.imageURL = imageURL;
        this.description = description;
        this.applyCss = applyCss;
    }

    public String getDescription() {
        return description;
    }
    public String getImageURL() {
        return imageURL;
    }

    public boolean isApplyCss() {
        return applyCss;
    }
}
