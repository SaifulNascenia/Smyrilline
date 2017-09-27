
package com.mcp.smyrilline.model.destination;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DestinationDetailsChild implements Parcelable {

    public final static Creator<DestinationDetailsChild> CREATOR = new Creator<DestinationDetailsChild>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DestinationDetailsChild createFromParcel(Parcel in) {
            DestinationDetailsChild instance = new DestinationDetailsChild();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.text1 = ((String) in.readValue((String.class.getClassLoader())));
            instance.text2 = ((String) in.readValue((String.class.getClassLoader())));
            instance.text3 = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.children,
                    (com.mcp.smyrilline.model.destination.DestinationDetailsChild.class
                            .getClassLoader()));
            return instance;
        }

        public DestinationDetailsChild[] newArray(int size) {
            return (new DestinationDetailsChild[size]);
        }

    };
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
    private String text1;
    @SerializedName("text2")
    @Expose
    private String text2;
    @SerializedName("text3")
    @Expose
    private String text3;
    @SerializedName("children")
    @Expose
    private List<DestinationDetailsChild> children;

    public DestinationDetailsChild(String name, String imageUrl, String text1, String text2,
            String text3) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    public DestinationDetailsChild() {
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

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
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
        dest.writeValue(children);
    }

    public int describeContents() {
        return 0;
    }

}
