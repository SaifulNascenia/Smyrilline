package com.mcp.smyrilline.model.destination;

/**
 * Created by saiful on 7/7/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ListOfDestinations implements Parcelable {

    public final static Creator<ListOfDestinations> CREATOR = new Creator<ListOfDestinations>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ListOfDestinations createFromParcel(Parcel in) {
            ListOfDestinations instance = new ListOfDestinations();
            instance.id = in.readValue((Object.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = in.readValue((Object.class.getClassLoader()));
            instance.text1 = in.readValue((Object.class.getClassLoader()));
            instance.text2 = in.readValue((Object.class.getClassLoader()));
            in.readList(instance.children, (com.mcp.smyrilline.model.restaurant.Child.class.getClassLoader()));
            return instance;
        }

        public ListOfDestinations[] newArray(int size) {
            return (new ListOfDestinations[size]);
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
    private Object imageUrl;
    @SerializedName("text1")
    @Expose
    private Object text1;
    @SerializedName("text2")
    @Expose
    private Object text2;
    @SerializedName("children")
    @Expose
    private List<Child> children = null;

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

    public Object getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getText1() {
        return text1;
    }

    public void setText1(Object text1) {
        this.text1 = text1;
    }

    public Object getText2() {
        return text2;
    }

    public void setText2(Object text2) {
        this.text2 = text2;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
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
        dest.writeValue(text1);
        dest.writeValue(text2);
        dest.writeList(children);
    }

    public int describeContents() {
        return 0;
    }

}
