package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.activity.MainGridActivity;
import com.mcp.smyrilline.fragment.CouponDetailFragment;
import com.mcp.smyrilline.util.AppUtils;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.rest.model.content.Coupon;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by raqib on 8/26/15.
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder>  //implements Filterable
{
    private ArrayList<Coupon> mCouponList;
    private ArrayList<Coupon> mTempList;
    private Context mContext;
    private CouponAdapter mAdapter;
    private TextView tvNothingText;
    private ArrayList<Long> mUnseenCoupons;

    public CouponAdapter(Context context, ArrayList<Coupon> couponList, ArrayList<Long> unseenCoupons, TextView tvNothingText) {

        mContext = context;
        mCouponList = couponList;
        mUnseenCoupons = unseenCoupons;
        this.tvNothingText = tvNothingText;

        // for search filter
        this.mTempList = new ArrayList<>();
        mTempList.addAll(couponList);

        // for passing to detail fragment
        mAdapter = this;
    }

    /**
     * Search filter method
     */
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.US).trim();
        mCouponList.clear();
        if (TextUtils.isEmpty(charText)) {
            mCouponList.addAll(mTempList);
        } else {
            for (Coupon coupon : mTempList) {
                if (coupon.name.toLowerCase(Locale.US)
                        .contains(charText)) {
                    mCouponList.add(coupon);
                }
            }
        }
        refreshSearchedList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.mcp.smyrilline.R.layout.list_item_coupon, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Coupon coupon = mCouponList.get(position);
        holder.tvOfferAmount.setText(coupon.name);
        holder.tvDescription.setText(coupon.message);

        // Set the expire time in format: "MMM dd, HH:mm' e.g. "Nov 01, 18:00"
        final Date expireDate = coupon.getExpiresAsDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppUtils.TIME_FORMAT_COUPON_EXPIRE);
        final String expireStr = simpleDateFormat.format(expireDate);

        holder.tvExpires.setText(" " + expireStr);

//        OnyxBeaconApplication.loadImageAsync(coupon.imageThumb, holder.imgCouponThumb);

        Picasso.with(mContext)
                .load(coupon.imageThumb)
                .placeholder(R.drawable.img_placeholder_thumb)
                .error(R.drawable.img_placeholder_thumb)
                .into(holder.imgCouponThumb);

        // Check if the coupon is seen, if not, show ribbon
        if (mUnseenCoupons.contains(coupon.couponId))
            holder.imgNewRibbon.setVisibility(View.VISIBLE);
        else
            holder.imgNewRibbon.setVisibility(View.GONE);

//        imageLoader.displayImage(coupon.imageThumb, holder.imgCouponThumb, mDisplayOptions);
//        AQuery aq = new AQuery(holder.itemView);
//        aq.id(holder.imgCouponThumb).image(coupon.imageThumb, false, true, 0, R.drawable.img_placeholder_failed_square, null, AQuery.FADE_IN);

        holder.cardViewCoupon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Send 'opened' statistic to cms
                OnyxBeaconApplication.getOnyxBeaconManager(mContext).markAsOpened(coupon.couponId);

                // If it was unseen, remove Id from the unseen list, save, and update drawer counter
                if (mUnseenCoupons.contains(coupon.couponId)) {
                    Log.i(AppUtils.TAG, "Unseen coupon list contains coupon(" + coupon.couponId + ") at index" + mUnseenCoupons.indexOf(coupon.couponId));
                    mUnseenCoupons.remove(coupon.couponId);
                    Log.i(AppUtils.TAG, "After removing id from unseen list:");
                    AppUtils.printCouponIdList(mUnseenCoupons);
                    AppUtils.saveListInSharedPref(mUnseenCoupons, AppUtils.PREF_UNSEEN_COUPONS);
                    ((DrawerActivity) mContext).updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_COUPONS, -1);
                }

                // Pass coupon data to the detail fragment
                Bundle couponData = new Bundle();
                couponData.putString("imageUrl", coupon.imageThumb);
                couponData.putInt("position", position);
                couponData.putInt("listSize", getItemCount());
                couponData.putLong("ID", coupon.couponId);
                couponData.putString("offerAmount", coupon.name);
                couponData.putString("description", coupon.message);
                couponData.putString("expireStr", expireStr);
                couponData.putLong("expireMillis", expireDate.getTime());

                CouponDetailFragment couponDetailFragment = new CouponDetailFragment();

                // Pass the arguments so we can remove the coupon when use pressed
                couponDetailFragment.setArguments(couponData);

                ((MainGridActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, 0)
                        .replace(R.id.content_frame, couponDetailFragment)
                        .commit();
            }
        });
    }



    public ArrayList<Coupon> getCouponList() {
        return mCouponList;
    }

    /**
     * checks empty list
     * Updates the textview if list is empty
     * We update the textview here as adapter is refreshed from multiple places
     */
    public void checkEmpty() {
        if (mCouponList.isEmpty()) {
            tvNothingText.setVisibility(View.VISIBLE);
            tvNothingText.setText(mContext.getResources().getText(R.string.no_coupons));
        }
        else
            tvNothingText.setVisibility(View.GONE);
    }

    /**
     * Calls notifyDataSetChanged and checks empty list
     * Updates the textview if list is empty
     */
    public void refreshSearchedList() {

        if (mCouponList.size() == 0) {
            tvNothingText.setVisibility(View.VISIBLE);
            tvNothingText.setText(mContext.getResources().getText(R.string.no_results));
        } else
            tvNothingText.setVisibility(View.GONE);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCouponList.size();
    }

    public void addAll(int position, List<Coupon> couponList) {
        mCouponList.addAll(position, couponList);
        notifyItemRangeInserted(position, couponList.size());
        checkEmpty();
    }

    public void remove(int position) {
        mCouponList.remove(position);
        notifyItemRemoved(position);
        AppUtils.saveListInSharedPref(mCouponList, AppUtils.PREF_COUPON_LIST);
        checkEmpty();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewCoupon;
        NetworkImageView imgCouponThumb;
        TextView tvOfferAmount;
        TextView tvDescription;
        TextView tvExpires;
        ImageView imgNewRibbon;

        ViewHolder(View itemView) {
            super(itemView);
            cardViewCoupon = (CardView) itemView.findViewById(R.id.cardViewCoupon);
            imgCouponThumb = (NetworkImageView) itemView.findViewById(R.id.imgCouponThumb);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvOfferAmount = (TextView) itemView.findViewById(R.id.tvOfferAmount);
            tvExpires = (TextView) itemView.findViewById(R.id.tvExpires);
            imgNewRibbon = (ImageView) itemView.findViewById(R.id.imgNewRibbon);
        }
    }
}
