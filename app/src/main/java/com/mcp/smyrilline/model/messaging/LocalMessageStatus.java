package com.mcp.smyrilline.model.messaging;

import com.mcp.smyrilline.util.AppUtils.MsgSendingStatus;
import java.util.UUID;

/**
 * Created by raqib on 10/16/17.
 */

public class LocalMessageStatus {

    private UUID uniqueId;
    private MsgSendingStatus requestStatus;

    public LocalMessageStatus(UUID uniqueId, MsgSendingStatus requestStatus) {
        this.uniqueId = uniqueId;
        this.requestStatus = requestStatus;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public MsgSendingStatus getRequestStatus() {
        return requestStatus;
    }
}
