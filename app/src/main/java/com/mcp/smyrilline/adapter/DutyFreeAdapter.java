package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.DutyFreeFragment;
import com.mcp.smyrilline.model.dutyfree.Child;
import com.mcp.smyrilline.model.restaurant.BreakfastItem;
import com.mcp.smyrilline.model.restaurant.DinnerItem;
import com.mcp.smyrilline.model.restaurant.LunchItem;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by saiful on 6/12/17.
 */

public class DutyFreeAdapter extends RecyclerView.Adapter<DutyFreeAdapter.DutyFreeViewHolder> {

    private Context mContext;
    private List dutyFreeChildList;
    private String thisAdapterClassCalledClassName;


    public DutyFreeAdapter(Context mContext, List dutyFreeChildList,
                           String thisAdapterClassCalledClassName) {

        this.mContext = mContext;
        this.thisAdapterClassCalledClassName = thisAdapterClassCalledClassName;
        this.dutyFreeChildList = dutyFreeChildList;


    }

    @Override
    public DutyFreeAdapter.DutyFreeViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_duty_free, parent, false);

        return new DutyFreeAdapter.DutyFreeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DutyFreeAdapter.DutyFreeViewHolder holder, int position) {

        if (thisAdapterClassCalledClassName.equals(DutyFreeFragment.class.getSimpleName())) {

            setViewOnDutyFreeRecylerView(holder, position);
        } else {
            setViewOnRestaurantRecylerView(holder, position);
        }


    }

    private void setViewOnRestaurantRecylerView(DutyFreeViewHolder holder, int position) {

        holder.rootCardView.getLayoutParams().height = AppUtils.dpToPx(160);

        holder.productNameTextView.setVisibility(View.GONE);
        holder.pennyValueOfProducPriceTextview.setVisibility(View.GONE);
        holder.productShortDetailsTextview.setTextColor(mContext.getResources().
                getColor(R.color.textColorSecondary));
        holder.productShortDetailsTextview.setTextSize(16);

        if (thisAdapterClassCalledClassName.equals(BreakfastItem.class.getSimpleName())) {

            BreakfastItem breakfastItem = (BreakfastItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(breakfastItem.getName());
            holder.euroValueOfProducPriceTextview.setText(breakfastItem.getSubheader());

            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            breakfastItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);


        } else if (thisAdapterClassCalledClassName.equals(LunchItem.class.getSimpleName())) {

            LunchItem lunchItem = (LunchItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(lunchItem.getName());
            holder.euroValueOfProducPriceTextview.setText(lunchItem.getSubheader());

            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            lunchItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);
        } else {

            DinnerItem dinnerItem = (DinnerItem) dutyFreeChildList.get(position);

            holder.productShortDetailsTextview.setText(dinnerItem.getName());
            holder.euroValueOfProducPriceTextview.setText(dinnerItem.getSubheader());


            Picasso.with(mContext)
                    .load(mContext.getResources().
                            getString(R.string.image_downloaded_base_url) +
                            dinnerItem.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.productImageView);
        }

    }

    private void setViewOnDutyFreeRecylerView(DutyFreeViewHolder holder, int position) {

        Child dutyFreeItemChild = (Child) dutyFreeChildList.get(position);

        holder.productNameTextView.setText(dutyFreeItemChild.getName());

        holder.productShortDetailsTextview.setText(dutyFreeItemChild.getHeader());

        holder.euroValueOfProducPriceTextview.setText("€ " + dutyFreeItemChild.getPrice().
                substring(1, dutyFreeItemChild.getPrice().indexOf(",")));

        holder.pennyValueOfProducPriceTextview.setText(dutyFreeItemChild.getPrice().
                substring(dutyFreeItemChild.getPrice().indexOf(",") + 1,
                        dutyFreeItemChild.getPrice().length()));

        Picasso.with(mContext)
                .load(mContext.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        dutyFreeItemChild.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.productImageView);

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
        public TextView euroValueOfProducPriceTextview;
        public TextView pennyValueOfProducPriceTextview;

        public DutyFreeViewHolder(View view) {
            super(view);

            rootCardView = (CardView) view.findViewById(R.id.root_card_view);
            productNameTextView = (TextView) view.findViewById(R.id.product_name_textview);
            productShortDetailsTextview = (TextView) view.findViewById(R.id.product_short_details_textview);
            productImageView = (ImageView) view.findViewById(R.id.product_imageview);
            euroValueOfProducPriceTextview = (TextView) view.findViewById(R.id.euro_value_of_product_price_textview);
            pennyValueOfProducPriceTextview = (TextView) view.findViewById(R.id.penny_value_of_product_price_textview);
        }
    }
}
