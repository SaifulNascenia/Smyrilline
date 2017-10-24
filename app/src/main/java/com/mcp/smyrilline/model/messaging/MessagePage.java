package com.mcp.smyrilline.model.messaging;

/**
 * Created by raqib on 10/6/17.
 */

public class MessagePage {

    private String senderId;
    private String receiverId;
    private int pageNo;
    private int itemCount;
    private int newMessageAdded;

    public MessagePage(String senderId, String receiverId, int pageNo, int itemCount,
        int newMessageAdded) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.pageNo = pageNo;
        this.itemCount = itemCount;
        this.newMessageAdded = newMessageAdded;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getNewMessageAdded() {
        return newMessageAdded;
    }

    @Override
    public String toString() {
        return "MessagePage{" +
            "senderId='" + senderId + '\'' +
            ", receiverId='" + receiverId + '\'' +
            ", pageNo=" + pageNo +
            ", itemCount=" + itemCount +
            ", newMessageAdded=" + newMessageAdded +
            '}';
    }
}
