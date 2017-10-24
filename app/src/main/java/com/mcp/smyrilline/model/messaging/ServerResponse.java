package com.mcp.smyrilline.model.messaging;

import com.mcp.smyrilline.util.AppUtils.RequestStatus;
import com.mcp.smyrilline.util.AppUtils.RequestType;

/**
 * Created by raqib on 10/6/17.
 */

public class ServerResponse {

    public RequestType requestType;
    public RequestStatus requestStatus;

    public ServerResponse(RequestType requestType,
        RequestStatus requestStatus) {
        this.requestType = requestType;
        this.requestStatus = requestStatus;
    }
}
