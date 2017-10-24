package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.fragment.RealtimeChatFragment;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by raqib on 9/12/17.
 */

public class ChatUserListAdapter extends
    RecyclerView.Adapter<ChatUserListAdapter.ChatUserViewHolder> {

    private final Context context;
    private ArrayList<ChatUserServerModel> userList;
    private ArrayList<ChatUserServerModel> tempSearchList; // temporary list to help search
    private TextView noSuchUserTextView;

    public ChatUserListAdapter(Context context, ArrayList<ChatUserServerModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_chat_user, parent, false);
        return new ChatUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatUserViewHolder holder, int position) {
        final ChatUserServerModel user = userList.get(position);

        // pic
        Picasso.with(context).load(user.getImageUrl()).placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .resize((int) context.getResources().getDimension(R.dimen.chat_user_list_pic_size),
                (int) context.getResources().getDimension(R.dimen.chat_user_list_pic_size))
            .into(holder.imgProPic);

        // name
        holder.tvName.setText(user.getName());

        // online/offline
        if (user.getStatus() == AppUtils.USER_ONLINE)
            holder.imgOnlineIndicator.setVisibility(View.VISIBLE);
        else
            holder.imgOnlineIndicator.setVisibility(View.INVISIBLE);

        // set new message count
        int newMessageCount = user.getNewMessageCount();
        AppUtils.updateCounterLabel(holder.tvNewMessageCount, newMessageCount,
            AppUtils.CHAT_USER_LIST_MAX_MESSAGE_COUNT);

        // handle click
        holder.rlUserItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppUtils.CHAT_REMOTE_USER, user);

                RealtimeChatFragment realtimeChatFragment = new RealtimeChatFragment();
                realtimeChatFragment.setArguments(bundle);

                ((DrawerActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, 0)
                    .replace(R.id.content_frame, realtimeChatFragment,
                        realtimeChatFragment.getClass().getSimpleName())
                    .addToBackStack(realtimeChatFragment.getClass().getSimpleName())
                    .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setNoSuchUserTextView(TextView noSuchUserTextView) {
        this.noSuchUserTextView = noSuchUserTextView;
    }

    /**
     * Search filter method searches character in user names
     * and updates list
     */
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.US).trim();
        userList.clear();
        if (TextUtils.isEmpty(charText)) {
            userList.addAll(tempSearchList);
        } else {
            for (ChatUserServerModel user : tempSearchList) {
                if (user.getName().toLowerCase(Locale.US)
                    .startsWith(charText)) {
                    userList.add(user);
                }
            }
        }
        refreshSearchedList();
    }

    /**
     * checks empty list and updates 'no data' textview
     */
    public void refreshSearchedList() {
        if (userList.size() == 0)
            noSuchUserTextView.setVisibility(View.VISIBLE);
        else
            noSuchUserTextView.setVisibility(View.GONE);

        notifyDataSetChanged();
    }

    /**
     * Init a temporary list with all users for searching only
     */
    public void initTempSearchList() {
        tempSearchList = new ArrayList<>();
        tempSearchList.addAll(userList);
    }

    class ChatUserViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlUserItem;
        private TextView tvName;
        private CircleImageView imgProPic;
        private ImageView imgOnlineIndicator;
        private TextView tvNewMessageCount;

        private ChatUserViewHolder(View itemView) {
            super(itemView);
            rlUserItem = (RelativeLayout) itemView.findViewById(R.id.rlUserItem);
            tvName = (TextView) itemView.findViewById(R.id.editName);
            imgProPic = (CircleImageView) itemView.findViewById(R.id.imgProPic);
            imgOnlineIndicator = (ImageView) itemView.findViewById(R.id.imgOnlineIndicator);
            tvNewMessageCount = (TextView) itemView.findViewById(R.id.tvNewMessageCount);
        }
    }
}
