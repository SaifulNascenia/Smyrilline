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
import com.mcp.smyrilline.model.destination.DestinationDetailsChild;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DestinationDetailsAdapter extends RecyclerView.Adapter<DestinationDetailsAdapter.ViewHolder> {

    private final Context context;
    private List mDestinationDetailsList;

    public DestinationDetailsAdapter(Context context,
                                     List mDestinationDetailsList) {
        this.context = context;
        this.mDestinationDetailsList = mDestinationDetailsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image_with_center_text, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final DestinationDetailsChild child = (DestinationDetailsChild) mDestinationDetailsList.get(position);
        holder.tvDestinationTitle.setText(Html.fromHtml(child.getName()));

        Picasso.with(context)
                .load(context.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        child.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imgRestaurant);
    }

    @Override
    public int getItemCount() {
        return mDestinationDetailsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDestinationTitle;
        public ImageView imgRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDestinationTitle = (TextView) itemView.findViewById(R.id.title_textview);
            imgRestaurant = (ImageView) itemView.findViewById(R.id.content_imageview);
        }
    }
}
