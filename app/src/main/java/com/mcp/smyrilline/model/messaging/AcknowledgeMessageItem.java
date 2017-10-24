package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 10/2/17.
 */

public class AcknowledgeMessageItem {

    private final int messageId;

    public AcknowledgeMessageItem(int messageId) {
        this.messageId = messageId;
    }
}
