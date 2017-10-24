package com.mcp.smyrilline.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.ChatUserListAdapter;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.model.messaging.SearchQuery;
import com.mcp.smyrilline.model.messaging.ServerMessageItem;
import com.mcp.smyrilline.model.messaging.ServerResponse;
import com.mcp.smyrilline.model.messaging.ServerUserListEvent;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.AppUtils.RequestStatus;
import com.mcp.smyrilline.util.InternalStorage;
import com.mcp.smyrilline.util.McpApplication;
import java.util.ArrayList;
import java.util.Collections;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by raqib on 9/11/17.
 */

public class UserListFragment extends Fragment {

    private ArrayList<ChatUserServerModel> userList = new ArrayList<>();
    private ArrayList<ChatUserServerModel> userListRecent = new ArrayList<>();
    private ArrayList<ChatUserServerModel> userListOnline = new ArrayList<>();
    private ArrayList<ChatUserServerModel> userListSearch = new ArrayList<>();
    private RecyclerView recyclerViewRecent;
    private RecyclerView recyclerViewOnline;
    private RecyclerView recyclerViewSearch;
    private ChatUserListAdapter userListSearchAdapter;
    private ChatUserListAdapter userListOnlineAdapter;
    private ChatUserListAdapter userListRecentAdapter;
    private SearchView searchView;
    private TextView textViewLoading;
    private RelativeLayout rlRecentUsers;
    private RelativeLayout rlOnlineUsers;
    private LinearLayout loadingLayout;
    private Button btnRetryLoad;
    private ProgressBar loadingProgressView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);
        Log.d(AppUtils.TAG, "UserListFragment -> onCreateView()");

        rlRecentUsers = (RelativeLayout) rootView.findViewById(R.id.rlRecentUsers);
        rlOnlineUsers = (RelativeLayout) rootView.findViewById(R.id.rlOnlineUsers);

        // Recent
        userListRecentAdapter = new ChatUserListAdapter(getActivity(), userListRecent);
        recyclerViewRecent = (RecyclerView) rootView.findViewById(R.id.recyclerViewRecent);
        recyclerViewRecent.setAdapter(userListRecentAdapter);
        LinearLayoutManager layoutManagerRecent = new LinearLayoutManager(getActivity(),
            LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecent.setLayoutManager(layoutManagerRecent);

        // Online
        userListOnlineAdapter = new ChatUserListAdapter(getActivity(), userListOnline);
        recyclerViewOnline = (RecyclerView) rootView.findViewById(R.id.recyclerViewOnline);
        recyclerViewOnline.setAdapter(userListOnlineAdapter);
        GridLayoutManager layoutManagerOnline = new GridLayoutManager(getActivity(), 4);
        recyclerViewOnline.setLayoutManager(layoutManagerOnline);

        // Search
        userListSearchAdapter = new ChatUserListAdapter(getActivity(), userListSearch);
        recyclerViewSearch = (RecyclerView) rootView.findViewById(R.id.recyclerViewSearch);
        recyclerViewSearch.setAdapter(userListSearchAdapter);
        GridLayoutManager layoutManagerSearch = new GridLayoutManager(getActivity(), 4);
        recyclerViewSearch.setLayoutManager(layoutManagerSearch);

        // No connection view
        loadingLayout = (LinearLayout) rootView.findViewById(R.id.loadingLayout);
        loadingProgressView = (ProgressBar) rootView.findViewById(R.id.loadingProgressView);
        textViewLoading = (TextView) rootView.findViewById(R.id.tvLoading);
        btnRetryLoad = (Button) rootView.findViewById(R.id.retryLoad);
        btnRetryLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForUserList();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        McpApplication.registerWithEventBus(this);
/*

        // Load saved list from memory
        // because list is already saved after sending online status from DrawerActivity: onStart
        userList = (ArrayList<ChatUserServerModel>) InternalStorage
                .readObject(getActivity(), AppUtils.KEY_SAVED_USER_LIST);

        loadUserList(userList);
*/
        requestForUserList();


        /*
        userList.clear();

        // Online recent
        userList.add(new ChatUserServerModel("0", 0, "touhid", "good boy",
                "https://i.imgur.com/Svm7EGi.png", "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis(), AppUtils.USER_ONLINE, 1));
        userList.add(new ChatUserServerModel("1", 1, "syedur", "good boy",
                "https://i.imgur.com/NbkqsBT.png", "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis() - 1000, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("2", 2, "shaer", "good boy",
                "https://i.imgur.com/KR7YPhX.png", "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis() - 2000, AppUtils.USER_ONLINE, 3));
        userList.add(new ChatUserServerModel("3", 3, "raqib", "good boy",
                "https://i.imgur.com/LuDQxJK.png", "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis() - 3000, AppUtils.USER_ONLINE, 2));

        // Offline recent
        userList.add(new ChatUserServerModel("4", 4, "awlad", "good boy",
                "https://i.imgur.com/em94xuH.png", "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis() - 1000, AppUtils.USER_OFFLINE, 2));
        userList.add(new ChatUserServerModel("5", 5, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis(), AppUtils.USER_OFFLINE, 3));

        // Online but no communication
        userList.add(new ChatUserServerModel("6", 6, "nadim", "good boy",
                "https://i.imgur.com/51j2kcR.png", "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("7", 7, "mazhar", "good boy",
                "https://i.imgur.com/mYlFkhe.png", "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2 ));
        userList.add(new ChatUserServerModel("8", 8, "ishtiaq", "good boy",
                "https://i.imgur.com/ayzih77.png", "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("9", 9, "fuad", "good boy",
                "https://i.imgur.com/3OPh9Z3.png", "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("10", 10, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("11", 11, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2 ));
        userList.add(new ChatUserServerModel("12", 12, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("13", 13, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("14", 14, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("15", 15, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));
        userList.add(new ChatUserServerModel("16", 16, "user", "good boy",
                null, "male", "bangladesh", System.currentTimeMillis(), -1, AppUtils.USER_ONLINE, 2));


        refreshRecentAndOnlineUserList(userList);*/

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void requestForUserList() {
        // hide lists
        rlRecentUsers.setVisibility(View.GONE);
        rlOnlineUsers.setVisibility(View.GONE);

        // show progress
        loadingLayout.setVisibility(View.VISIBLE);
        loadingProgressView.setVisibility(View.VISIBLE);
        textViewLoading.setVisibility(View.VISIBLE);
        textViewLoading.setText(getString(R.string.loading));
        btnRetryLoad.setVisibility(View.GONE);

        // call register chat
        AppUtils.setClientOnlineStatus();
    }

    /**
     * filter recent users (online + offline) by sorting communication time
     * rest online users will go to online list
     */
    private void refreshRecentAndOnlineUserList(ArrayList<ChatUserServerModel> allUserList) {
        if (allUserList == null || allUserList.isEmpty()) {
            showNoDataWithRetry(getString(R.string.no_user_available));
        } else {
            // hide no data view
            loadingLayout.setVisibility(View.GONE);

            // clear
            userListRecent.clear();
            userListOnline.clear();
            userListSearch.clear();

            ArrayList<ChatUserServerModel> recentOnline = new ArrayList<>();
            ArrayList<ChatUserServerModel> recentOffline = new ArrayList<>();

            for (ChatUserServerModel user : allUserList) {
                if (user.getLastCommunicationTimeMillis() != -1)    // recent communication done
                {
                    if (user.getStatus() == AppUtils.USER_ONLINE)            // online
                        recentOnline.add(user);
                    else                                            // offline
                        recentOffline.add(user);
                } else if (user.getStatus()
                    == AppUtils.USER_ONLINE)         // unfamiliar user, but online
                    userListOnline.add(user);
            }

            // sort recent online and offline list based on communication time
            // and append to recent main list
            Collections.sort(recentOnline);
            Collections.sort(recentOffline);
            userListRecent.addAll(recentOnline);
            userListRecent.addAll(recentOffline);

            // refresh list views
            userListRecentAdapter.notifyDataSetChanged();
            userListOnlineAdapter.notifyDataSetChanged();

            // add all items to search list for searching
            userListSearch.addAll(userListRecent);
            userListSearch.addAll(userListOnline);
            userListSearchAdapter.initTempSearchList();
            userListSearchAdapter.notifyDataSetChanged();

            // check if list are empty, and hide accordingly
            if (userListRecent.isEmpty() && userListOnline.isEmpty()) {
                showNoDataWithRetry(getString(R.string.no_user_available));
            } else if (userListRecent.isEmpty()) {
                // hide recent
                rlRecentUsers.setVisibility(View.GONE);
                rlOnlineUsers.setVisibility(View.VISIBLE);
            } else if (userListOnline.isEmpty()) {
                // hide online
                rlRecentUsers.setVisibility(View.VISIBLE);
                rlOnlineUsers.setVisibility(View.GONE);
            } else {
                // show both
                rlRecentUsers.setVisibility(View.VISIBLE);
                rlOnlineUsers.setVisibility(View.VISIBLE);
            }
        }
    }

    // Get search text from main fragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQuery searchQuery) {
        String query = searchQuery.getQuery();
        if (!query.isEmpty()) {      // text is entered
            // hide recent and online list
            rlRecentUsers.setVisibility(View.GONE);
            rlRecentUsers.setVisibility(View.GONE);
            // show search list
            recyclerViewSearch.setVisibility(View.VISIBLE);
            userListSearchAdapter.setNoSuchUserTextView(textViewLoading);
            userListSearchAdapter.filter(query);
        } else {    // show online & recent, hide search list
            rlRecentUsers.setVisibility(View.VISIBLE);
            rlRecentUsers.setVisibility(View.VISIBLE);
            recyclerViewSearch.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserListReceivedSuccess(ServerUserListEvent userListEvent) {
        userList = userListEvent.serverUserList;
        refreshRecentAndOnlineUserList(userList);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserListReceivedFailed(ServerResponse serverResponse) {
        if (serverResponse.requestStatus == RequestStatus.FAIL) {
            switch (serverResponse.requestType) {
                case SERVER_CONNECT:
                case REQUEST_MESSAGE_LIST:
                    showNoDataWithRetry(getString(R.string.could_not_load_users));
                    break;
            }
        }
    }

    private void showNoDataWithRetry(String noDataText) {
        // hide lists
        rlRecentUsers.setVisibility(View.GONE);
        rlOnlineUsers.setVisibility(View.GONE);

        // show no data with retry
        loadingLayout.setVisibility(View.VISIBLE);
        loadingProgressView.setVisibility(View.GONE);
        textViewLoading.setVisibility(View.VISIBLE);
        textViewLoading.setText(noDataText);
        btnRetryLoad.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageReceived(ServerMessageItem serverMessageItem) {
        // update new message count, and sendtime of respective user
        for (ChatUserServerModel chatUser : userList) {
            if (chatUser.getDeviceId().equals(serverMessageItem.getSenderDeviceId())) {
                chatUser.setNewMessageCount(chatUser.getNewMessageCount() + 1);
                chatUser.setLastCommunicationTimeMillis(serverMessageItem.getSendTime());
                chatUser.setLastSeen(serverMessageItem.getSendTime());

                // refresh list
                refreshRecentAndOnlineUserList(userList);

                // save updated user list
                InternalStorage.writeObject(McpApplication.instance().context(),
                    AppUtils.KEY_SAVED_USER_LIST, userList);

                // save updated new message count
                SharedPreferences sharedPref = McpApplication.instance().sharedPref();
                int newChatMessageCount = sharedPref
                    .getInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT, 0);
                sharedPref.edit().putInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT,
                    ++newChatMessageCount).apply();

                break;
            }
        }
    }
}