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
import at.blogc.android.views.ExpandableTextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.destination.DestinationDetailsChild;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;
import java.util.List;

public class DestinationDetailsAdapter2 extends
        RecyclerView.Adapter<DestinationDetailsAdapter2.ViewHolder> {

    private final Context context;
    private List mDestinationDetailsList;

    public DestinationDetailsAdapter2(Context context,
            List mDestinationDetailsList) {
        this.context = context;
        this.mDestinationDetailsList = mDestinationDetailsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_linear_title_image_description, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final DestinationDetailsChild child = (DestinationDetailsChild) mDestinationDetailsList
                .get(position);

        holder.tvTitle.setText(Html.fromHtml(child.getName()));
        holder.tvExpandableDescription.setText(child.getText3());
        AppUtils.handleExpandableTextView(holder.tvExpandableDescription, holder.tvDescriptionMore);

        Picasso.with(context)
                .load(context.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        child.getImageUrl())
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(holder.featureImage);
    }

    @Override
    public int getItemCount() {
        return mDestinationDetailsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView featureImage;
        private ExpandableTextView tvExpandableDescription;
        private TextView tvDescriptionMore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.list_item_linear_title);
            featureImage = (ImageView) itemView.findViewById(R.id.list_item_linear_image);
            tvExpandableDescription = (ExpandableTextView) itemView
                    .findViewById(R.id.list_item_linear_expandable_description);
            tvDescriptionMore = (TextView) itemView.findViewById(R.id.list_item_linear_expand);
        }
    }
}
