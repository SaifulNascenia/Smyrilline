package com.mcp.smyrilline.model.shipinfo;

import android.os.Parcel;
import java.util.List;

public class ShipInfo implements android.os.Parcelable {

    public static final Creator<ShipInfo> CREATOR = new Creator<ShipInfo>() {
        @Override
        public ShipInfo createFromParcel(Parcel source) {
            return new ShipInfo(source);
        }

        @Override
        public ShipInfo[] newArray(int size) {
            return new ShipInfo[size];
        }
    };
    private String id;
    private String name;
    private String imageUrl;
    private String header;
    private String subheader;
    private String description;
    private List<Child> children = null;

    protected ShipInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.header = in.readString();
        this.subheader = in.readString();
        this.description = in.readString();
        this.children = in.createTypedArrayList(Child.CREATOR);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeString(this.header);
        dest.writeString(this.subheader);
        dest.writeString(this.description);
        dest.writeTypedList(this.children);
    }
}
