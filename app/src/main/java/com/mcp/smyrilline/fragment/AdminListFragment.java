package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.ChatUserListAdapter;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.model.messaging.SearchQuery;
import com.mcp.smyrilline.model.messaging.ServerMessageItem;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.InternalStorage;
import com.mcp.smyrilline.util.McpApplication;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by raqib on 9/17/17.
 */

public class AdminListFragment extends Fragment {

    private ChatUserListAdapter adminListAdapter;
    private RecyclerView recyclerViewAdmin;
    private ArrayList<ChatUserServerModel> adminList = new ArrayList<>();
    private TextView tvNoSuchUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_list, container, false);

        // Admin list
        adminListAdapter = new ChatUserListAdapter(getActivity(), adminList);
        recyclerViewAdmin = (RecyclerView) rootView.findViewById(R.id.recyclerViewAdmin);
        recyclerViewAdmin.setAdapter(adminListAdapter);
        GridLayoutManager layoutManagerOnline = new GridLayoutManager(getActivity(), 3);
        recyclerViewAdmin.setLayoutManager(layoutManagerOnline);

        // no user text
        tvNoSuchUser = (TextView) rootView.findViewById(R.id.tvLoading);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        adminList.clear();
        adminList.add(new ChatUserServerModel("3", 3, "user", "good boy",
            null, "male", "bangladesh", System.currentTimeMillis(),
            System.currentTimeMillis() - 3000, AppUtils.USER_ONLINE, 1, 0));

        // Offline recent
        adminList.add(new ChatUserServerModel("4", 4, "user", "good boy",
            null, "male", "bangladesh", System.currentTimeMillis(),
            System.currentTimeMillis() - 1000, AppUtils.USER_OFFLINE, 2, 0));
        adminList.add(new ChatUserServerModel("5", 5, "user", "good boy",
            null, "male", "bangladesh", System.currentTimeMillis(), System.currentTimeMillis(),
            AppUtils.USER_OFFLINE, 3, 0));

        adminListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        McpApplication.registerWithEventBus(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // Get search text from main fragment
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchQuery(SearchQuery searchQuery) {
        String query = searchQuery.getQuery();
        if (!query.isEmpty()) {      // text is entered
            recyclerViewAdmin.setVisibility(View.GONE);
            // show search list
            adminListAdapter.setNoSuchUserTextView(tvNoSuchUser);
            adminListAdapter.filter(query);
        } else {    // show online & recent, hide search list
            recyclerViewAdmin.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserListReceived(ArrayList<ChatUserServerModel> userList) {
        this.adminList = userList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageReceived(ServerMessageItem serverMessageItem) {
        // update new message count, and sendtime of respective user
        for (ChatUserServerModel chatUser : adminList) {
            if (chatUser.getDeviceId().equals(serverMessageItem.getSenderDeviceId())) {
                chatUser.setNewMessageCount(chatUser.getNewMessageCount() + 1);
                chatUser.setLastCommunicationTimeMillis(serverMessageItem.getSendTime());
                chatUser.setLastSeen(serverMessageItem.getSendTime());

                // refresh list
                adminListAdapter.notifyDataSetChanged();

                // save updated user list
                InternalStorage
                    .writeObject(McpApplication.instance().context(), AppUtils.KEY_SAVED_ADMIN_LIST,
                        adminList);
                break;
            }
        }
    }
}
