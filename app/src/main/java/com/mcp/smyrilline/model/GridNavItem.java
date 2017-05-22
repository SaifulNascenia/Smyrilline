package com.mcp.smyrilline.model;

/**
 * Created by raqib on 5/4/17.
 */

public class GridNavItem {
    private String title;
    private int imageId;

    public GridNavItem(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageId() {
        return imageId;
    }
}
