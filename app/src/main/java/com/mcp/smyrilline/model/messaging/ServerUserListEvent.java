package com.mcp.smyrilline.model.messaging;

import java.util.ArrayList;

/**
 * Created by raqib on 10/20/17.
 */

public class ServerUserListEvent {

    public ArrayList<ChatUserServerModel> serverUserList;

    public ServerUserListEvent(
        ArrayList<ChatUserServerModel> serverUserList) {
        this.serverUserList = serverUserList;
    }
}
