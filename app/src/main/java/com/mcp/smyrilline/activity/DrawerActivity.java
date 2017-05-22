package com.mcp.smyrilline.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.fragment.InfoFragment;
import com.mcp.smyrilline.fragment.SettingsFragment;
import com.mcp.smyrilline.model.DrawerItem;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.NavDrawerAdapter;
import com.mcp.smyrilline.fragment.TicketFragment;
import com.mcp.smyrilline.fragment.CouponsFragment;
import com.mcp.smyrilline.fragment.DestinationsFragment;
import com.mcp.smyrilline.fragment.DutyFreeFragment;
import com.mcp.smyrilline.fragment.InboxFragment;
import com.mcp.smyrilline.fragment.LoginFragment;
import com.mcp.smyrilline.fragment.RestaurantsFragment;
import com.mcp.smyrilline.fragment.ShipTrackerFragment;
import com.mcp.smyrilline.listener.BleStateListener;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.listener.DrawerCouponsCountListener;
import com.mcp.smyrilline.model.Bulletin;
import com.mcp.smyrilline.receiver.BleStateReceiver;
import com.mcp.smyrilline.receiver.ContentReceiver;
import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.Utils;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthData;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;
import com.onyxbeacon.service.logging.LoggingStrategy;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity implements BleStateListener, BulletinListener, DrawerCouponsCountListener {
    public static final String FRAGMENT_PARENT_TAG = "parent_fragment";
    public static final String FRAGMENT_CHILD_TAG = "child_fragment";
    private static final int REQUEST_ENABLE_BT = 1;
    public static BulletinListener mListener;
    private ArrayList<Long> mUsedCouponList;
    private TextView tvDrawerInboxCount;
    private TextView tvDrawerCouponsCount;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private String[] mPlanetTitles;

    private static final String TAG = "smyrilline MainActivity";
    private static final int REQUEST_FINE_LOCATION = 0;
    private OnyxBeaconManager mManager;
    private String CONTENT_INTENT_FILTER;
    private String BLE_INTENT_FILTER;
    private ContentReceiver mContentReceiver;
    private BleStateReceiver mBleReceiver;
    private boolean receiverRegistered = false;
    private boolean bleStateRegistered = false;

    // Bulletins
    private SignalRService mSignalRService;
    private ServiceConnection mServiceConnection;
    private BulletinListener mBulletinListener;
    private boolean mIsBound = false;
    private Snackbar mBluetoothSnackbar;

    private SharedPreferences mSharedPreferences;
    private Gson gson = new Gson();
    private Context mContext;
    private ArrayList<DrawerItem> mDrawerListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        initUI(savedInstanceState);

        // SignalRService initialize before load list (for Bulletins)
        initSignalRService();

        // Load lists before setup drawer
        loadSavedLists();

        // setup drawer
        setupNavigationDrawer();

        // show fragment

        Bundle bundle = getIntent().getExtras();
        String fragmentName = bundle.getString(Utils.START_DRAWER_FRAGMENT);
        if (fragmentName != null) {
            showFragment(fragmentName);
        }

        // onyx beacon  (for Coupons)
//        initOnyxBeacon();
    }


    private void initUI(Bundle savedInstanceState) {
        setContentView(R.layout.activity_drawer);

        mDrawerListItems = new ArrayList<>();
        mDrawerListItems.add(new DrawerItem(getString(R.string.view_booking), R.drawable.ic_grid_ticket_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.ship_tracker), R.drawable.ic_grid_ship_tracker_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.inbox), R.drawable.ic_grid_inbox_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.duty_free), R.drawable.ic_grid_duty_free_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.restaurants), R.drawable.ic_grid_restaurants_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.destinations), R.drawable.ic_grid_destinations_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.coupons), R.drawable.ic_grid_coupons_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.settings), R.drawable.ic_grid_settings_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.info), R.drawable.ic_grid_info_white));
    }

    private void showFragment(String fragmentName) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        boolean loggedIn = mSharedPreferences.getBoolean(Utils.PREF_LOGGED_IN, false);

        switch (fragmentName) {
            case ("LoginFragment"):
                fragment = new LoginFragment();
                if (!loggedIn)  // if logged, then go to next case
                    break;      // otherwise break out
            /*case ("TicketFragment"):
                fragment = new TicketFragment();
                break;*/
            case ("ShipTrackerFragment"):
                fragment = new ShipTrackerFragment();
                break;
            case ("InboxFragment"):
                fragment = new InboxFragment();
                break;
            case ("DutyFreeFragment"):
                fragment = new DutyFreeFragment();
                break;
            case ("RestaurantsFragment"):
                fragment = new RestaurantsFragment();
                break;
            case ("DestinationsFragment"):
                fragment = new DestinationsFragment();
                break;
            case ("SettingsFragment"):
                fragment = new SettingsFragment();
                break;
            case ("CouponsFragment"):
                fragment = new CouponsFragment();
                break;
            case ("InfoFragment"):
                fragment = new InfoFragment();
                break;
        }

        // Only LoginFragment is Parent, others are child
        String fragmentTag = (fragment instanceof LoginFragment) || (fragment instanceof TicketFragment) ? FRAGMENT_PARENT_TAG : FRAGMENT_CHILD_TAG;

        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_right, 0)
                .replace(R.id.content_frame, fragment, fragmentTag)
                .commit();

        // update selected item and title, then close the drawer
