package com.mcp.smyrilline.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.mcp.smyrilline.BuildConfig;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.CouponsFragment;
import com.mcp.smyrilline.fragment.DestinationFragment;
import com.mcp.smyrilline.fragment.DutyFreeFragment;
import com.mcp.smyrilline.fragment.HelpFragment;
import com.mcp.smyrilline.fragment.InboxFragment;
import com.mcp.smyrilline.fragment.LoginFragment;
import com.mcp.smyrilline.fragment.RestaurantFragment;
import com.mcp.smyrilline.fragment.SettingsFragment;
import com.mcp.smyrilline.fragment.ShipInfoFragment;
import com.mcp.smyrilline.fragment.ShipTrackerFragment;
import com.mcp.smyrilline.util.AppUtils;

public class MainGridActivity extends AppCompatActivity {

    Toolbar mToolbar;
    GridLayout mGridLayoutNavigation;

    // OnyxBeacon SDK


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.i("versiondetails", "versionName " + pInfo.versionName
                    + " versionCode " + pInfo.versionCode);

            Log.i("versiondetails", "build " + "versionName " + BuildConfig.VERSION_NAME
                    + " versionCode " + BuildConfig.VERSION_CODE);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main_grid);
        mGridLayoutNavigation = (GridLayout) findViewById(R.id.gridLayoutNavigation);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
    }

    public void onClick(View view) {
        // Create a new fragment and specify the screen to show
        String fragmentName = null;

        switch (view.getId()) {
            case R.id.imgGridViewBooking:
                fragmentName = LoginFragment.class.getSimpleName();
                break;
            case R.id.imgGridShipTracker:
                fragmentName = ShipTrackerFragment.class.getSimpleName();
                break;
            case (R.id.imgGridInbox):
                fragmentName = InboxFragment.class.getSimpleName();
                break;
            case R.id.imgGridDutyFree:
                fragmentName = DutyFreeFragment.class.getSimpleName();
                break;
            case R.id.imgGridRestaurants:
                fragmentName = RestaurantFragment.class.getSimpleName();
                break;
            case R.id.imgGridDestinations:
                fragmentName = DestinationFragment.class.getSimpleName();
                break;
            case R.id.imgGridCoupons:
                fragmentName = CouponsFragment.class.getSimpleName();
                break;
            case R.id.imgGridSettings:
                fragmentName = SettingsFragment.class.getSimpleName();
                break;
            case R.id.imgGridInfo:
                fragmentName = ShipInfoFragment.class.getSimpleName();
                break;
            case R.id.imgGridHelp:
                fragmentName = HelpFragment.class.getSimpleName();
                break;
            default:
                fragmentName = LoginFragment.class.getSimpleName();
        }
        // start drawer activity
        Intent intent = new Intent();
        intent.setClass(this, DrawerActivity.class);
        intent.putExtra(AppUtils.START_DRAWER_FRAGMENT, fragmentName);
        startActivity(intent);
        // close this activity
        finish();
    }
}
