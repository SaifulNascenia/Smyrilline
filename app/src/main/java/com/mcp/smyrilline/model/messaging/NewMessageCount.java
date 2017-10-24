package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 10/2/17.
 */

public class NewMessageCount {

    public int newBulletinCount;
    public int newChatCount;

    public NewMessageCount(int newBulletinCount, int newChatCount) {
        this.newBulletinCount = newBulletinCount;
        this.newChatCount = newChatCount;
    }
}
