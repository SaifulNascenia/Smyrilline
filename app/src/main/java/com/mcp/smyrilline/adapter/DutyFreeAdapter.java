package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.DemoRestaurent;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.model.dutyfreemodels.DutyFree;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by saiful on 6/12/17.
 */

public class DutyFreeAdapter extends RecyclerView.Adapter<DutyFreeAdapter.DutyFreeViewHolder> {

    private Context mContext;
    private List<Child> dutyFreeChildList;

    public DutyFreeAdapter(Context mContext, List<Child> dutyFreeChildList) {
        this.mContext = mContext;
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

        holder.productNameTextView.setText(dutyFreeChildList.get(position).getName());

        holder.productShortDetailsTextview.setText(dutyFreeChildList.get(position).getText1());

        holder.productPriceTextView.setText(dutyFreeChildList.get(position).getText2().
                substring(1, dutyFreeChildList.get(position).getText2().indexOf(",")));

        holder.productQuantityTextView.setText(dutyFreeChildList.get(position).getText2().
                substring(dutyFreeChildList.get(position).getText2().indexOf(",") + 1,
                        dutyFreeChildList.get(position).getText2().length()));


        Picasso.with(mContext)
                .load(mContext.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        dutyFreeChildList.get(position).getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.productImageView);

    }

    @Override
    public int getItemCount() {
        return dutyFreeChildList.size();
    }


    public class DutyFreeViewHolder extends RecyclerView.ViewHolder {

        public TextView productNameTextView;
        public TextView productShortDetailsTextview;
        public ImageView productImageView;
        public TextView productPriceTextView;
        public TextView productQuantityTextView;

        public DutyFreeViewHolder(View view) {
            super(view);

            productNameTextView = (TextView) view.findViewById(R.id.product_name_textview);
            productShortDetailsTextview = (TextView) view.findViewById(R.id.product_short_details_textview);
            productImageView = (ImageView) view.findViewById(R.id.product_imageview);
            productPriceTextView = (TextView) view.findViewById(R.id.quantity_product_price);
            productQuantityTextView = (TextView) view.findViewById(R.id.quantity_product_price_number_textview);
        }
    }
}
