
package com.mcp.smyrilline.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class RestaurantDetails implements Parcelable
{

    public final static Creator<RestaurantDetails> CREATOR = new Creator<RestaurantDetails>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RestaurantDetails createFromParcel(Parcel in) {
            RestaurantDetails instance = new RestaurantDetails();
            instance.breakfastTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.lunchTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.dinnerTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.openCloseTimeText = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.adultMeals, (AdultMeal.class.getClassLoader()));
            in.readList(instance.childrenMeals, (ChildMeals.class.getClassLoader()));
            in.readList(instance.breakfastItems, (BreakfastItem.class.getClassLoader()));
            in.readList(instance.lunchItems, (LunchItem.class.getClassLoader()));
            in.readList(instance.dinnerItems, (DinnerItem.class.getClassLoader()));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.text1 = in.readValue((Object.class.getClassLoader()));
            instance.text2 = in.readValue((Object.class.getClassLoader()));
            instance.text3 = in.readValue((Object.class.getClassLoader()));
            instance.children = in.readValue((Object.class.getClassLoader()));
            return instance;
        }

        public RestaurantDetails[] newArray(int size) {
            return (new RestaurantDetails[size]);
        }

    };
    @SerializedName("breakfastTime")
    @Expose
    private String breakfastTime;
    @SerializedName("lunchTime")
    @Expose
    private String lunchTime;
    @SerializedName("dinnerTime")
    @Expose
    private String dinnerTime;
    @SerializedName("openCloseTimeText")
    @Expose
    private String openCloseTimeText;
    @SerializedName("adultMeals")
    @Expose
    private List<AdultMeal> adultMeals = null;
    @SerializedName("childrenMeals")
    @Expose
    private List<ChildMeals> childrenMeals = null;
    @SerializedName("breakfastItems")
    @Expose
    private List<BreakfastItem> breakfastItems = null;
    @SerializedName("lunchItems")
    @Expose
    private List<LunchItem> lunchItems = null;
    @SerializedName("dinnerItems")
    @Expose
    private List<DinnerItem> dinnerItems = null;
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
    @SerializedName("text3")
    @Expose
    private Object text3;
    @SerializedName("children")
    @Expose
    private Object children;

    public String getBreakfastTime() {
        return breakfastTime;
    }

    public void setBreakfastTime(String breakfastTime) {
        this.breakfastTime = breakfastTime;
    }

    public String getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }

    public String getDinnerTime() {
        return dinnerTime;
    }

    public void setDinnerTime(String dinnerTime) {
        this.dinnerTime = dinnerTime;
    }

    public String getOpenCloseTimeText() {
        return openCloseTimeText;
    }

    public void setOpenCloseTimeText(String openCloseTimeText) {
        this.openCloseTimeText = openCloseTimeText;
    }

    public List<AdultMeal> getAdultMeals() {
        return adultMeals;
    }

    public void setAdultMeals(List<AdultMeal> adultMeals) {
        this.adultMeals = adultMeals;
    }

    public List<ChildMeals> getChildrenMeals() {
        return childrenMeals;
    }

    public void setChildrenMeals(List<ChildMeals> childrenMeals) {
        this.childrenMeals = childrenMeals;
    }

    public List<BreakfastItem> getBreakfastItems() {
        return breakfastItems;
    }

    public void setBreakfastItems(List<BreakfastItem> breakfastItems) {
        this.breakfastItems = breakfastItems;
    }

    public List<LunchItem> getLunchItems() {
        return lunchItems;
    }

    public void setLunchItems(List<LunchItem> lunchItems) {
        this.lunchItems = lunchItems;
    }

    public List<DinnerItem> getDinnerItems() {
        return dinnerItems;
    }

    public void setDinnerItems(List<DinnerItem> dinnerItems) {
        this.dinnerItems = dinnerItems;
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
        dest.writeValue(breakfastTime);
        dest.writeValue(lunchTime);
        dest.writeValue(dinnerTime);
        dest.writeValue(openCloseTimeText);
        dest.writeList(adultMeals);
        dest.writeList(childrenMeals);
        dest.writeList(breakfastItems);
        dest.writeList(lunchItems);
        dest.writeList(dinnerItems);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(imageUrl);
        dest.writeValue(text1);
        dest.writeValue(text2);
        dest.writeValue(text3);
        dest.writeValue(children);
    }

    public int describeContents() {
        return  0;
    }

}
