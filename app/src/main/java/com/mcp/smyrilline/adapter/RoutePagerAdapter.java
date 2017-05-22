package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.RouteItem;

import java.util.ArrayList;

/**
 * Created by raqib on 4/26/16.
 */
public class RoutePagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<RouteItem> mList;
    private LayoutInflater mLayoutInflater;

    public RoutePagerAdapter(Context context, ArrayList<RouteItem> routeList) {
        mContext = context;
        mList = routeList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item_layout_route, container, false);

        TextView tvDepartureHarbor = (TextView) itemView.findViewById(R.id.tvDepartureHarbor);
        TextView tvDepartureDate = (TextView) itemView.findViewById(R.id.tvDepartureDate);
        TextView tvArrivalHarbor = (TextView) itemView.findViewById(R.id.tvArrivalHarbor);
        TextView tvArrivalDate = (TextView) itemView.findViewById(R.id.tvArrivalDate);

        RouteItem routeItem = mList.get(position);
        tvDepartureHarbor.setText(routeItem.getDepartureHarbor());
        tvDepartureDate.setText(routeItem.getDepartureDate());
        tvArrivalHarbor.setText(routeItem.getArrivalHarbor());
        tvArrivalDate.setText(routeItem.getArrivalDate());

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
