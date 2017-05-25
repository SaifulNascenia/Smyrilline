package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.CouponAdapter;
import com.mcp.smyrilline.receiver.ContentReceiver;
import com.mcp.smyrilline.util.AppUtils;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.rest.model.content.Coupon;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;

/**
 * Created by raqib on 5/11/17.
 */

public class CouponsFragment extends Fragment implements OnyxCouponsListener {

    public SearchView mSearchView;
    private Context mContext;
    private RecyclerView mListView;
    private Gson gson = new Gson();
    private ArrayList<Long> mUnseenCoupons;
    private TextView tvNothingText;

    // Coupons persistance
    private SharedPreferences mSharedPref;
    private ContentReceiver mContentReceiver;
    private CouponAdapter mAdapter;
    private ArrayList<Coupon> mCouponList;
    private OnyxBeaconManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coupons, container, false);
        mContext = getActivity();

        // set the toolbar as actionbar
        ((DrawerActivity)getActivity()).setToolbarAndToggle((Toolbar)rootView.findViewById(R.id.toolbar));
        getActivity().setTitle((mContext.getResources().getString(R.string.coupons)));

        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        // Init UI
        tvNothingText = (TextView) rootView.findViewById(R.id.tvCouponsNothingText);
        tvNothingText.setVisibility(View.GONE);
        mListView = (RecyclerView) rootView.findViewById(R.id.listViewCoupons);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        // Get stored coupons
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        String couponsListAsString = mSharedPref.getString(AppUtils.PREF_COUPON_LIST, AppUtils.PREF_NO_ENTRY);
        String unseenCouponsAsString = mSharedPref.getString(AppUtils.PREF_UNSEEN_COUPONS, AppUtils.PREF_NO_ENTRY);

        if (!couponsListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
            mCouponList = gson.fromJson(couponsListAsString, new TypeToken<ArrayList<Coupon>>() {
            }.getType());

            mUnseenCoupons = gson.fromJson(unseenCouponsAsString, new TypeToken<ArrayList<Long>>() {
            }.getType());

            // Filter out expired coupons
            mCouponList = filterExpiredCoupons(mCouponList, mUnseenCoupons);
        } else {
            mCouponList = new ArrayList<>();
            mUnseenCoupons = new ArrayList<>();
        }

        mAdapter = new CouponAdapter(mContext, mCouponList, mUnseenCoupons, tvNothingText);
        mListView.setAdapter(mAdapter);
        mSearchView = (SearchView) rootView.findViewById(R.id.couponSearch);

        mAdapter.checkEmpty();

        // removing underline inside SearchView
        int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        searchPlate.setBackgroundColor(Color.WHITE);

        // remove search focusing on first time screen loads
        mSearchView.setFocusable(false);

        // change hint text color
        mSearchView.setQueryHint(Html.fromHtml("<font color = #D3D3D3>" + getResources().getString(R.string.search) + "</font>"));

        // check search input and apply search
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AppUtils.hideKeyboard(getActivity());
                if (query.length() > 0) mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });

        // Initialize content receiver
        mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxCouponsListener(this);

        return rootView;
    }
    
    @Override
    public void onCouponReceived(Coupon coupon, IBeacon beacon) {
        if (coupon != null) {
            mCouponList.add(coupon);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeliveredCouponsReceived(ArrayList<Coupon> deliveredCoupons) {
        ArrayList<Coupon> newCoupons = new ArrayList<>();
        for (Coupon deliveredCoupon : deliveredCoupons) {
            boolean isInList = false;
            for (Coupon coupon : mCouponList) {
                if (coupon.couponId == deliveredCoupon.couponId) {
                    isInList = true;
                }
            }
            if (!isInList) {
                newCoupons.add(deliveredCoupon);
            }
        }
        mCouponList.addAll(newCoupons);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * We will check if any coupon has been used from the coupon detail fragment
         * and update list here
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int couponRemovedPosition = sharedPreferences.getInt(AppUtils.POSITION_COUPON_REMOVED, -1);

        if (couponRemovedPosition != -1) {
            long couponId = mCouponList.get(couponRemovedPosition).couponId;
            // Send 'tapped' to cms
            OnyxBeaconApplication.getOnyxBeaconManager(mContext).markAsTapped(couponId);

            // Adding the ID to the used list and store it
            ArrayList usedCouponList = ((DrawerActivity) mContext).getUsedCouponList();
            usedCouponList.add(couponId);
            Log.i(AppUtils.TAG, "Adding Coupon with id " + couponId + " to the used list");
            AppUtils.saveListInSharedPref(usedCouponList, AppUtils.PREF_USED_COUPONS);

            // Remove the coupon from UI and store the list
            mAdapter.remove(couponRemovedPosition);
            Log.i(AppUtils.TAG, "After removing coupon(" + couponId + ") from list:");
            AppUtils.printCouponList(mCouponList);

            sharedPreferences.edit().putInt(AppUtils.POSITION_COUPON_REMOVED, -1).apply();
        }
    }

    /**
     * Filters out the expired coupons from the list
     * Also removes the expired Ids from the unseen list
     *
     * @param couponList    the list to filter
     * @param unseenCoupons the Id list of unseen coupons
     * @return new list with the unexpired coupons
     */
    private ArrayList<Coupon> filterExpiredCoupons(ArrayList<Coupon> couponList, ArrayList<Long> unseenCoupons) {
        ArrayList<Coupon> filteredList = new ArrayList<>();
        for (Coupon coupon : couponList) {
            if (System.currentTimeMillis() <= coupon.getExpiresAsDate().getTime()) {
                filteredList.add(coupon);
            } else
                unseenCoupons.remove(coupon.couponId);
        }

        // Save the lists
        AppUtils.saveListInSharedPref(filteredList, AppUtils.PREF_COUPON_LIST);
        AppUtils.saveListInSharedPref(unseenCoupons, AppUtils.PREF_UNSEEN_COUPONS);

        return filteredList;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchView.clearFocus();
    }
}
