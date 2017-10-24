package com.mcp.smyrilline.model.messaging;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatUserClientModel implements Parcelable {

    public static final Creator<ChatUserClientModel> CREATOR = new Creator<ChatUserClientModel>() {
        @Override
        public ChatUserClientModel createFromParcel(Parcel source) {
            return new ChatUserClientModel(source);
        }

        @Override
        public ChatUserClientModel[] newArray(int size) {
            return new ChatUserClientModel[size];
        }
    };
    private String deviceId;
    private int bookingNo;
    private String name;
    private String imageUrl;
    private String description;
    private String country;
    private String gender;
    private int status; // 1 - Online, 0 - Offline
    private int visibility; // 1-Visible to Booking, 2-Public, 3-Invisible

    public ChatUserClientModel(String deviceId, int bookingNo, String name, String imageUrl,
        String description, String country, String gender, int status, int visibility) {
        this.deviceId = deviceId;
        this.bookingNo = bookingNo;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.country = country;
        this.gender = gender;
        this.status = status;
        this.visibility = visibility;
    }

    protected ChatUserClientModel(Parcel in) {
        this.deviceId = in.readString();
        this.bookingNo = in.readInt();
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.description = in.readString();
        this.country = in.readString();
        this.gender = in.readString();
        this.status = in.readInt();
        this.visibility = in.readInt();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "ChatUserClientModel{" +
            "deviceId='" + deviceId + '\'' +
            ", bookingNo=" + bookingNo +
            ", name='" + name + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", description='" + description + '\'' +
            ", country='" + country + '\'' +
            ", gender='" + gender + '\'' +
            ", status=" + status +
            ", visibility=" + visibility +
            '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceId);
        dest.writeInt(this.bookingNo);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeString(this.description);
        dest.writeString(this.country);
        dest.writeString(this.gender);
        dest.writeInt(this.status);
        dest.writeInt(this.visibility);
    }
}