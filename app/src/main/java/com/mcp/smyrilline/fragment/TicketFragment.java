package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.TicketFragmentPagerAdapter;

/**
 * Created by raqib on 5/11/17.
 */

public class TicketFragment extends Fragment {

    private Context mContext;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SharedPreferences mSharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();


        getActivity().invalidateOptionsMenu();
        // Inflate tab_layout and setup Views.
        View rootView = inflater.inflate(R.layout.fragment_ticket, null);
        ((DrawerActivity) getActivity()).setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(mContext.getResources().getString(R.string.my_smyrilline));

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mTabLayout = (TabLayout) rootView.findViewById(R.id.ticketTabs);
        mViewPager = (ViewPager) rootView.findViewById(R.id.ticketPager);

        // Get the titles of Tabs
        String tabTitles[] = {mContext.getResources().getString(R.string.booking),
                mContext.getResources().getString(R.string.meals)};

        // Set adapter
        TicketFragmentPagerAdapter pagerAdapter = new TicketFragmentPagerAdapter(tabTitles, getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

//        View tab1_view = tabLayout.getTabAt(0).getCustomView();
//        View tab2_view = tabLayout.getTabAt(1).getCustomView();
//        TextView tab1_title = (TextView) tab1_view.findViewById(R.id.tv_tab_title);
//        ImageView img1 = (ImageView) tab1_view.findViewById(R.id.img);
//        TextView tab2_title = (TextView) tab2_view.findViewById(R.id.tv_tab_title);
//        ImageView img2 = (ImageView) tab2_view.findViewById(R.id.img);
//        tab1_title.setText("Tab1");
//        img1.setImageResource(R.drawable.sample_image);
//        tab2_title.setText("Tab2");
//        img2.setImageResource(R.drawable.sample_image);

//        mTabLayout.setTabsFromPagerAdapter(mViewPager);

        /**
         * Now, this is a workaround ,
         * The setupWithViewPager doesn't work without the runnable .
         * Maybe a Support Library Bug .
         */

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });

        return rootView;
    }
}
