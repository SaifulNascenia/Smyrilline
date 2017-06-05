package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.DemoRestaurent;

import java.util.List;

/**
 * Created by saiful on 5/18/17.
 */

public class DemoRestaurentAdapter extends RecyclerView.Adapter<DemoRestaurentAdapter.RestaurentViewHolder> {


    private Context mContext;
    private List<DemoRestaurent> demoRestaurentList;

    public DemoRestaurentAdapter(Context mContext, List<DemoRestaurent> demoRestaurentList) {
        this.mContext = mContext;
        this.demoRestaurentList = demoRestaurentList;
    }


    @Override
    public RestaurentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurents_grid_item, parent, false);
*/
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_duty_free, parent, false);

        return new RestaurentViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RestaurentViewHolder holder, int position) {

        holder.productNameTextView.setText(demoRestaurentList.get(position).getRecipeName());
        holder.productShortDetailsTextview.setText(demoRestaurentList.get(position).getRecipeDetails());
    }

    @Override
    public int getItemCount() {
        return demoRestaurentList.size();
    }

    public class RestaurentViewHolder extends RecyclerView.ViewHolder {

        public TextView productNameTextView;
        public TextView productShortDetailsTextview;

        public RestaurentViewHolder(View view) {
            super(view);

            productNameTextView = (TextView) view.findViewById(R.id.product_name_textview);
            productShortDetailsTextview = (TextView) view.findViewById(R.id.product_short_details_textview);
        }
    }


}