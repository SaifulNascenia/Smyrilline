package com.mcp.smyrilline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.LoginFragment;
import com.mcp.smyrilline.util.Utils;

public class MainGridActivity extends AppCompatActivity {

    Toolbar mToolbar;
    GridLayout mGridLayoutNavigation;
    ImageView imgGridViewBooking, imgGridShipTracker, imgGridInbox, imgGridDutyFree, imgGridRestaurants,
            imgGridDestinations, imgGridCoupons, imgGridSettings, imgGridInfo;

    // OnyxBeacon SDK


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main_grid);

        mGridLayoutNavigation = (GridLayout) findViewById(R.id.gridLayoutNavigation);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setTitle(getString(R.string.app_name));
    }

    public void onClick(View view) {
        // Create a new fragment and specify the screen to show
        String fragmentName = null;

        switch (view.getId()) {
            case R.id.imgGridViewBooking:
                fragmentName = Utils.fragmentList[0];
                break;
            case R.id.imgGridShipTracker:
                fragmentName = Utils.fragmentList[1];
                break;
            case (R.id.imgGridInbox):
                fragmentName = Utils.fragmentList[2];
                break;
            case R.id.imgGridDutyFree:
                fragmentName = Utils.fragmentList[3];
                break;
            case R.id.imgGridRestaurants:
                fragmentName = Utils.fragmentList[4];
                break;
            case R.id.imgGridDestinations:
                fragmentName = Utils.fragmentList[5];
                break;
            case R.id.imgGridCoupons:
                fragmentName = Utils.fragmentList[6];
                break;
            case R.id.imgGridSettings:
                fragmentName = Utils.fragmentList[7];
                break;
            case R.id.imgGridInfo:
                fragmentName = Utils.fragmentList[8];
                break;
            default:
                fragmentName = LoginFragment.class.getSimpleName();
        }

        Intent intent = new Intent();
        intent.setClass(this, DrawerActivity.class);
        intent.putExtra(Utils.START_DRAWER_FRAGMENT, fragmentName);
        startActivity(intent);
        // close this activity
        finish();
    }
}
