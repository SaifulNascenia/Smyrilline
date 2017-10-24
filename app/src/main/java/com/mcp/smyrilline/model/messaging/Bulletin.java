package com.mcp.smyrilline.model.messaging;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for bulletin
 */
public class Bulletin implements Parcelable {

    public static final Creator<Bulletin> CREATOR = new Creator<Bulletin>() {
        @Override
        public Bulletin createFromParcel(Parcel source) {
            return new Bulletin(source);
        }

        @Override
        public Bulletin[] newArray(int size) {
            return new Bulletin[size];
        }
    };
    private int id;
    private String title;
    private String content;
    private String date;
    private String imageUrl;
    private boolean seen;

    public Bulletin(int id, String title, String content, String date, String imageUrl,
        boolean seen) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.imageUrl = imageUrl;
        this.seen = seen;
    }

    protected Bulletin(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.imageUrl = in.readString();
        this.seen = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeString(this.imageUrl);
        dest.writeByte(this.seen ? (byte) 1 : (byte) 0);
    }
}
