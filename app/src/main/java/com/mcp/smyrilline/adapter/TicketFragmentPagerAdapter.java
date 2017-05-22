package com.mcp.smyrilline.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mcp.smyrilline.fragment.MealsFragment;
import com.mcp.smyrilline.fragment.TicketDetailFragment;


/**
 * Adapter for the ViewPager in WelcomeFragment.
 * 3 fragments: LoginFragment & TicketDetailFragment for 1st view, HelpFragment for 2nd view
 * Switching between Login and Ticket handled by FirstPageListener
 */
public class TicketFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mPageTitles;

    public TicketFragmentPagerAdapter(String[] pageTitles, final FragmentManager fragmentManager) {
        super(fragmentManager);
        mPageTitles = pageTitles;
    }

    @Override
    public int getCount() {
        return mPageTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return TicketDetailFragment.newInstance();
        }
        if (position == 1)
            return MealsFragment.newInstance();

        return null;
    }
}

