
package com.mcp.smyrilline.model.restaurentsmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Child implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("text1")
    @Expose
    private Object text1;
    @SerializedName("text2")
    @Expose
    private Object text2;
    @SerializedName("children")
    @Expose
    private Object children;
    public final static Creator<Child> CREATOR = new Creator<Child>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Child createFromParcel(Parcel in) {
            Child instance = new Child();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.text1 = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.text2 = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.children = ((Object) in.readValue((Object.class.getClassLoader())));
            return instance;
        }

        public Child[] newArray(int size) {
            return (new Child[size]);
        }

    }
    ;

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
        dest.writeValue(text1);
        dest.writeValue(text2);
        dest.writeValue(children);
    }

    public int describeContents() {
        return  0;
    }

}
