package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;
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
    private DrawerActivity mMainActivity;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mContext = getActivity();

        mMainActivity = (DrawerActivity) mContext;
//        // set the messaging_toolbar as actionbar
//        ((DrawerActivity)getActivity()).setToolbarAndToggle((Toolbar)mRootView.findViewById(R.id.messaging_toolbar));
//        getActivity().setTitle(R.string.inbox);                // set title
//        mMainActivity.invalidateOptionsMenu();                  // Refresh messaging_toolbar options
        mMainActivity.setBulletinListener(this);                // Set bulletin listener

        mSharedPref = PreferenceManager
            .getDefaultSharedPreferences(mContext.getApplicationContext());

        tvNothingText = (TextView) mRootView.findViewById(R.id.tvBulletinsNothingText);
        mBulletinList = new ArrayList<>();

        // init list view
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.listViewBulletins);
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
//                                        mMainActivity.updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, -1);

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
                                BulletinDetailFragment bulletinDetailDialogFragment =
                                    BulletinDetailFragment.newInstance(mBulletinList.get(position));
                                bulletinDetailDialogFragment.show(getFragmentManager(),
                                    bulletinDetailDialogFragment.getClass().getSimpleName());
//                                showDetailPopupAndSetSeen(mBulletinList.get(position));
                            } catch (IndexOutOfBoundsException ex) {
                                // If tapped quickly when dismiss animation is going on
                                // i.e. the item doesn't exist
                            }
                        }
                    }
                }));

/**     SWIPE TO DELETE & UNDO  [END] **/

        return mRootView;
    }

    private void showDetailPopupAndSetSeen(Bulletin bulletin) {
        // show the detail popup
        mRootView.findViewById(R.id.rlBulletinDetail).setVisibility(View.VISIBLE);
        ((TextView) mRootView.findViewById(R.id.tvBulletinTitleDetail))
            .setText(bulletin.getTitle());
        ((TextView) mRootView.findViewById(R.id.tvBulletinDateDetail)).setText(bulletin.getDate());

        String content = bulletin.getContent();
        content = content.replace("\n", "<br>");
        WebView wvBulletinContentDetail = (WebView) mRootView
            .findViewById(R.id.wvBulletinContentDetail);

        WebSettings webSettings = wvBulletinContentDetail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setPluginState(WebSettings.PluginState.ON);
        }

        AppUtils.removeBottomSpaceFromWebView(wvBulletinContentDetail);
        wvBulletinContentDetail.setWebChromeClient(new WebChromeClient());
        wvBulletinContentDetail
            .loadDataWithBaseURL(null, AppUtils.CSS_FIT_IMAGE_IN_WEBVIEW + content, "text/html",
                "UTF-8", null);
        wvBulletinContentDetail.setBackgroundColor(Color.TRANSPARENT);

        ImageView imgBulletinDetail = (ImageView) mRootView.findViewById(R.id.imgBulletinDetail);
        String imageUrl = bulletin.getImageUrl();
        if (imageUrl != null) {
            Picasso.with(getActivity()).load(imageUrl)
                .error(R.drawable.img_placeholder_failed_square).into(imgBulletinDetail);
        } else
            imgBulletinDetail.setVisibility(View.GONE);

        // the bulletin is seen, change seen status
        if (!bulletin.isSeen()) {
            // Set seen true & save
            bulletin.setSeen(true);
            // Update the unread label in nav drawer
//          mMainActivity.updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, -1);
        }

        // Save the list with updated status
        AppUtils.saveListInSharedPref(mBulletinList, AppUtils.PREF_BULLETIN_LIST);

        // handle close press
        mRootView.findViewById(R.id.imgCloseBulletinDetail)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRootView.findViewById(R.id.rlBulletinDetail).setVisibility(View.GONE);
                }
            });
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
/*

        // dummy bulletin for test [REMOVE for release]
        dummyBulletinCount = mSharedPref.getInt(DUMMY_BULLETIN_COUNT,0);
        if(dummyBulletinCount == 0) {
            mBulletinList.add(new Bulletin(0, "Welcome to Smyril Line!",
            getString(R.string.lorem_ipsum), "Aug 02, 2017 04:15 PM",
            "http://i.ytimg.com/vi/1Xu_qyeiilU/maxresdefault.jpg", false));
            mSharedPref.edit().putInt(DUMMY_BULLETIN_COUNT, ++dummyBulletinCount).apply();
        }
*/
        // dummy for view pager inbox
        mBulletinList
            .add(new Bulletin(0, "Welcome to Smyril Line!", getString(R.string.lorem_ipsum),
                "Aug 02, 2017 04:15 PM", "http://i.ytimg.com/vi/1Xu_qyeiilU/maxresdefault.jpg",
                false));

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

    @Override
    public void onDetach() {
        super.onDetach();
        /*
        // == REMOVE FOR ACTUAL BULLETIN == //
        mBulletinList.clear();
        mAdapter.notifyDataSetChanged();
        mSharedPref.edit().putInt(DUMMY_BULLETIN_COUNT, 0).apply();
    */
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