//        mDrawerListView.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerListView);
    }

    private void initSignalRService() {
        // Setup service connection
        // it will be called after the activity is bound to the service with bindService()
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSignalRService = ((SignalRService.Binder) service).getService();
//                mSignalRService.onStartCommand(null, 0, 0);
                mSignalRService.setListener(DrawerActivity.this);
                mIsBound = true;
                Log.i(TAG, "SignalRService: listener set");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSignalRService = null;
                mIsBound = false;
            }
        };

        // Start the socket service
        startService(SignalRService.startIntent(getBaseContext()));
    }

    private void loadSavedLists() {
        // Get used coupon list from memory if available
        String usedCouponListAsString = mSharedPreferences.getString(Utils.PREF_USED_COUPONS, Utils.PREF_NO_ENTRY);
        if (!usedCouponListAsString.equals(Utils.PREF_NO_ENTRY))
            mUsedCouponList = gson.fromJson(usedCouponListAsString, new TypeToken<ArrayList<Long>>() {
            }.getType());
        else
            mUsedCouponList = new ArrayList<>();
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerListView = (ListView) findViewById(R.id.drawer_listview);
        mDrawerListView.setAdapter(new NavDrawerAdapter(this, R.layout.list_item_drawer, mDrawerListItems));
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* if (position == 0) {      // show grid activity
                    startActivity(new Intent(DrawerActivity.this, MainGridActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), Utils.fragmentList[position - 1] + "", Toast.LENGTH_LONG).show();
                    showFragment(Utils.fragmentList[position - 1]);
                }*/

                showFragment(Utils.fragmentList[position]);

                // Toast.makeText(getApplicationContext(), Utils.fragmentList[position] + "", Toast.LENGTH_LONG).show();
            }
        });

        // enable ActionBar app icon to behave as action to toggle nav drawer
        /*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  *//* host Activity *//*
                mDrawerLayout,         *//* DrawerLayout object *//*
//                R.drawable.ic_drawer,  *//* nav drawer image to replace 'Up' caret *//*
                R.string.drawer_open,  *//* "open drawer" description for accessibility *//*
                R.string.drawer_close  *//* "close drawer" description for accessibility *//*
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        */
    }

    private void initOnyxBeacon() {
        mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setCouponsCountListener(this);

        //Register for BLE events
        mBleReceiver = BleStateReceiver.getInstance();
        mBleReceiver.setBleStateListener(this);


        BLE_INTENT_FILTER = getPackageName() + ".scan";
        registerReceiver(mBleReceiver, new IntentFilter(BLE_INTENT_FILTER));
        bleStateRegistered = true;

        CONTENT_INTENT_FILTER = getPackageName() + ".content";
        registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
        receiverRegistered = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "Checking location permission.");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            }
        }

        mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
        mManager.setDebugMode(LoggingStrategy.DEBUG);
        mManager.setAPIEndpoint("https://connect.onyxbeacon.com");
        mManager.setCouponEnabled(true);
        mManager.setAPIContentEnabled(true);
        mManager.enableGeofencing(true);
        mManager.setLocationTrackingEnabled(true);
        AuthData authData = new AuthData();
        authData.setAuthenticationMode(AuthenticationMode.CLIENT_SECRET_BASED);
        authData.setClientId(getString(R.string.onyx_client_id));
        authData.setSecret(getString(R.string.onyx_secret));
        mManager.setAuthData(authData);
    }

    /**
     * Requests the Location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */

    private void requestLocationPermission() {
        // BEGIN_INCLUDE(location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(mDrawerLayout, "Location permission are needed to enable location",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(DrawerActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_FINE_LOCATION);
                        }
                    })
                    .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity: onStop");

        // Unbind from socket service
        if (mSignalRService != null)
            mSignalRService.setListener(null);
        if (mIsBound)
            unbindService(mServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity: onDestroy");
    }
