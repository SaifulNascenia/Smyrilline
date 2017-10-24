package com.mcp.smyrilline.model.messaging;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.io.Serializable;

/**
 * Created by raqib on 9/11/17.
 */

public class ChatUserServerModel implements Comparable<ChatUserServerModel>, Parcelable,
    Serializable {


    public static final Creator<ChatUserServerModel> CREATOR = new Creator<ChatUserServerModel>() {
        @Override
        public ChatUserServerModel createFromParcel(Parcel source) {
            return new ChatUserServerModel(source);
        }

        @Override
        public ChatUserServerModel[] newArray(int size) {
            return new ChatUserServerModel[size];
        }
    };
    String deviceId;
    int bookingNo;
    String name;
    String description;
    String imageUrl;
    String gender;
    String country;
    long lastSeen;
    long lastCommunicationTimeMillis;   // -1 means no communication
    int status;  // online -> 0 offline -> 1
    int visibility;
    int newMessageCount;

    public ChatUserServerModel(String deviceId,
        int bookingNo,
        String name,
        String description,
        String imageUrl,
        String gender,
        String country,
        long lastSeen,
        long lastCommunicationTimeMillis,
        int status,
        int visibility,
        int newMessageCount) {
        this.deviceId = deviceId;
        this.bookingNo = bookingNo;
        this.name = name;
        this.description = description;
        this.country = country;
        this.lastSeen = lastSeen;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.status = status;
        this.lastCommunicationTimeMillis = lastCommunicationTimeMillis;
        this.newMessageCount = newMessageCount;
    }

    protected ChatUserServerModel(Parcel in) {
        this.deviceId = in.readString();
        this.bookingNo = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.imageUrl = in.readString();
        this.gender = in.readString();
        this.country = in.readString();
        this.lastSeen = in.readLong();
        this.lastCommunicationTimeMillis = in.readLong();
        this.status = in.readInt();
        this.visibility = in.readInt();
        this.newMessageCount = in.readInt();
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getLastCommunicationTimeMillis() {
        return lastCommunicationTimeMillis;
    }

    public void setLastCommunicationTimeMillis(long lastCommunicationTimeMillis) {
        this.lastCommunicationTimeMillis = lastCommunicationTimeMillis;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(int newMessageCount) {
        this.newMessageCount = newMessageCount;
    }

    @Override
    public int compareTo(@NonNull ChatUserServerModel otherUser) {
        // descending order
        if (this.lastCommunicationTimeMillis == otherUser.lastCommunicationTimeMillis)
            return 0;
        else if (otherUser.lastCommunicationTimeMillis > this.lastCommunicationTimeMillis)
            return 1;
        else
            return -1;
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
        dest.writeString(this.description);
        dest.writeString(this.imageUrl);
        dest.writeString(this.gender);
        dest.writeString(this.country);
        dest.writeLong(this.lastSeen);
        dest.writeLong(this.lastCommunicationTimeMillis);
        dest.writeInt(this.status);
        dest.writeInt(this.visibility);
        dest.writeInt(this.newMessageCount);
    }
}
