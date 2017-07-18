
package com.mcp.smyrilline.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DinnerItem implements Parcelable
{

    public final static Creator<DinnerItem> CREATOR = new Creator<DinnerItem>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DinnerItem createFromParcel(Parcel in) {
            DinnerItem instance = new DinnerItem();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.header = ((String) in.readValue((String.class.getClassLoader())));
            instance.subheader = ((String) in.readValue((String.class.getClassLoader())));
            instance.text3 = in.readValue((Object.class.getClassLoader()));
            instance.children = in.readValue((Object.class.getClassLoader()));
            return instance;
        }

        public DinnerItem[] newArray(int size) {
            return (new DinnerItem[size]);
        }

    }
    ;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("header")
    @Expose
    private String header;
    @SerializedName("subheader")
    @Expose
    private String subheader;
    @SerializedName("text3")
    @Expose
    private Object text3;
    @SerializedName("children")
    @Expose
    private Object children;

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

    public Object getText3() {
        return text3;
    }

    public void setText3(Object text3) {
        this.text3 = text3;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(imageUrl);
        dest.writeValue(header);
        dest.writeValue(subheader);
        dest.writeValue(text3);
        dest.writeValue(children);
    }

    public int describeContents() {
        return  0;
    }

}
