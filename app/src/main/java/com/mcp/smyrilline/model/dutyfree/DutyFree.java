
package com.mcp.smyrilline.model.dutyfree;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class DutyFree implements Parcelable
{

    public final static Creator<DutyFree> CREATOR = new Creator<DutyFree>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DutyFree createFromParcel(Parcel in) {
            DutyFree instance = new DutyFree();
            instance.id = in.readValue((Object.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.text1 = ((String) in.readValue((String.class.getClassLoader())));
            instance.text2 = ((String) in.readValue((String.class.getClassLoader())));
            instance.text3 = in.readValue((Object.class.getClassLoader()));
            in.readList(instance.children, (Child.class.getClassLoader()));
            return instance;
        }

        public DutyFree[] newArray(int size) {
            return (new DutyFree[size]);
        }

    }
            ;
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
    private String text2;
    @SerializedName("text3")
    @Expose
    private Object text3;
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

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public Object getText3() {
        return text3;
    }

    public void setText3(Object text3) {
        this.text3 = text3;
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
        dest.writeValue(text3);
        dest.writeList(children);
    }

    public int describeContents() {
        return  0;
    }

}
