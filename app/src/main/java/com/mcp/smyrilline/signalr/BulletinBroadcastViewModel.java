package com.mcp.smyrilline.signalr;

public class BulletinBroadcastViewModel {
    private int id;
    private int user_id;
    private String catagory_name;
    private String title;
    private String description;
    private String image_url;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCatagory_name() {
        return catagory_name;
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
}