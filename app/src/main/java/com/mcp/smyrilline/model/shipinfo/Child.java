
package com.mcp.smyrilline.model.shipinfo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child implements Parcelable
{

    public final static Creator<Child> CREATOR = new Creator<Child>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Child createFromParcel(Parcel in) {
            Child instance = new Child();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.header = ((String) in.readValue((String.class.getClassLoader())));
            instance.subheader = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.children = in.readValue((Object.class.getClassLoader()));
            return instance;
        }

        public Child[] newArray(int size) {
            return (new Child[size]);
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
    @SerializedName("description")
    @Expose
    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getChildren() {
        return children;
    }

    public void setChildren(Object children) {
        this.children = children;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(imageUrl);
        dest.writeValue(header);
        dest.writeValue(subheader);
        dest.writeValue(description);
        dest.writeValue(children);
    }

    public int describeContents() {
        return  0;
    }

}
