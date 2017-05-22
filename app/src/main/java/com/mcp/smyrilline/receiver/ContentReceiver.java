package com.mcp.smyrilline.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.CouponsFragment;
import com.mcp.smyrilline.listener.DrawerCouponsCountListener;
import com.mcp.smyrilline.util.Utils;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.listeners.OnyxPushListener;
import com.onyxbeacon.listeners.OnyxTagsListener;

import com.onyxbeacon.rest.model.account.BluemixApp;
import com.onyxbeacon.rest.model.content.Coupon;
import com.onyxbeacon.rest.model.content.Tag;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is the BroadcastReceiver for the coupon and push notification
 * Exactly identical to 'ContentReceiver.java' in the sample app source
 * (https://connect.onyxbeacon.com/admin/setup/type/android/v/2.2a)
 * <p/>
 * We only changed the order in which the coupon is added to the list
 */
public class ContentReceiver extends BroadcastReceiver {

    private static ContentReceiver sInstance;
    /* Coupons */
    private static String COUPONS_TAG = "coupons_tag";
    private OnyxBeaconsListener mOnyxBeaconListener;
    private OnyxCouponsListener mOnyxCouponsListener;
    private OnyxTagsListener mOnyxTagsListener;
    private OnyxPushListener mOnyxPushListener;
    private SharedPreferences mSharedPref;
    private Gson gson = new Gson();
    private DrawerCouponsCountListener mCouponsCountListener;

    public ContentReceiver() {
    }

    public static ContentReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new ContentReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public void setOnyxBeaconsListener(OnyxBeaconsListener onyxBeaconListener) {
        mOnyxBeaconListener = onyxBeaconListener;
    }

    public void setOnyxCouponsListener(OnyxCouponsListener onyxCouponsListener) {
        mOnyxCouponsListener = onyxCouponsListener;
    }

    public void setOnyxTagsListener(OnyxTagsListener onyxTagsListener) {
        mOnyxTagsListener = onyxTagsListener;
    }

    public void setOnyxPushListener(OnyxPushListener onyxPushListener) {
        mOnyxPushListener = onyxPushListener;
    }

    public void setCouponsCountListener(DrawerCouponsCountListener couponsCountListener) {
        mCouponsCountListener = couponsCountListener;
    }

    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        switch (payloadType) {
            case OnyxBeaconApplication.TAG_TYPE:
                ArrayList<Tag> tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
                if (mOnyxTagsListener != null) {
                    mOnyxTagsListener.onTagsReceived(tagsList);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.BEACON_TYPE:
                ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                if (mOnyxBeaconListener != null) {
                    mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.COUPON_TYPE:

                mSharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                ArrayList<Coupon> coupons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);
                IBeacon beacon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BEACON);

                Log.i(Utils.TAG, "BUZZ beacon " + gson.toJson(beacon));

                if (!(coupons == null || coupons.size() == 0)) {

                    // Get coupon list from memory if exists
                    ArrayList<Coupon> couponsFromStorage;
                    String couponsListAsString = mSharedPref.getString(Utils.PREF_COUPON_LIST, Utils.PREF_NO_ENTRY);
                    if (!couponsListAsString.equals(Utils.PREF_NO_ENTRY)) {
                        couponsFromStorage = gson.fromJson(couponsListAsString, new TypeToken<ArrayList<Coupon>>() {
                        }.getType());
                    } else {
                        couponsFromStorage = new ArrayList<>();
                    }

                    // Get unseen coupon list from memory
                    ArrayList<Long> unseenCoupons;
                    String unseenCouponsAsString = mSharedPref.getString(Utils.PREF_UNSEEN_COUPONS, Utils.PREF_NO_ENTRY);
                    if (!unseenCouponsAsString.equals(Utils.PREF_NO_ENTRY)) {
                        unseenCoupons = gson.fromJson(unseenCouponsAsString, new TypeToken<ArrayList<Long>>() {
                        }.getType());
                    } else {
                        unseenCoupons = new ArrayList<>();
                    }

                    // Get list of used coupons from memory
                    ArrayList<Long> usedListFromStorage;
                    String usedListAsString = mSharedPref.getString(Utils.PREF_USED_COUPONS, Utils.PREF_NO_ENTRY);
                    if (!usedListAsString.equals(Utils.PREF_NO_ENTRY))
                        usedListFromStorage = gson.fromJson(usedListAsString, new TypeToken<ArrayList<Long>>() {
                        }.getType());
                    else
                        usedListFromStorage = new ArrayList<>();

                    ArrayList<Coupon> newCouponsList = new ArrayList<>();

                    for (Coupon cp : coupons) {
                        if (!(couponsFromStorage.contains(cp) || usedListFromStorage.contains(cp.couponId))) {
                            newCouponsList.add(0, cp);  // add the latest coupon to first position
                            unseenCoupons.add(cp.couponId);

                            // Update the drawer counter
                            if (mCouponsCountListener != null)
                                mCouponsCountListener.onNewCouponReceived();
                        }

                        if (usedListFromStorage.contains(cp.couponId))
                            Log.i(Utils.TAG, "Used list already contains \"" + cp.name + "\" with ID - " + cp.couponId);
                        else
                            Log.i(Utils.TAG, "Used list doesn't contain \"" + cp.name + "\" with ID - " + cp.couponId);
                    }

                    // add the latest coupon list to the begining of existing list
                    couponsFromStorage.addAll(0, newCouponsList);

                    for (Iterator<Coupon> ci = newCouponsList.iterator(); ci.hasNext(); ) {
                        Coupon c = ci.next();
                        Utils.generateNotification(context, c.name, c.message, R.mipmap.ic_launcher,
                                CouponsFragment.class.getSimpleName());
                    }

                    if (mOnyxCouponsListener != null)
                        mOnyxCouponsListener.onDeliveredCouponsReceived(newCouponsList);

                    // Save updated lists in shared preference
                    Utils.saveListInSharedPref(couponsFromStorage, Utils.PREF_COUPON_LIST);
                    Utils.saveListInSharedPref(unseenCoupons, Utils.PREF_UNSEEN_COUPONS);
                }
                break;
            case OnyxBeaconApplication.PUSH_TYPE:
                BluemixApp bluemixApp = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BLUEMIX);
                System.out.println("PUSH Received bluemix credentials " + gson.toJson(bluemixApp));
                if (mOnyxPushListener != null) {
                    mOnyxPushListener.onBluemixCredentialsReceived(bluemixApp);
                }
                break;
            case OnyxBeaconApplication.COUPONS_DELIVERED_TYPE:
                ArrayList<Coupon> deliveredCoupons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);
                android.util.Log.d("Receiver", "No of delivered coupons " + deliveredCoupons.size());
                if (mOnyxCouponsListener != null) {
                    mOnyxCouponsListener.onDeliveredCouponsReceived(deliveredCoupons);
                }
                break;
            case OnyxBeaconApplication.WEB_REQUEST_TYPE:
                String extraInfo = intent.getStringExtra(OnyxBeaconApplication.EXTRA_INFO);
                System.out.println("AUTH Web reguest info " + extraInfo);
                if (extraInfo.equals(OnyxBeaconApplication.REQUEST_UNAUTHORIZED)) {
                    // Pin based session expired
                }
                break;
        }
    }
}