package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 10/6/17.
 */

public class CommunicatingClients {

    String senderId;    // remote id
    String receiverId;  // local client

    public CommunicatingClients(String remoteUserId, String localUserId) {
        this.senderId = remoteUserId;
        this.receiverId = localUserId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
