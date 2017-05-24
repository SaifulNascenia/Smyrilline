package com.mcp.smyrilline.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by raqib on 10/9/15.
 */

public class Restaurant implements Serializable {
    private int id;
    private String title;
    private String description;
    private String image_url;
    private ArrayList<RestaurantMenuInfoGroup> menuList;
    private boolean applyCss = true;

    public Restaurant(int id,
                      String title,
                      String description,
                      String image_url,
                      ArrayList<RestaurantMenuInfoGroup> menuList,
                      boolean applyCss) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.menuList = menuList;
        this.applyCss = applyCss;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return image_url;
    }

    public ArrayList<RestaurantMenuInfoGroup> getMenuList() {
        return menuList;
    }

    public boolean isApplyCss() {
        return applyCss;
    }
}

