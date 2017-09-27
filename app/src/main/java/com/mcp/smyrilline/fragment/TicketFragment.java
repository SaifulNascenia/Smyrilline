package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mContext = getActivity();

        getActivity().invalidateOptionsMenu();
        // Inflate tab_layout and setup Views.
        View rootView = inflater.inflate(R.layout.fragment_ticket, null);
        ((DrawerActivity) getActivity())
                .setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(getString(R.string.my_smyrilline));

        mTabLayout = (TabLayout) rootView.findViewById(R.id.ticketTabs);
        mViewPager = (ViewPager) rootView.findViewById(R.id.ticketPager);

        // Get the titles of Tabs
        String tabTitles[] = {mContext.getResources().getString(R.string.booking),
                mContext.getResources().getString(R.string.meals)};

        // Set adapter
        TicketFragmentPagerAdapter pagerAdapter = new TicketFragmentPagerAdapter(tabTitles,
                getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

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
