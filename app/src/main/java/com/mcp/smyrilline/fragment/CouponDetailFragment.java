package com.mcp.smyrilline.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.CouponAdapter;
import com.mcp.smyrilline.util.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by raqib on 5/17/17.
 */

public class CouponDetailFragment extends Fragment{

    private static final String TAG = "smyrilline:couponDetail";
    private CouponAdapter mCouponAdapter;
    private Context mContext;
    private AlertDialog mWarningDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Utils.isNetworkAvailable(mContext))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String response = Utils.isDomainAvailable(mContext, mContext.getResources().getString(R.string.url_wordpress));
                    if (!response.equals(Utils.CONNECTION_OK))
                        if (getActivity() != null) {     // if fragment is switched already
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Utils.showAlertDialog(mContext, response + " Cannot load image.");
                                }
                            });
                        }
                }
            }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // implement "USE" coupon behaviour
        final View rootView = inflater.inflate(R.layout.fragment_coupon_detail, container, false);

        mContext = getActivity();

        // Get the data passed from CouponAdapter
        Bundle extras = getArguments();

        if (extras != null) {

            String imageUrl = extras.getString("imageUrl");
//            OnyxBeaconApplication.loadImageAsync(imageUrl, (NetworkImageView) rootView.findViewById(R.id.imgCouponDetail));

            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_placeholder_thumb)
                    .error(R.drawable.img_placeholder_thumb)
                    .into((ImageView) rootView.findViewById(R.id.imgCouponDetail));

            String offerAmount = extras.getString("offerAmount");
            ((TextView) rootView.findViewById(R.id.tvOfferAmountDetail)).setText(offerAmount);

            String description = extras.getString("description");
            ((TextView) rootView.findViewById(R.id.tvDescriptionDetail)).setText(description);

            // Get expire string
            String expireStr = extras.getString("expireStr");
            ((TextView) rootView.findViewById(R.id.tvExpiresDetail)).setText(" " + expireStr);

            // Get expire time in milliseconds
            final Long expireMillis = extras.getLong("expireMillis");

            final int position = extras.getInt("position");
            final Button btnUse = (Button) rootView.findViewById(R.id.btnUse);

            // Check expire again, in case it expired when coupon was showing in the list
            // if expired, then disable and grey out the 'Use' button
            // change text to 'Expired'
            if (System.currentTimeMillis() > expireMillis) {
                btnUse.setText("Expired");
                btnUse.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    btnUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used, null));
                else
                    btnUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used));
            }

            btnUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Again check expire
                    // in case it expired while viewing the details
                    if (System.currentTimeMillis() > expireMillis) {
                        btnUse.setText("Expired");
                        btnUse.setClickable(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            btnUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used, null));
                        else
                            btnUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used));
                    } else {
                        AlertDialog.Builder warningDialogBuilder = new AlertDialog.Builder(mContext);
                        warningDialogBuilder
                                .setTitle(mContext.getResources().getString(R.string.are_you_sure))
                                .setMessage(Html.fromHtml("<font color = #B33A3A>" + getResources().getString(R.string.use_coupon_warning) + "</font>"))
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        useCouponAndExit(position, btnUse);
                                    }
                                })
                                .setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        mWarningDialog = warningDialogBuilder.create();
                        mWarningDialog.setCancelable(true);
                        mWarningDialog.setCanceledOnTouchOutside(false);
                        mWarningDialog.show();
                    }
                }
            });
        }
        return rootView;
    }

    private void useCouponAndExit(int couponPosition, Button useButton) {
//
//        // implement "USE" coupon behaviour
//        ArrayList<Coupon> couponList = mCouponAdapter.getCouponList();
//
//        if (couponPosition < couponList.size()) {
//            long couponId = couponList.get(couponPosition).couponId;
//            // Send 'tapped' to cms
//            OnyxBeaconApplication.getOnyxBeaconManager(mContext).markAsTapped(couponId);
//
//            // Adding the ID to the used list and store it
//            ArrayList usedCouponList = ((MainActivity) mContext).getUsedCouponList();
//            usedCouponList.add(couponId);
//            Log.w(Utils.TAG, "Adding Coupon with id " + couponId + " to the used list");
//            Log.w(Utils.TAG, "Printing used coupon list in MainActivity");
//            Utils.printCouponIdList(((MainActivity) mContext).getUsedCouponList());
//            Log.w(Utils.TAG, "Printing used coupon list in CouponDetail");
//            Utils.printCouponIdList(usedCouponList);
//            Utils.saveListInSharedPref(usedCouponList, Utils.PREF_USED_COUPONS);
//
//            // Remove the coupon from UI and store the list
//            couponList.remove(couponPosition);
//            Log.w(Utils.TAG, "After removing coupon(" + couponId + ") from list:");
//            Utils.printCouponList(couponList);
//            Utils.saveListInSharedPref(couponList, Utils.PREF_COUPON_LIST);
//
//            mCouponAdapter.notifyItemRemoved(couponPosition);
//            mCouponAdapter.checkEmpty();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext()).edit();
        editor.putInt(Utils.POSITION_COUPON_REMOVED, couponPosition).apply();

        useButton.setText(mContext.getResources().getString(R.string.used));
        useButton.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            useButton.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used, null));
        else
            useButton.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_btn_used));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        }, 345);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mWarningDialog != null && mWarningDialog.isShowing())
            mWarningDialog.dismiss();
    }

    public void setAdapter(CouponAdapter couponAdapter) {
        Log.e(TAG, " set before: CouponAdapter: list size ->" + couponAdapter.getCouponList().size());
        mCouponAdapter = couponAdapter;
        Log.e(TAG, " set after: CouponAdapter: list size ->" + mCouponAdapter.getCouponList().size());
    }

}
