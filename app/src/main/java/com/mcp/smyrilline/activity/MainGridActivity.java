package com.mcp.smyrilline.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.CouponsFragment;
import com.mcp.smyrilline.fragment.DestinationFragment;
import com.mcp.smyrilline.fragment.DutyFreeFragment;
import com.mcp.smyrilline.fragment.HelpFragment;
import com.mcp.smyrilline.fragment.LoginFragment;
import com.mcp.smyrilline.fragment.MessagingMainFragment;
import com.mcp.smyrilline.fragment.RestaurantFragment;
import com.mcp.smyrilline.fragment.SettingsFragment;
import com.mcp.smyrilline.fragment.ShipInfoFragment;
import com.mcp.smyrilline.fragment.ShipTrackerFragment;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.model.messaging.ServerMessageItem;
import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.McpApplication;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainGridActivity extends AppCompatActivity implements BulletinListener {

    Toolbar mToolbar;
    GridLayout mGridLayoutNavigation;
    private TextView tvGridMenuMessagingCounter;

    // OnyxBeacon SDK


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main_grid);
        mGridLayoutNavigation = (GridLayout) findViewById(R.id.gridLayoutNavigation);
        tvGridMenuMessagingCounter = (TextView) findViewById(R.id.tvGridMenuMessagingCounter);
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
            case (R.id.imgGridMessaging):
                fragmentName = MessagingMainFragment.class.getSimpleName();
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
        intent.putExtra(AppUtils.KEY_START_DRAWER_FRAGMENT, fragmentName);
        startActivity(intent);
        // close this activity // let's keep it to keep service bound
//        finish();
    }

    @Override
    public void onBulletinReceived(Bulletin newBulletin) {

    }

    @Override
    public void onBulletinListReceived(ArrayList<Bulletin> newBulletinList) {

    }

    /**
     * Our client is online for chat for both MainGridActivity, and DrawerActivity
     */
    public void onResume() {
        super.onResume();
        Log.i(AppUtils.TAG, "MainGridActivity: onResume");

//        // Bind to the the socket service
        McpApplication.instance().bindSignalRService(SignalRService.startIntent(getBaseContext()));

        // user online
//        AppUtils.setClientOnlineStatus();

        // subscribe to eventbus
        McpApplication.registerWithEventBus(this);

        // set counter in onResume, to update for messages coming in App background
        AppUtils.updateCounterLabel(tvGridMenuMessagingCounter,
            McpApplication.instance().sharedPref()
                .getInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT, 0),
            AppUtils.GRID_MENU_MAX_MESSSAGE_COUNT);
/*

        if (mBleReceiver == null) mBleReceiver = BleStateReceiver.instance();

        if (mContentReceiver == null) mContentReceiver = ContentReceiver.instance();

        registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
        receiverRegistered = true;


        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Snackbar.make(mDrawerLayout, "Device does not support Bluetooth",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Snackbar.make(mDrawerLayout, "Please turn on bluetooth",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                // Enable scanner in foreground mode and registerBulletin receiver
                mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
                mManager.setForegroundMode(true);
            }
        }
*/
    }

    public void onPause() {
        super.onPause();

        Log.i(AppUtils.TAG, "MainGridActivity: onPause");

//        // Unbind from signalr service
        McpApplication.instance().unbindSignalRService();

        // client offline
//        AppUtils.setClientOfflineStatus();

        // Unsubscribe from eventbus
        EventBus.getDefault().unregister(this);

/*        // Set scanner in background mode
        mManager.setForegroundMode(false);

        if (bleStateRegistered) {
            unregisterReceiver(mBleReceiver);
            bleStateRegistered = false;
        }

        if (receiverRegistered) {
            unregisterReceiver(mContentReceiver);
            receiverRegistered = false;
        }*/

//        if (push != null) {
//            push.hold();
//        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageReceived(ServerMessageItem serverMessageItem) {
        // save updated new message count
        SharedPreferences sharedPref = McpApplication.instance().sharedPref();
        int newChatMessageCount = sharedPref.getInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT, 0);
        sharedPref.edit().putInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT, ++newChatMessageCount)
            .apply();

        // update counter label
        AppUtils.updateCounterLabel(tvGridMenuMessagingCounter, newChatMessageCount,
            AppUtils.GRID_MENU_MAX_MESSSAGE_COUNT);
    }
}
