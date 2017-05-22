package com.mcp.smyrilline.model;

/**
 * Created by raqib on 5/18/17.
 */

public class DrawerItem {

    private String title;
    private  int imageResId;

    public DrawerItem(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
