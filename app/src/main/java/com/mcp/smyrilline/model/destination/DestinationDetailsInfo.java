
package com.mcp.smyrilline.model.destination;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DestinationDetailsInfo implements Parcelable {

    public final static Creator<DestinationDetailsInfo> CREATOR = new Creator<DestinationDetailsInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DestinationDetailsInfo createFromParcel(Parcel in) {
            DestinationDetailsInfo instance = new DestinationDetailsInfo();
            instance.id = in.readValue((Object.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.text1 = ((String) in.readValue((String.class.getClassLoader())));
            instance.text2 = in.readValue((Object.class.getClassLoader()));
            instance.text3 = in.readValue((Object.class.getClassLoader()));
            in.readList(instance.children, (com.mcp.smyrilline.model.shipinfo.Child.class.getClassLoader()));
            return instance;
        }

        public DestinationDetailsInfo[] newArray(int size) {
            return (new DestinationDetailsInfo[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("text1")
    @Expose
    private String text1;
    @SerializedName("text2")
    @Expose
    private Object text2;
    @SerializedName("text3")
    @Expose
    private Object text3;
    @SerializedName("children")
    @Expose
    private List<DestinationDetailsChild> children = null;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
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

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public Object getText2() {
        return text2;
    }

    public void setText2(Object text2) {
        this.text2 = text2;
    }

    public Object getText3() {
        return text3;
    }

    public void setText3(Object text3) {
        this.text3 = text3;
    }

    public List<DestinationDetailsChild> getChildren() {
        return children;
    }

    public void setChildren(List<DestinationDetailsChild> children) {
        this.children = children;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(imageUrl);
        dest.writeValue(text1);
        dest.writeValue(text2);
        dest.writeValue(text3);
        dest.writeList(children);
    }

    public int describeContents() {
        return 0;
    }

}
