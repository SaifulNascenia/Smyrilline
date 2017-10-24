package com.mcp.smyrilline.model.messaging;

import com.mcp.smyrilline.util.AppUtils;

/**
 * Created by raqib on 9/20/17.
 */

public class ClientStatus {

    private int status = AppUtils.USER_OFFLINE; // default is Offline

    public ClientStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
