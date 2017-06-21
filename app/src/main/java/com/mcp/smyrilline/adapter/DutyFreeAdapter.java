package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.model.restaurentsmodel.BreakfastItem;
import com.mcp.smyrilline.model.restaurentsmodel.DinnerItem;
import com.mcp.smyrilline.model.restaurentsmodel.LunchItem;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by saiful on 6/12/17.
 */

public class DutyFreeAdapter extends RecyclerView.Adapter<DutyFreeAdapter.DutyFreeViewHolder> {

    private Context mContext;
    private List dutyFreeChildList;
    private String thisAdapterUsedClassName;


    public DutyFreeAdapter(Context mContext, List dutyFreeChildList,
                           String thisAdapterUsedClassName) {

        Log.i("listsize", dutyFreeChildList.size() + "");
        this.mContext = mContext;
        this.thisAdapterUsedClassName = thisAdapterUsedClassName;
        this.dutyFreeChildList = dutyFreeChildList;

       /* if (thisAdapterUsedClassName.equals(AppUtils.fragmentList[4])
                && restaurentFoodtime.equals("Breakfast")) {
            this.dutyFreeChildList = (List<BreakfastItem>) dutyFreeChildList;
        }
        if (thisAdapterUsedClassName.equals(AppUtils.fragmentList[4])
                && restaurentFoodtime.equals("Lunch")) {
            this.dutyFreeChildList = (List<LunchItem>) dutyFreeChildList;
        }
        if (thisAdapterUsedClassName.equals(AppUtils.fragmentList[4])
                && restaurentFoodtime.equals("Dinner")) {
            this.dutyFreeChildList = (List<DinnerItem>) dutyFreeChildList;
        } else {
            this.dutyFreeChildList = (List<Child>) dutyFreeChildList;
        }*/

    }

    @Override
    public DutyFreeAdapter.DutyFreeViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_duty_free, parent, false);

        return new DutyFreeAdapter.DutyFreeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DutyFreeAdapter.DutyFreeViewHolder holder, int position) {


        if (thisAdapterUsedClassName.equals(AppUtils.fragmentList[3])) {

            setViewOnDutyFreeRecylerView(holder, position);
        } else {
            setViewOnRestaurentRecylerView(holder, position);
        }


    }

    private void setViewOnRestaurentRecylerView(DutyFreeViewHolder holder, int position) {

        holder.rootCardView.getLayoutParams().height = dpToPx(160);

        holder.productNameTextView.setVisibility(View.GONE);
        holder.productQuantityTextView.setVisibility(View.GONE);
        holder.productShortDetailsTextview.setTextColor(mContext.getResources().
                getColor(R.color.textColorSecondary));
        holder.productShortDetailsTextview.setTextSize(16);

        if (thisAdapterUsedClassName.equals("Breakfast")) {

            BreakfastItem breakfastItem = (BreakfastItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(breakfastItem.getName());
            holder.productPriceTextView.setText(breakfastItem.getText2());

            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            breakfastItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);


        } else if (thisAdapterUsedClassName.equals("Lunch")) {

            LunchItem lunchItem = (LunchItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(lunchItem.getName());
            holder.productPriceTextView.setText(lunchItem.getText2());

            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            lunchItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);
        } else {

            DinnerItem dinnerItem = (DinnerItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(dinnerItem.getName());
            holder.productPriceTextView.setText(dinnerItem.getText2());


            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            dinnerItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);
        }

    }

    private void setViewOnDutyFreeRecylerView(DutyFreeViewHolder holder, int position) {
/*
        Log.i("dutydata", dutyFreeChildList.get(position).getName()+"\n"+
                dutyFreeChildList.get(position).getText1()+"\n"+);*/

        Child dutyFreeItemChild = (Child) dutyFreeChildList.get(position);
        /*holder.productNameTextView.setText((Child) dutyFreeChildList.get(position).getName());

        holder.productShortDetailsTextview.setText(dutyFreeChildList.get(position).getText2());

        holder.productPriceTextView.setText("€ " + dutyFreeChildList.get(position).getText3().
                substring(1, dutyFreeChildList.get(position).getText3().indexOf(",")));

        holder.productQuantityTextView.setText(dutyFreeChildList.get(position).getText3().
                substring(dutyFreeChildList.get(position).getText3().indexOf(",") + 1,
                        dutyFreeChildList.get(position).getText3().length()));


        Picasso.with(mContext)
                .load(mContext.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        dutyFreeChildList.get(position).getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.productImageView);
*/
        holder.productNameTextView.setText(dutyFreeItemChild.getName());

        holder.productShortDetailsTextview.setText(dutyFreeItemChild.getText2());

        holder.productPriceTextView.setText("€ " + dutyFreeItemChild.getText3().
                substring(1, dutyFreeItemChild.getText3().indexOf(",")));

        holder.productQuantityTextView.setText(dutyFreeItemChild.getText3().
                substring(dutyFreeItemChild.getText3().indexOf(",") + 1,
                        dutyFreeItemChild.getText3().length()));


        Picasso.with(mContext)
                .load(mContext.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        dutyFreeItemChild.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.productImageView);

    }


    private int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public int getItemCount() {
        return dutyFreeChildList.size();
    }


    public class DutyFreeViewHolder extends RecyclerView.ViewHolder {

        public CardView rootCardView;
        public TextView productNameTextView;
        public TextView productShortDetailsTextview;
        public ImageView productImageView;
        public TextView productPriceTextView;
        public TextView productQuantityTextView;

        public DutyFreeViewHolder(View view) {
            super(view);

            rootCardView = (CardView) view.findViewById(R.id.root_card_view);
            productNameTextView = (TextView) view.findViewById(R.id.product_name_textview);
            productShortDetailsTextview = (TextView) view.findViewById(R.id.product_short_details_textview);
            productImageView = (ImageView) view.findViewById(R.id.product_imageview);
            productPriceTextView = (TextView) view.findViewById(R.id.quantity_product_price);
            productQuantityTextView = (TextView) view.findViewById(R.id.quantity_product_price_number_textview);
        }
    }
}
