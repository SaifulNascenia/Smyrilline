package com.mcp.smyrilline.model;

/**
 * Model class for bulletin
 */
public class Bulletin {
    private int mId;
    private String mTitle;
    private String mContent;
    private String mDate;
    private String mImageURL;
    private boolean mSeen;

    public Bulletin(int id, String title, String content, String date, String imageUrl, boolean seen) {
        this.mId = id;
        this.mTitle = title;
        this.mContent = content;
        this.mDate = date;
        this.mImageURL = imageUrl;
        this.mSeen = seen;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getDate() {
        return mDate;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public boolean isSeen() {
        return mSeen;
    }

    public void setSeen(boolean isSeen) {
        mSeen = isSeen;
    }

    public int getId() {
        return mId;
    }
}
