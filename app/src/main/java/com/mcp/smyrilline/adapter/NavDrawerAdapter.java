package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.DrawerItem;
import com.mcp.smyrilline.activity.DrawerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raqib on 5/19/17.
 */

public class NavDrawerAdapter extends ArrayAdapter<DrawerItem> {

    private Context context;
    private int layoutResId;
    private List<DrawerItem> list;
    public NavDrawerAdapter(@NonNull Context context, @LayoutRes int layoutResId, @NonNull List<DrawerItem> list) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;

    }

        @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

        @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                LayoutInflater inflater = ((DrawerActivity)context).getLayoutInflater();
                convertView = inflater.inflate(layoutResId, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            }else
                viewHolder = (ViewHolder)convertView.getTag();

            DrawerItem drawerItem = list.get(position);
            viewHolder.tvDrawerItemTitle.setText(drawerItem.getTitle());
            viewHolder.imgDrawerItemIcon.setImageResource(drawerItem.getImageResId());
            return convertView;
    }

    private static class ViewHolder{
        TextView tvDrawerItemTitle;
        ImageView imgDrawerItemIcon;
        public ViewHolder(View itemView) {
            tvDrawerItemTitle = (TextView) itemView.findViewById(R.id.tvDrawerItemTitle);
            imgDrawerItemIcon = (ImageView) itemView.findViewById(R.id.imgDrawerItemIcon);

        }
    }
}
