package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.messaging.ChatListItem;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

/**
 * Created by raqib on 9/20/17.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private ArrayList<ChatListItem> messageList;

    public ChatMessageAdapter(Context context, ArrayList<ChatListItem> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_sent, parent, false);
            return new SentMessageViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message_received, parent, false);
            return new ReceivedMessageViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatListItem messageItem = messageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder).bind(messageItem);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind(messageItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isFromLocalClient())
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    public void add(ChatListItem chatListMessage) {
        messageList.add(chatListMessage);
    }

    public void setMessageList(ArrayList<ChatListItem> messageList) {
        this.messageList = messageList;
    }

    // View holder for receiving messages
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfilePic;
        private TextView tvMessage;
        private TextView tvMessageTime;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            imgProfilePic = (CircleImageView) itemView.findViewById(R.id.imgProfilePic);
            tvMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            tvMessageTime = (TextView) itemView.findViewById(R.id.textViewMessageTime);
        }

        void bind(ChatListItem messageItem) {
            // load profile pic
            Picasso.with(context).load(messageItem.getSenderImageUrl())
                .placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                .resize((int) context.getResources()
                        .getDimension(R.dimen.chat_messaging_pic_size),
                    (int) context.getResources()
                        .getDimension(R.dimen.chat_messaging_pic_size))
                .into(imgProfilePic);

            // load message text
            tvMessage.setText(messageItem.getMessage());

            // load message time
            tvMessageTime.setText(messageItem.getSendTime());
        }
    }

    // View holder for sent messages
    class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfilePic;
        private TextView tvMessage;
        private TextView tvMessageTime;
        private ImageView imgMessageSendingStatus;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            imgProfilePic = (CircleImageView) itemView.findViewById(R.id.imgProfilePic);
            tvMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            tvMessageTime = (TextView) itemView.findViewById(R.id.textViewMessageTime);
            imgMessageSendingStatus = (ImageView) itemView
                .findViewById(R.id.imgMessageSendingStatus);
        }

        void bind(ChatListItem messageItem) {
            // load profile pic
            Picasso.with(context).load(messageItem.getSenderImageUrl())
                .placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                .resize((int) context.getResources()
                        .getDimension(R.dimen.chat_messaging_pic_size),
                    (int) context.getResources()
                        .getDimension(R.dimen.chat_messaging_pic_size))
                .into(imgProfilePic);

            // load message text
            tvMessage.setText(messageItem.getMessage());

            // load message time
            tvMessageTime.setText(messageItem.getSendTime());

            // load status image
            int statusImageId = R.drawable.ic_message_sending;
            if (messageItem.msgSendingStatus
                == null) {     // when binding message list received from server
                imgMessageSendingStatus.setVisibility(View.GONE);
            } else {
                imgMessageSendingStatus.setVisibility(View.VISIBLE);    // when sending message
                switch (messageItem.msgSendingStatus) {
                    case SENDING:
                        statusImageId = R.drawable.ic_message_sending;
                        break;
                    case SENT:
                        statusImageId = R.drawable.ic_message_sending_failed;
                        break;
                    case FAILED:
                        statusImageId = R.drawable.ic_message_sending_success;
                        break;
                }
            }

            // load status image
            Picasso.with(context).load(statusImageId)
                .into(imgMessageSendingStatus);
        }
    }
}
