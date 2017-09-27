package com.mcp.smyrilline.adapter;

/**
 * Created by saiful on 7/7/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.shipinfo.Child;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ShipInfoAdapter extends RecyclerView.Adapter<ShipInfoAdapter.ViewHolder> {

    private final Context context;
    private List mShipInfoList;

    public ShipInfoAdapter(Context context,
                           List mShipInfoList) {
        this.context = context;
        this.mShipInfoList = mShipInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_grid_image_with_center_text, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Child shipInfo = (Child) mShipInfoList.get(position);
        holder.tvShipInfoTitle.setText(Html.fromHtml(shipInfo.getName()));

        Picasso.with(context)
                .load(context.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        shipInfo.getImageUrl())
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(holder.imgRestaurant);
    }

    @Override
    public int getItemCount() {
        return mShipInfoList.size();
    }

    public void setShipInfoList(List mShipInfoList) {
        this.mShipInfoList = mShipInfoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvShipInfoTitle;
        public ImageView imgRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);

            tvShipInfoTitle = (TextView) itemView.findViewById(R.id.titleGridListItem);
            imgRestaurant = (ImageView) itemView.findViewById(R.id.imageGridListItem);
        }
    }
}
