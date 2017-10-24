package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.ChatMessageAdapter;
import com.mcp.smyrilline.model.messaging.ChatListItem;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.model.messaging.LocalMessageStatus;
import com.mcp.smyrilline.model.messaging.MessagePage;
import com.mcp.smyrilline.model.messaging.ServerMessageItem;
import com.mcp.smyrilline.model.messaging.ServerMessageListEvent;
import com.mcp.smyrilline.model.messaging.ServerResponse;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.AppUtils.RequestType;
import com.mcp.smyrilline.util.McpApplication;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by raqib on 9/12/17.
 */

public class RealtimeChatFragment extends Fragment {

    public static int pageNo = 0;
    private static ChatUserServerModel currentRemoteUser;
    private static boolean isStarted = false;
    private static boolean isVisible = false;
    private static boolean isLoadingFinished = false;
    private ArrayList<ChatListItem> messageList;
    private ChatMessageAdapter messageAdapter;
    private RecyclerView recyclerViewMessage;
    private ImageButton sendButton;
    private EditText messageEditText;
    private int newMessageCount = 0;
    private LinearLayout loadingLayout;
    private ProgressBar loadingProgressView;
    private TextView textViewLoading;
    private Button btnRetryLoad;
    private ImageView imgLoadingError;

    public static ChatUserServerModel getCurrentRemoteUser() {
        return currentRemoteUser;
    }

    public static boolean isFragmentShowing() {
        return isStarted/* && isVisible*/;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_realtime_messaging, container, false);

        // messaging_toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");

        // get the receiver user
        currentRemoteUser = getArguments()
            .getParcelable(AppUtils.CHAT_REMOTE_USER);

