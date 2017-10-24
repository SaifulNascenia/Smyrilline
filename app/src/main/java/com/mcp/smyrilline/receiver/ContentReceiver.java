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
import com.mcp.smyrilline.listener.CouponsListener;
import com.mcp.smyrilline.listener.DrawerCouponsCountListener;
import com.mcp.smyrilline.util.AppUtils;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.rest.model.content.Coupon;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.Eddystone;
import com.onyxbeaconservice.IBeacon;
import java.util.ArrayList;

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
    private OnyxBeaconsListener mOnyxBeaconListener;
    private CouponsListener mCouponsListener;
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

    public void setOnyxCouponsListener(CouponsListener couponsListener) {
        mCouponsListener = couponsListener;
    }

    public void setCouponsCountListener(DrawerCouponsCountListener couponsCountListener) {
        mCouponsCountListener = couponsCountListener;
    }

    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        switch (payloadType) {
            case OnyxBeaconApplication.BEACON_TYPE:
                ArrayList<Beacon> beacons = intent
                        .getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                for (Beacon beacon : beacons) {
                    if (beacon instanceof IBeacon) {
                        IBeacon iBeacon = (IBeacon) beacon;
                        android.util.Log.d("BeaconRec", "IBeacon toString " + iBeacon.toString());
                    } else if (beacon instanceof Eddystone) {
                        Eddystone eddystone = (Eddystone) beacon;
                        android.util.Log
                                .d("BeaconRec", "Eddystone toString " + eddystone.toString());
                    }
                }
                break;
            case OnyxBeaconApplication.COUPON_TYPE:

                mSharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                Coupon coupon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_COUPON);

                System.out.println("Coupon receiver - Received coupon " + gson.toJson(coupon));
                Log.d("ExpTrig", "Coupon exp date is " + coupon.expires);

                if (coupon != null) {
                    // Get coupon list from memory if exists
                    ArrayList<Coupon> couponsFromStorage;
                    String couponsListAsString = mSharedPref
                            .getString(AppUtils.PREF_COUPON_LIST, AppUtils.PREF_NO_ENTRY);
                    if (!couponsListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                        couponsFromStorage = gson
                                .fromJson(couponsListAsString, new TypeToken<ArrayList<Coupon>>() {
                                }.getType());
                    } else {
                        couponsFromStorage = new ArrayList<>();
                    }

                    // Get unseen coupon list from memory
                    ArrayList<Long> unseenCoupons;
                    String unseenCouponsAsString = mSharedPref
                            .getString(AppUtils.PREF_UNSEEN_COUPONS, AppUtils.PREF_NO_ENTRY);
                    if (!unseenCouponsAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                        unseenCoupons = gson
                                .fromJson(unseenCouponsAsString, new TypeToken<ArrayList<Long>>() {
                                }.getType());
                    } else {
                        unseenCoupons = new ArrayList<>();
                    }

                    // Get list of used coupons from memory
                    ArrayList<Long> usedListFromStorage;
                    String usedListAsString = mSharedPref
                            .getString(AppUtils.PREF_USED_COUPONS, AppUtils.PREF_NO_ENTRY);
                    if (!usedListAsString.equals(AppUtils.PREF_NO_ENTRY))
                        usedListFromStorage = gson
                                .fromJson(usedListAsString, new TypeToken<ArrayList<Long>>() {
                                }.getType());
                    else
                        usedListFromStorage = new ArrayList<>();

                    // check if coupon is already present, or was present (i.e. used)
                    if (!(couponsFromStorage.contains(coupon) || usedListFromStorage
                            .contains(coupon.couponId))) {

                        couponsFromStorage.add(0, coupon);  // add the latest coupon to first pos
                        unseenCoupons.add(coupon.couponId); // add id to unseen list

                        // Update the drawer counter
                        if (mCouponsCountListener != null)
                            mCouponsCountListener.onNewCouponReceived(+1);

                        // Update coupons fragment if showing
                        if (mCouponsListener != null)
                            mCouponsListener.onCouponReceived(coupon);

                        // Save updated lists in shared preference
                        AppUtils.saveListInSharedPref(couponsFromStorage,
                                AppUtils.PREF_COUPON_LIST);
                        AppUtils.saveListInSharedPref(unseenCoupons, AppUtils.PREF_UNSEEN_COUPONS);

                        // Generate notification
                        AppUtils.generateNotification(context, coupon.name, coupon.message,
                            CouponsFragment.class.getSimpleName(), null);

                        if (usedListFromStorage.contains(coupon.couponId))
                            Log.i(AppUtils.TAG,
                                    "Used list already contains \"" + coupon.name + "\" with ID - "
                                            + coupon.couponId);
                        else
                            Log.i(AppUtils.TAG,
                                    "Used list doesn't contain \"" + coupon.name + "\" with ID - "
                                            + coupon.couponId);
                    }
                }
                break;
            case OnyxBeaconApplication.COUPONS_DELIVERED_TYPE:
                mSharedPref = PreferenceManager
                        .getDefaultSharedPreferences(context.getApplicationContext());
                ArrayList<Coupon> coupons = intent
                        .getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);

                if (!(coupons == null || coupons.size() == 0)) {

                    // Get coupon list from memory if exists
                    ArrayList<Coupon> couponsFromStorage;
                    String couponsListAsString = mSharedPref.getString(AppUtils.PREF_COUPON_LIST, AppUtils.PREF_NO_ENTRY);
                    if (!couponsListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                        couponsFromStorage = gson.fromJson(couponsListAsString, new TypeToken<ArrayList<Coupon>>() {
                        }.getType());
                    } else {
                        couponsFromStorage = new ArrayList<>();
                    }

                    // Get unseen coupon list from memory
                    ArrayList<Long> unseenCoupons;
                    String unseenCouponsAsString = mSharedPref.getString(AppUtils.PREF_UNSEEN_COUPONS, AppUtils.PREF_NO_ENTRY);
                    if (!unseenCouponsAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                        unseenCoupons = gson.fromJson(unseenCouponsAsString, new TypeToken<ArrayList<Long>>() {
                        }.getType());
                    } else {
                        unseenCoupons = new ArrayList<>();
                    }

                    // Get list of used coupons from memory
                    ArrayList<Long> usedListFromStorage;
                    String usedListAsString = mSharedPref.getString(AppUtils.PREF_USED_COUPONS, AppUtils.PREF_NO_ENTRY);
                    if (!usedListAsString.equals(AppUtils.PREF_NO_ENTRY))
                        usedListFromStorage = gson.fromJson(usedListAsString, new TypeToken<ArrayList<Long>>() {
                        }.getType());
                    else
                        usedListFromStorage = new ArrayList<>();

                    // Check for existing coupons and create a new list
                    ArrayList<Coupon> newCouponsList = new ArrayList<>();
                    for (Coupon cp : coupons) {
                        if (!(couponsFromStorage.contains(cp) || usedListFromStorage.contains(cp.couponId))) {
                            newCouponsList.add(0, cp);  // add the latest coupon to first position
                            unseenCoupons.add(cp.couponId); // add id to the unseen list
                        }

                        if (usedListFromStorage.contains(cp.couponId))
                            Log.i(AppUtils.TAG, "Used list already contains \"" + cp.name + "\" with ID - " + cp.couponId);
                        else
                            Log.i(AppUtils.TAG, "Used list doesn't contain \"" + cp.name + "\" with ID - " + cp.couponId);
                    }

                    if (newCouponsList.size() > 0) {
                        // add the new coupon list to the beginning of existing list
                        couponsFromStorage.addAll(0, newCouponsList);

                        // Update the drawer counter
                        if (mCouponsCountListener != null)
                            mCouponsCountListener.onNewCouponReceived(newCouponsList.size());

                        // Update coupons fragment if visible
                        if (mCouponsListener != null)
                            mCouponsListener.onDeliveredCouponsReceived(newCouponsList);

                        // Save updated lists in shared preference
                        AppUtils.saveListInSharedPref(couponsFromStorage,
                                AppUtils.PREF_COUPON_LIST);
                        AppUtils.saveListInSharedPref(unseenCoupons, AppUtils.PREF_UNSEEN_COUPONS);

                        // Generate notification based on number of coupons
                        if (newCouponsList.size() == 1) {
                            AppUtils.generateNotification(context,
                                    context.getString(R.string.new_coupon),
                                    newCouponsList.get(0).name,
                                CouponsFragment.class.getSimpleName(), null);
                        } else {
                            AppUtils.generateNotification(context,
                                    newCouponsList.size() + " " + context
                                            .getString(R.string.new_coupons_available),
                                    context.getString(R.string.please_check_app_for_details),
                                CouponsFragment.class.getSimpleName(), null);
                        }
                    }
                }
                break;
            case OnyxBeaconApplication.WEB_REQUEST_TYPE:
                String extraInfo = intent.getStringExtra(OnyxBeaconApplication.EXTRA_INFO);
                Log.i(AppUtils.TAG, "AUTH Web reguest info " + extraInfo);
                if (extraInfo.equals(OnyxBeaconApplication.REQUEST_UNAUTHORIZED)) {
                    // Pin based session expired
                }
                break;
        }
    }
}