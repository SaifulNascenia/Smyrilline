package com.mcp.smyrilline.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.GuideScreenPagerAdapter;
import com.mcp.smyrilline.util.AppUtils;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * Created by raqib on 2/17/16.
 */
public class GuideActivity extends FragmentActivity {

    public static final int GUIDE_SCREEN_PAGE_COUNT = 5;
    private String[] mGuideScreenTitles;
    private String[] mGuideScreenSubTitles;
    private int[] mGuideScreenImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        final Context context = this;

        mGuideScreenTitles = new String[]{
                context.getResources().getString(R.string.guide_text_header_nav),
                context.getResources().getString(R.string.guide_text_header_shiptracker),
//                context.getResources().getString(R.string.guide_text_header_2),
                context.getResources().getString(R.string.guide_text_header_restaurants),
                context.getResources().getString(R.string.guide_text_header_taxfree),
                context.getResources().getString(R.string.guide_text_header_destinations)
        };

        mGuideScreenSubTitles = new String[]{
                context.getResources().getString(R.string.guide_text_subheader_nav),
                context.getResources().getString(R.string.guide_text_subheader_shiptracker),
//                context.getResources().getString(R.string.guide_text_subheader_2),
                context.getResources().getString(R.string.guide_text_subheader_restaurants),
                context.getResources().getString(R.string.guide_text_subheader_taxfree),
                context.getResources().getString(R.string.guide_text_subheader_destinations)
        };

        mGuideScreenImages = new int[]{
                R.drawable.img_guide_screen_nav,
                R.drawable.img_guide_screen_tracker,
//                R.drawable.img_guide_screen_coupons,

                R.drawable.img_guide_screen_restaurants,
                R.drawable.img_guide_screen_taxfree,
                R.drawable.img_guide_screen_destinations
        };

        // Setup view pager
        GuideScreenPagerAdapter pagerAdapter = new GuideScreenPagerAdapter(
                getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.guideScreenParallaxViewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(GUIDE_SCREEN_PAGE_COUNT - 1);

        PageIndicator pagerIndicator = (CirclePageIndicator) findViewById(
                R.id.guideScreePagerIndicator);
        pagerIndicator.setViewPager(viewPager);

        final ImageView imgStartArrow = (ImageView) findViewById(R.id.imgStartArrow);
        //We set this on the indicator, NOT the pager
        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == (GUIDE_SCREEN_PAGE_COUNT - 1)) {
                    imgStartArrow.setVisibility(View.VISIBLE);
                    imgStartArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Start your app main activity
                            Intent i = new Intent(context, MainGridActivity.class);
                            startActivity(i);

                            // save
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .edit()
                                    .putBoolean(AppUtils.FIRST_RUN, false)
                                    .apply();

                            // close current activity
                            finish();
                        }
                    });
                } else
                    imgStartArrow.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public String getGuideScreenTitle(int position) {
        return mGuideScreenTitles[position];
    }

    public String getGuideScreenSubTitle(int position) {
        return mGuideScreenSubTitles[position];
    }

    public int getGuideScreenImage(int position) {
        return mGuideScreenImages[position];
    }
}
