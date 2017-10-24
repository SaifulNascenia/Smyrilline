package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 9/20/17.
 */

public class ServerMessageItem {

    private int messageId;
    private String message;
    private String receiverDeviceId;
    private long sendTime;
    private ChatUserServerModel senderChatUserServerModel;

    public ServerMessageItem(int messageId,
        ChatUserServerModel senderChatUserServerModel, String receiverDeviceId, String message,
        long sendTime) {
        this.messageId = messageId;
        this.senderChatUserServerModel = senderChatUserServerModel;
        this.receiverDeviceId = receiverDeviceId;
        this.message = message;
        this.sendTime = sendTime;
    }

    public int getMessageId() {
        return messageId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiverDeviceId() {
        return receiverDeviceId;
    }

    public String getSenderDeviceId() {
        return senderChatUserServerModel.getDeviceId();
    }

    public String getSenderName() {
        return senderChatUserServerModel.getName();
    }

    public ChatUserServerModel getSenderChatUserServerModel() {
        return senderChatUserServerModel;
    }
}
