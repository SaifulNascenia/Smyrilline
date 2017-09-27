package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.BulletinAdapter;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.model.Bulletin;
import com.mcp.smyrilline.util.AppUtils;
import java.util.ArrayList;

/**
 * Created by raqib on 12/29/15.
 */
public class InboxFragment extends Fragment implements BulletinListener {

    private static final String DUMMY_BULLETIN_COUNT = "dummy_bulletin_count";
    private static int dummyBulletinCount = 0;
    private Context mContext;
    private ArrayList<Bulletin> mBulletinList;
    private RecyclerView mRecyclerView;
    private BulletinAdapter mAdapter;
    private SharedPreferences mSharedPref;
    private TextView tvNothingText;
    private DrawerActivity mDrawerActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mContext = getActivity();

        mDrawerActivity = (DrawerActivity) mContext;
        // set the toolbar as actionbar
        ((DrawerActivity) getActivity())
                .setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(R.string.inbox);                // set title
        mDrawerActivity.invalidateOptionsMenu();                  // Refresh toolbar options
        mDrawerActivity.setBulletinListener(this);                // Set bulletin listener

        mSharedPref = PreferenceManager
                .getDefaultSharedPreferences(mContext.getApplicationContext());

        tvNothingText = (TextView) rootView.findViewById(R.id.tvBulletinsNothingText);
        mBulletinList = new ArrayList<>();

        // init list view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listViewBulletins);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // Set adapter
        mAdapter = new BulletinAdapter(mBulletinList, tvNothingText);
        mRecyclerView.setAdapter(mAdapter);

/**     SWIPE TO DELETE & UNDO  [START] **/
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(mRecyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                // Remove item and update counter
                                try {

                                    // we'll update the counter after remove is complete
                                    // so we use additional variable
                                    boolean updateCounter = false;
                                    if (!mBulletinList.get(position).isSeen())
                                        updateCounter = true;

                                    mAdapter.remove(position);

//                                    if (updateCounter)
//                                        mDrawerActivity.updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, -1);

                                } catch (IndexOutOfBoundsException e) {
                                    // Happens when swiped too fast, too random
                                    // ignore it
                                }
                            }
                        });

        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.addOnScrollListener(
                (RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new FixSwipeableItemClickListener(mContext,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.imgDeleted) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.imgUndo) {
                            touchListener.undoPendingDismiss();
                        } else {
                            try {
                                Log.i(AppUtils.TAG,
                                        String.format("Bulletin @%d touched", position));
                                Bulletin bulletin = mBulletinList.get(position);

                                Bundle bulletinData = new Bundle();
                                bulletinData.putString("title", bulletin.getTitle());
                                bulletinData.putString("content", bulletin.getContent());
                                bulletinData.putString("date", bulletin.getDate());
                                bulletinData.putString("imageURL", bulletin.getImageUrl());

                                BulletinDetailFragment fragment = new BulletinDetailFragment();
                                fragment.setArguments(bulletinData);

                                mDrawerActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_right,
                                                R.anim.slide_out_left)
                                        .replace(R.id.content_frame, fragment)
                                        .commit();

                                if (!bulletin.isSeen()) {
                                    // Set seen true & save
                                    bulletin.setSeen(true);
                                    // Update the unread label in nav drawer
//                                    mDrawerActivity.updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, -1);
                                }

                                // Save the list
                                AppUtils.saveListInSharedPref(mBulletinList,
                                        AppUtils.PREF_BULLETIN_LIST);

                            } catch (IndexOutOfBoundsException ex) {
                                // If tapped quickly when dismiss animation is going on
                                // i.e. the item doesn't exist
                            }
                        }
                    }
                }));

/**     SWIPE TO DELETE & UNDO  [END] **/

        return rootView;
    }

    @Override
    public void onBulletinReceived(Bulletin newBulletin) {
        // Add bulletin to list and save
        mAdapter.add(0, newBulletin);

        // Scroll to first position
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                .getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void onBulletinListReceived(ArrayList<Bulletin> newBulletinList) {
        // Add bulletin to list and save
        mAdapter.addAll(0, newBulletinList);

        // Scroll to first position
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                .getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update list onResume, to handle case:
        // App foreground > lock screen > bulletin sent
        Log.i(AppUtils.TAG, "InboxFragment: onResume -> getting BulletinList");
        mBulletinList = getSavedBulletinList();
        AppUtils.printBulletinList(mBulletinList);

        // dummy bulletin for test [REMOVE for release]
        dummyBulletinCount = mSharedPref.getInt(DUMMY_BULLETIN_COUNT, 0);
        if (dummyBulletinCount == 0) {
            mBulletinList
                    .add(new Bulletin(0, "Welcome to Smyril Line!", getString(R.string.lorem_ipsum),
                            "Aug 02, 2017 04:15 PM",
                            "http://i.ytimg.com/vi/1Xu_qyeiilU/maxresdefault.jpg", false));
            mSharedPref.edit().putInt(DUMMY_BULLETIN_COUNT, ++dummyBulletinCount).apply();
        }

        mAdapter.setBulletinList(mBulletinList);
        mAdapter.notifyDataSetChanged();

        if (mAdapter.getItemCount() == 0)
            tvNothingText.setVisibility(View.VISIBLE);
        else
            tvNothingText.setVisibility(View.GONE);
    }

    public ArrayList<Bulletin> getSavedBulletinList() {
        ArrayList<Bulletin> savedBulletinList = null;
        Gson gson = new Gson();

        // Init list - check saved if exists
        String savedBulletinListAsString = mSharedPref
                .getString(AppUtils.PREF_BULLETIN_LIST, AppUtils.PREF_NO_ENTRY);
        if (savedBulletinListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
            savedBulletinList = new ArrayList<>();
        } else {
            savedBulletinList = gson
                    .fromJson(savedBulletinListAsString, new TypeToken<ArrayList<Bulletin>>() {
                    }.getType());
        }
        return savedBulletinList;
    }

    /**
     * Use this instead of SwipeableItemClickListener directly
     * Issue: java.lang.AbstractMethodError for RecyclerView version above 21.0.3
     * https://github.com/hudomju/android-swipe-to-dismiss-undo/issues/9
     */
    private static class FixSwipeableItemClickListener extends SwipeableItemClickListener {

        public FixSwipeableItemClickListener(Context context, OnItemClickListener listener) {
            super(context, listener);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
