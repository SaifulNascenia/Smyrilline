package com.mcp.smyrilline.model.messaging;

import java.util.ArrayList;

/**
 * Created by raqib on 10/20/17.
 */

public class ServerMessageListEvent {

    public ArrayList<ChatListItem> serverMessageList;

    public ServerMessageListEvent(
        ArrayList<ChatListItem> serverMessageList) {
        this.serverMessageList = serverMessageList;
    }
}