/*

    private void checkBluetooth() {
        if (mManager.isBluetoothAvailable()) {
            // Enable scanner in foreground mode and register receiver
            mManager.setForegroundMode(true);
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If bluetooth is turned on, dismiss snackbar
        if (resultCode == Activity.RESULT_OK)
            if (mBluetoothSnackbar.isShown())
                mBluetoothSnackbar.dismiss();
    }

    public void onResume() {
        super.onResume();
/*

        if (mBleReceiver == null) mBleReceiver = BleStateReceiver.getInstance();

        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();

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
                // Enable scanner in foreground mode and register receiver
                mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
                mManager.setForegroundMode(true);
            }
        }
*/

//        if (push != null) {
//            if (notificationListener != null) push.listen(notificationListener);
//        }
    }

    public void onPause() {
        super.onPause();
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

    @Override
    public void onBleStackEvent(int event) {

    }

    @Override
    public void onNewCouponReceived() {
        // update drawer counter
//        updateDrawerCounterLabelAndSave(Utils.PREF_UNREAD_COUPONS, +1);
    }

    public ArrayList<Long> getUsedCouponList() {
        return mUsedCouponList;
    }

    @Override
    public void onBulletinReceived(Bulletin newBulletin) {
        // if InboxFragment is visible, update UI
        if (mBulletinListener != null)
            mBulletinListener.onBulletinReceived(newBulletin);

        // update drawer counter
//        updateDrawerCounterLabelAndSave(Utils.PREF_UNREAD_BULLETINS, +1);
    }

    @Override
    public void onBulletinListReceived(ArrayList<Bulletin> newBulletinList) {
        // if InboxFragment is visible, update UI
        if (mBulletinListener != null)
            mBulletinListener.onBulletinListReceived(newBulletinList);

        // update drawer counter
//        updateDrawerCounterLabelAndSave(Utils.PREF_UNREAD_BULLETINS, +newBulletinList.size());
    }

    // Called from InboxFragment
    public void setBulletinListener(BulletinListener bulletinListener) {
        mBulletinListener = bulletinListener;
    }

    /**
     * Updates the counter label in Navigation Drawer
     * and saves in shared preference
     *
     * @param countType the menu 'bulletin' or 'coupon'
     * @param change    change amount
     */
    public void updateDrawerCounterLabelAndSave(String countType, int change) {
        // Check count type bulletin or coupon
        TextView countLabel = countType == Utils.PREF_UNREAD_BULLETINS ? tvDrawerInboxCount : tvDrawerCouponsCount;
        String currentValue = countLabel.getText().toString().trim().split(" ")[0];

        // If current text is not "99+", we'll update counter
        if (!currentValue.contains("+")) {
            int newValue = Integer.parseInt(currentValue) + change;

            countLabel.setText(newValue + " " + getString(R.string.label_new));

            // Hide label if none unread
            if (newValue > 0) {
                countLabel.setVisibility(View.VISIBLE);
                if (newValue > 99)
                    countLabel.setText("99+ " + getString(R.string.label_new));
                else {
                    countLabel.setText(newValue + " " + getString(R.string.label_new));
                }
            } else
                countLabel.setVisibility(View.GONE);

            // Save new value in memory
            mSharedPreferences.edit().putInt(countType, newValue).apply();
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showFragment(Utils.fragmentList[0]);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setToolbarAndToggle(Toolbar toolbar) {
        if (toolbar != null) {
            AppCompatActivity appCompatActivity = this;
            appCompatActivity.setSupportActionBar(toolbar);

            mDrawerLayout = (DrawerLayout) appCompatActivity.findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        } else {
            mDrawerLayout.setDrawerListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen())
            closeNavDrawer();
        else {
            startActivity(new Intent(this, MainGridActivity.class));
            finish();
        }
    }

    /**
     * @return true if drawer is currently open, else false
     */
    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Close the Sliding navigation drawer
     */
    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
