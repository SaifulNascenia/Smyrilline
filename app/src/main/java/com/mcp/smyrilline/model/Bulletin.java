package com.mcp.smyrilline.model;

/**
 * Model class for bulletin
 */
public class Bulletin {

    private int id;
    private String title;
    private String content;
    private String date;
    private String imageUrl;
    private boolean seen;

    public Bulletin(int id, String title, String content, String date, String imageUrl, boolean seen) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.imageUrl = imageUrl;
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean isSeen) {
        seen = isSeen;
    }

    public int getId() {
        return id;
    }
}