        // load user pic
        CircleImageView remoteUserPic = (CircleImageView) rootView.findViewById(R.id.remoteUserPic);
        Picasso.with(getActivity()).load(currentRemoteUser.getImageUrl())
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user).into(remoteUserPic);

        // messaging_toolbar title text
        TextView toolbarTitle = (TextView) rootView.findViewById(R.id.chatToolbarTitle);
        toolbarTitle.setText(getString(R.string.chat_with_user, currentRemoteUser.getName()));

        // init views
        recyclerViewMessage = (RecyclerView) rootView.findViewById(R.id.recyclerViewMessage);
        messageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hide loading layout
                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendButton = (ImageButton) rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage();
            }
        });

        // list view stack from bottom
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(
            getContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessage.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMessage.setLayoutManager(layoutManager);

        messageList = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(getContext(), messageList);
        recyclerViewMessage.setAdapter(messageAdapter);

        // loading views
        loadingLayout = (LinearLayout) rootView.findViewById(R.id.loadingLayout);
        loadingProgressView = (ProgressBar) rootView.findViewById(R.id.loadingProgressView);
        imgLoadingError = (ImageView) rootView.findViewById(R.id.imgLoadingError);
        textViewLoading = (TextView) rootView.findViewById(R.id.tvLoading);
        btnRetryLoad = (Button) rootView.findViewById(R.id.retryLoad);
        btnRetryLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForMessageList();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;

        // reset new count to zero (shown in list and menu)
        AppUtils.resetNewCountOfUser(currentRemoteUser.getDeviceId());

        McpApplication.registerWithEventBus(this);

        requestForMessageList();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        isStarted = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
    }

    private void requestForMessageList() {
        // show progress
        loadingLayout.setVisibility(View.VISIBLE);
        loadingProgressView.setVisibility(View.VISIBLE);
        imgLoadingError.setVisibility(View.GONE);
        textViewLoading.setVisibility(View.VISIBLE);
        textViewLoading.setText(getString(R.string.loading));
        btnRetryLoad.setVisibility(View.GONE);

        // Disable input text & post event for loading messages to signalrservice
        AppUtils.disableInputText(messageEditText, ContextCompat
            .getDrawable(getActivity(), R.drawable.bg_light_grey_rounded_primary_outline));
        EventBus.getDefault()
            .post(new MessagePage(currentRemoteUser.getDeviceId(), AppUtils.getDeviceId(),
                pageNo, AppUtils.MESSAGE_LOAD_ITEM_COUNT, newMessageCount));
    }

    private void sendTextMessage() {
        String message = messageEditText.getText().toString();
        if (!message.trim().isEmpty()) {

            ChatListItem chatListItem = new ChatListItem(
                message,
                AppUtils.getDeviceId(),
                currentRemoteUser.getDeviceId(),
                System.currentTimeMillis(),
                true, McpApplication.instance().sharedPref()
                .getString(AppUtils.PREF_MY_IMAGE_URL, null),
                UUID.randomUUID());

            // update UI
            messageAdapter.add(chatListItem);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewMessage
                .getLayoutManager();
            layoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
//            recyclerViewMessage.scrollToPosition(messageAdapter.getItemCount()-1);
            //clear edit text
            messageEditText.getText().clear();

            // send message to server
            EventBus.getDefault().post(chatListItem);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageReceived(ServerMessageItem serverMessageItem) {
        loadingLayout.setVisibility(View.GONE);

        // Check if the message is from currently messaging user
        if (serverMessageItem.getSenderDeviceId().trim().equals(currentRemoteUser.getDeviceId())) {
            this.messageList.add(new ChatListItem(
                serverMessageItem.getMessage(),
                serverMessageItem.getSenderDeviceId(),
                serverMessageItem.getReceiverDeviceId(),
                serverMessageItem.getSendTime(),
                false, currentRemoteUser.getImageUrl(),
                UUID.randomUUID()));
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewMessage
                .getLayoutManager();
            layoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerConnectionState(ServerResponse serverResponse) {
        switch (serverResponse.requestStatus) {
            case FAIL:
                // check request type
                // if request for message list, then
                if (serverResponse.requestType == RequestType.REQUEST_MESSAGE_LIST
                    || serverResponse.requestType == RequestType.SERVER_CONNECT) {
                    Log.e(AppUtils.TAG, "RealtimeChatFragment: serverResponse.status-"
                        + serverResponse.requestStatus
                        + " serverResponse.requestType-" + serverResponse.requestType);
                    showNoDataWithRetry(getString(R.string.could_not_connect_for_messages));
                }
                break;
            case SUCCESS:
                if (serverResponse.requestType == RequestType.REQUEST_MESSAGE_LIST
                    || serverResponse.requestType == RequestType.SERVER_CONNECT) {
                    Log.e(AppUtils.TAG, "RealtimeChatFragment: serverResponse.status-"
                        + serverResponse.requestStatus
                        + "serverResponse.requestType-" + serverResponse.requestType);
                    // hide loading layout
                    loadingLayout.setVisibility(View.GONE);
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageListReceived(ServerMessageListEvent messageListEvent) {
        Log.i(AppUtils.TAG,
            "RealtimeChatFragment message list received ->\n" + messageListEvent.serverMessageList);

        // If there are no previous messages, hide loading and retry
        if (messageListEvent.serverMessageList.isEmpty()) {
            loadingLayout.setVisibility(View.VISIBLE);
            textViewLoading.setVisibility(View.VISIBLE);
            textViewLoading.setText(getString(R.string.no_messages_start_typing));
            loadingProgressView.setVisibility(View.GONE);
            imgLoadingError.setVisibility(View.GONE);
            btnRetryLoad.setVisibility(View.GONE);
            // set empty list and refresh
            messageAdapter.setMessageList(messageListEvent.serverMessageList);
            messageAdapter.notifyDataSetChanged();
        }
        // check if message list is from the current remote user
        else /*if (currentRemoteUser.getDeviceId().equals(messageList.get(0).getSenderDeviceId()))*/ {
            messageAdapter.setMessageList(messageList);
            messageAdapter.notifyItemRangeInserted(0, messageList.size());
            // scroll to bottom
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewMessage
                .getLayoutManager();
            layoutManager.scrollToPositionWithOffset(messageAdapter.getItemCount() - 1, 0);

            // increment pageNo
            pageNo++;

            // hide loading progress
            loadingLayout.setVisibility(View.GONE);
        }

        // re-enable the edittext
        AppUtils.enableInputText(messageEditText, ContextCompat
            .getDrawable(getActivity(), R.drawable.bg_white_rounded_primary_outline));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageSendStatus(LocalMessageStatus localMessageStatus) {
        // update the msg item sending status
        for (ChatListItem chatListItem : messageList) {
            if (chatListItem.getUniqueId().equals(localMessageStatus.getUniqueId())) {
                chatListItem.msgSendingStatus = localMessageStatus.getRequestStatus();
                messageAdapter.setMessageList(messageList);
                messageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showNoDataWithRetry(String noDataText) {
        // show no data with retry
        loadingLayout.setVisibility(View.VISIBLE);
        loadingProgressView.setVisibility(View.GONE);
        imgLoadingError.setVisibility(View.VISIBLE);
        textViewLoading.setVisibility(View.VISIBLE);
        textViewLoading.setText(noDataText);
        btnRetryLoad.setVisibility(View.VISIBLE);
    }

    class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }
}

