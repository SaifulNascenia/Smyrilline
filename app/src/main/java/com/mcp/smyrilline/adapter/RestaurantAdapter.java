package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.restaurant.Child;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final Context context;
    private List mRestaurantsList;

    public RestaurantAdapter(Context context,
                             List mRestaurantsList) {
        this.context = context;
        this.mRestaurantsList = mRestaurantsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_linear_image_with_center_text, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Child restaurant = (Child) mRestaurantsList.get(position);
        holder.tvRestaurantTitle.setText(Html.fromHtml(restaurant.getName()));

        Picasso.with(context)
                .load(context.getResources().
                        getString(R.string.image_downloaded_base_url) +
                        restaurant.getImageUrl())
                .placeholder(R.drawable.img_placeholder_restaurant)
                .into(holder.imgRestaurant);
    }

    @Override
    public int getItemCount() {
        return mRestaurantsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRestaurantTitle;
        public ImageView imgRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);

            tvRestaurantTitle = (TextView) itemView.findViewById(R.id.titleLinearListItem);
            imgRestaurant = (ImageView) itemView.findViewById(R.id.imageLinearListItem);
        }
    }
}
