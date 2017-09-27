package com.mcp.smyrilline.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.mcp.smyrilline.activity.GuideActivity;
import com.mcp.smyrilline.fragment.GuideFragment;

/**
 * Created by raqib on 2/17/16.
 */
public class GuideScreenPagerAdapter extends FragmentPagerAdapter {

    public GuideScreenPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return GuideFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return GuideActivity.GUIDE_SCREEN_PAGE_COUNT;
    }
}
