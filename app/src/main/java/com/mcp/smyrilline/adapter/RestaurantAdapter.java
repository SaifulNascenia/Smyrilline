package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;

import com.mcp.smyrilline.model.Restaurant;

import java.util.ArrayList;

/**
 * Adapter for restaurant list in RestaurantFragment
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Restaurant> restaurantList;
    private TextView tvNothingText;

    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurantList, TextView tvNothingText) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.tvNothingText = tvNothingText;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurants, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurant restaurant = restaurantList.get(position);

        //bindDataonRestaurentView(restaurant, holder);
        bindDataonDestinationView(restaurant, holder);


    }

    private void bindDataonDestinationView(Restaurant restaurant, ViewHolder holder) {
        holder.tvRestaurantTitle.setText(Html.fromHtml(restaurant.getTitle()));
    }

    private void bindDataonRestaurentView(Restaurant restaurant, ViewHolder holder) {

        holder.tvRestaurantTitle.setText(Html.fromHtml(restaurant.getTitle()));

        AQuery aq = new AQuery(context);
        aq.id(holder.imgRestaurant).image(restaurant.getImageUrl(),
                false, true, 0, 0, null, AQuery.FADE_IN);

        /*holder.rlRestaurantListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RestaurantDetailFragment restaurantMenuFragment = new RestaurantDetailFragment();
                // Pass data to detail fragment and replace
                restaurantMenuFragment.setData(restaurant);

                ((DrawerActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.content_frame, restaurantMenuFragment)
                        .commit();
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void setRestaurantList(ArrayList<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    /**
     * Calls notifyDataSetChanged and checks empty list
     * Updates the textview if list is empty
     * We update the textview here as adapter is refreshed from multiple places
     */
    public void refreshList() {
        notifyDataSetChanged();
        if (restaurantList.isEmpty())
            tvNothingText.setVisibility(View.VISIBLE);
        else
            tvNothingText.setVisibility(View.GONE);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlRestaurantListItem;
        public TextView tvRestaurantTitle;
        public ImageView imgRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);
            rlRestaurantListItem = (RelativeLayout) itemView.findViewById(R.id.rlRestaurantListItem);
            tvRestaurantTitle = (TextView) itemView.findViewById(R.id.tvRestaurantTitle);
            imgRestaurant = (ImageView) itemView.findViewById(R.id.imgRestaurant);
        }
    }
}
