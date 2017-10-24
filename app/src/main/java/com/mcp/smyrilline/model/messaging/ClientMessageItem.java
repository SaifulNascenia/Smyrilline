package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 9/20/17.
 */

public class ClientMessageItem {

    public String senderDeviceId;
    private String receiverDeviceId;
    private String message;

    public ClientMessageItem(String senderDeviceId, String receiverDeviceId, String message) {
        this.senderDeviceId = senderDeviceId;
        this.receiverDeviceId = receiverDeviceId;
        this.message = message;
    }

    public String getSenderDeviceId() {
        return senderDeviceId;
    }

    public String getReceiverDeviceId() {
        return receiverDeviceId;
    }

    public String getMessage() {
        return message;
    }

}
