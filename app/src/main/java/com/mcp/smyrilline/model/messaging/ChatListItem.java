package com.mcp.smyrilline.model.messaging;

import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.AppUtils.MsgSendingStatus;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by raqib on 9/20/17.
 */

public class ChatListItem implements Serializable {

    public MsgSendingStatus msgSendingStatus = MsgSendingStatus.SENDING;
    String message;
    String senderDeviceId;
    String receiverDeviceId;
    long sendTime;
    boolean fromLocalClient; // did I send the message
    String senderImageUrl;
    // unique ID for local use (not server side), e.g. to update send status of specific message
    private UUID uniqueId;

    public ChatListItem(String message, String senderDeviceId, String receiverDeviceId,
        long sendTime, boolean isMine, String senderImageUrl, UUID uniqueId) {
        this.message = message;
        this.senderDeviceId = senderDeviceId;
        this.receiverDeviceId = receiverDeviceId;
        // convert long millis to current time zone in format\

//        this.sendTime = AppUtils.convertMillisToLocalTimeZoneInFormat(sendTime,
//                AppUtils.TIME_FORMAT_CHAT_MESSAGE);
        this.sendTime = sendTime;
        this.fromLocalClient = isMine;
        this.senderImageUrl = senderImageUrl;
        this.uniqueId = uniqueId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderDeviceId() {
        return senderDeviceId;
    }

    public String getReceiverDeviceId() {
        return receiverDeviceId;
    }

    public String getSendTime() {
        return AppUtils.convertMillisToFormat(sendTime, AppUtils.TIME_FORMAT_CHAT_MESSAGE);
    }

    public boolean isFromLocalClient() {
        return fromLocalClient;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof ChatListItem)
            if (this.uniqueId.equals(((ChatListItem) object).getUniqueId()))
                return true;
        return false;
    }
}
