package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.Passenger;
import java.util.ArrayList;

/**
 * Created by raqib on 4/26/16.
 */
public class PassengerPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<Passenger> mList;
    LayoutInflater mLayoutInflater;

    public PassengerPagerAdapter(Context context, ArrayList<Passenger> passengerList) {
        mContext = context;
        mList = passengerList;
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
        View itemView = mLayoutInflater.inflate(R.layout.pager_item_layout_passenger, container, false);

        TextView tvBookingName = (TextView) itemView.findViewById(R.id.tvBookingName);
        TextView tvBookingSex = (TextView) itemView.findViewById(R.id.tvBookingSex);
        TextView tvBookingDOB = (TextView) itemView.findViewById(R.id.tvBookingDOB);
        TextView tvBookingNationality = (TextView) itemView.findViewById(R.id.tvBookingNationality);

        Passenger passenger = mList.get(position);
        tvBookingName.setText(passenger.getName());
        tvBookingSex.setText(passenger.getSex());
        tvBookingDOB.setText(passenger.getDOB());
        tvBookingNationality.setText(passenger.getCountry());

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
