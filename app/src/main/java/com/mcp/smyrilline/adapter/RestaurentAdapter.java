package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.Restaurent;

import java.util.List;

/**
 * Created by saiful on 5/18/17.
 */

public class RestaurentAdapter extends RecyclerView.Adapter<RestaurentAdapter.RestaurentViewHolder> {


    private Context mContext;
    private List<Restaurent> restaurentList;

    public RestaurentAdapter(Context mContext, List<Restaurent> restaurentList) {
        this.mContext = mContext;
        this.restaurentList = restaurentList;
    }


    @Override
    public RestaurentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurents_grid_item, parent, false);

        return new RestaurentViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RestaurentViewHolder holder, int position) {

        holder.recipeNameTextView.setText(restaurentList.get(position).getRecipeName());
        holder.recipeDetailsTextView.setText(restaurentList.get(position).getRecipeDetails());
    }

    @Override
    public int getItemCount() {
        return restaurentList.size();
    }

    public class RestaurentViewHolder extends RecyclerView.ViewHolder {

        public TextView recipeNameTextView;
        public TextView recipeDetailsTextView;

        public RestaurentViewHolder(View view) {
            super(view);

            recipeNameTextView = (TextView) view.findViewById(R.id.recipe_name_textview);
            recipeDetailsTextView = (TextView) view.findViewById(R.id.recipe_details_textview);
        }
    }


}
