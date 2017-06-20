
package com.mcp.smyrilline.model.restaurentsmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ChildMeals implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("prebookPrice")
    @Expose
    private String prebookPrice;
    @SerializedName("onboardPrice")
    @Expose
    private String onboardPrice;
    @SerializedName("save")
    @Expose
    private String save;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("seatingTime")
    @Expose
    private String seatingTime;
    @SerializedName("seatingText")
    @Expose
    private String seatingText;
    public final static Creator<ChildMeals> CREATOR = new Creator<ChildMeals>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ChildMeals createFromParcel(Parcel in) {
            ChildMeals instance = new ChildMeals();
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.tag = ((String) in.readValue((String.class.getClassLoader())));
            instance.prebookPrice = ((String) in.readValue((String.class.getClassLoader())));
            instance.onboardPrice = ((String) in.readValue((String.class.getClassLoader())));
            instance.save = ((String) in.readValue((String.class.getClassLoader())));
            instance.time = ((String) in.readValue((Object.class.getClassLoader())));
            instance.seatingTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.seatingText = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ChildMeals[] newArray(int size) {
            return (new ChildMeals[size]);
        }

    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPrebookPrice() {
        return prebookPrice;
    }

    public void setPrebookPrice(String prebookPrice) {
        this.prebookPrice = prebookPrice;
    }

    public String getOnboardPrice() {
        return onboardPrice;
    }

    public void setOnboardPrice(String onboardPrice) {
        this.onboardPrice = onboardPrice;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSeatingTime() {
        return seatingTime;
    }

    public void setSeatingTime(String seatingTime) {
        this.seatingTime = seatingTime;
    }

    public String getSeatingText() {
        return seatingText;
    }

    public void setSeatingText(String seatingText) {
        this.seatingText = seatingText;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(tag);
        dest.writeValue(prebookPrice);
        dest.writeValue(onboardPrice);
        dest.writeValue(save);
        dest.writeValue(time);
        dest.writeValue(seatingTime);
        dest.writeValue(seatingText);
    }

    public int describeContents() {
        return 0;
    }

}
