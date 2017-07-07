package com.mcp.smyrilline.adapter;

/**
 * Created by saiful on 7/7/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.destination.Child;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private final Context context;
    private List mDestinationsList;

    public DestinationAdapter(Context context,
                              List mDestinationsList) {

        this.context = context;
        this.mDestinationsList = mDestinationsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_image_with_center_text, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        final Child info = (Child) mDestinationsList.get(position);
        holder.tvDesinationTitleTitle.setText(info.getName());

        Picasso.with(context)
                .load(context.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        info.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.destinationImage);


    }

    @Override
    public int getItemCount() {

        return mDestinationsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDesinationTitleTitle;
        public ImageView destinationImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDesinationTitleTitle = (TextView) itemView.findViewById(R.id.title_textview);
            destinationImage = (ImageView) itemView.findViewById(R.id.content_imageview);
        }
    }
}
