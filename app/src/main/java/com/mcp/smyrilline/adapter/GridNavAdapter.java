package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.mcp.smyrilline.model.GridNavItem;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.util.StaticData;

import java.util.ArrayList;

/**
 * Created by raqib on 5/4/17.
 */

public class GridNavAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<GridNavItem> gridNavList;

    public GridNavAdapter(Context context, ArrayList gridNavList) {
        this.context = context;
        this.gridNavList = gridNavList;
    }

    @Override
    public int getCount() {
        return gridNavList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridNavList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.nav_grid_item, parent, false);
        Button navButton = (Button) convertView.findViewById(R.id.btnGridNavItem);
        navButton.setText(StaticData.gridNavList.get(position).getTitle());

        navButton.setCompoundDrawables(null, context.getResources().getDrawable(StaticData.gridNavList.get(position).getImageId()), null, null);
        return convertView;
    }
}
