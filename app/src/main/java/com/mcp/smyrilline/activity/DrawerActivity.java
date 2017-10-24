package com.mcp.smyrilline.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.DrawerMenuAdapter;
import com.mcp.smyrilline.adapter.LanguageSpinnerAdapter;
import com.mcp.smyrilline.fragment.CouponsFragment;
import com.mcp.smyrilline.fragment.DestinationFragment;
import com.mcp.smyrilline.fragment.DutyFreeFragment;
import com.mcp.smyrilline.fragment.HelpFragment;
import com.mcp.smyrilline.fragment.InboxFragment;
import com.mcp.smyrilline.fragment.LoginFragment;
import com.mcp.smyrilline.fragment.MessagingMainFragment;
import com.mcp.smyrilline.fragment.ProfileDialogFragment;
import com.mcp.smyrilline.fragment.RealtimeChatFragment;
import com.mcp.smyrilline.fragment.RestaurantFragment;
import com.mcp.smyrilline.fragment.SettingsFragment;
import com.mcp.smyrilline.fragment.ShipInfoFragment;
import com.mcp.smyrilline.fragment.ShipTrackerFragment;
import com.mcp.smyrilline.fragment.TicketFragment;
import com.mcp.smyrilline.listener.BleStateListener;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.listener.DrawerCouponsCountListener;
import com.mcp.smyrilline.model.DrawerItem;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.model.messaging.NewMessageCount;
import com.mcp.smyrilline.receiver.BleStateReceiver;
import com.mcp.smyrilline.receiver.ContentReceiver;
import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.ImagePicker;
import com.mcp.smyrilline.util.McpApplication;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthData;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;
import com.onyxbeacon.service.logging.LoggingStrategy;
import java.util.ArrayList;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DrawerActivity extends AppCompatActivity implements BleStateListener, BulletinListener,
    DrawerCouponsCountListener {

    public static final String ENTERED_FROM_MENU = "enteredFromMenu";
    public static final String FRAGMENT_PARENT_TAG = "parent_fragment";
    public static final String FRAGMENT_CHILD_TAG = "child_fragment";
    private static final String TAG = "smyrilline MainActivity";
    private static final int REQUEST_FINE_LOCATION = 0;
    public static BulletinListener mListener;
    private static boolean languageChangeRestart = false;
    // Fragment list in the order they are shown in menu
    private final String[] fragmentList = {
        LoginFragment.class.getSimpleName(),
        ShipTrackerFragment.class.getSimpleName(),
        MessagingMainFragment.class.getSimpleName(),
        DutyFreeFragment.class.getSimpleName(),
        RestaurantFragment.class.getSimpleName(),
        DestinationFragment.class.getSimpleName(),
        CouponsFragment.class.getSimpleName(),
        SettingsFragment.class.getSimpleName(),
        ShipInfoFragment.class.getSimpleName(),
        HelpFragment.class.getSimpleName(),
    };
    private ArrayList<Long> mUsedCouponList;
    private TextView tvDrawerInboxCount;
    private TextView tvDrawerCouponsCount;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Spinner mLanguageSpinner;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
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
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        mContext = this;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        initUI(savedInstanceState);

        // Load lists before setup drawer
        loadSavedLists();

        // setup drawero
        setupNavigationDrawer();

        // show fragment
        // if it is restart from language switch, must start the last shown fragment
        String fragmentName = languageChangeRestart ? LoginFragment.class.getSimpleName() :
            getIntent().getExtras().getString(AppUtils.KEY_START_DRAWER_FRAGMENT);

        if (fragmentName != null)
            showFragment(fragmentName);
        languageChangeRestart = false;

        // onyx beacon  (for Coupons)
//        initOnyxBeacon();

    }


    private void initUI(Bundle savedInstanceState) {
        setContentView(R.layout.activity_drawer);

        mDrawerListItems = new ArrayList<>();
        mDrawerListItems.add(new DrawerItem(getString(R.string.view_booking),
            R.drawable.ic_grid_ticket_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.ship_tracker),
            R.drawable.ic_grid_ship_tracker_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.messaging),
            R.drawable.ic_grid_messaging_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.duty_free),
            R.drawable.ic_grid_duty_free_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.restaurants),
            R.drawable.ic_grid_restaurants_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.destinations),
            R.drawable.ic_grid_destinations_white));
        mDrawerListItems
            .add(new DrawerItem(getString(R.string.coupons), R.drawable.ic_grid_coupons_white));
        mDrawerListItems.add(new DrawerItem(getString(R.string.settings),
            R.drawable.ic_grid_settings_white));
        mDrawerListItems
            .add(new DrawerItem(getString(R.string.info), R.drawable.ic_grid_info_white));
        mDrawerListItems
            .add(new DrawerItem(getString(R.string.help), R.drawable.ic_grid_help_white));
    }

    private void showFragment(String fragmentName) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        boolean loggedIn = mSharedPreferences.getBoolean(AppUtils.PREF_LOGGED_IN, false);

        switch (fragmentName) {
            case ("LoginFragment"):
                /*fragment = new LoginFragment();
                if (!loggedIn)  // if logged, then go to next case
                    break;      // otherwise break out
                */
                if (loggedIn)
                    fragment = new TicketFragment();
                else
                    fragment = new LoginFragment();
                break;
            case ("ShipTrackerFragment"):
                fragment = new ShipTrackerFragment();
                break;
            case ("MessagingMainFragment"):
                fragment = new MessagingMainFragment();
                break;
            case ("RealtimeChatFragment"):  // From notification
                fragment = new RealtimeChatFragment();
                break;
            case ("DutyFreeFragment"):
                fragment = new DutyFreeFragment();
                break;
            case ("RestaurantFragment"):
                fragment = new RestaurantFragment();
                break;
            case ("DestinationFragment"):
                fragment = new DestinationFragment();
                break;
            case ("SettingsFragment"):
                fragment = new SettingsFragment();
                break;
            case ("CouponsFragment"):
                fragment = new CouponsFragment();
                break;
            case ("ShipInfoFragment"):
                fragment = new ShipInfoFragment();
                break;
            case ("HelpFragment"):
                fragment = new HelpFragment();
                break;
        }

        fragment.setArguments(bundle);
        // Only LoginFragment is Parent, others are child
        String fragmentTag =
            (fragment instanceof LoginFragment) || (fragment instanceof TicketFragment)
                ? FRAGMENT_PARENT_TAG : FRAGMENT_CHILD_TAG;

        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_right, 0)
            .replace(R.id.content_frame, fragment, fragmentTag)
            .commit();

        // update selected item and title, then close the drawer
//        mDrawerListView.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerListView);
    }

    private void loadSavedLists() {
        // Get used coupon list from memory if available
        String usedCouponListAsString = mSharedPreferences
            .getString(AppUtils.PREF_USED_COUPONS, AppUtils.PREF_NO_ENTRY);
        if (!usedCouponListAsString.equals(AppUtils.PREF_NO_ENTRY))
            mUsedCouponList = gson
                .fromJson(usedCouponListAsString, new TypeToken<ArrayList<Long>>() {
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
        mDrawerListView.setAdapter(
            new DrawerMenuAdapter(this, R.layout.list_item_drawer, mDrawerListItems));
        mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showFragment(fragmentList[position]);
            }
        });
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
                .setAction(McpApplication.instance().context().getString(R.string.ok),
                    new View.OnClickListener() {
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
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
        }
    }

/*

    private void checkBluetooth() {
        if (mManager.isBluetoothAvailable()) {
            // Enable scanner in foreground mode and registerBulletin receiver
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
        if (requestCode == AppUtils.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            if (mBluetoothSnackbar != null && mBluetoothSnackbar.isShown())
                mBluetoothSnackbar.dismiss();
        } else if (requestCode == ImagePicker.REQUEST_PICK_IMAGE
            || requestCode == ImagePicker.REQUEST_CROP_IMAGE) {
            getSupportFragmentManager()
                .findFragmentByTag(ProfileDialogFragment.class.getSimpleName())
                .onActivityResult(requestCode, resultCode, data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(AppUtils.TAG, "DrawerActivity: onStart");

        // Bind to the the socket service
        McpApplication.instance().bindSignalRService(SignalRService.startIntent(getBaseContext()));

        // registerBulletin from eventbus
        McpApplication.registerWithEventBus(this);
        // client is online
        AppUtils.setClientOnlineStatus();

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

    @Override
    public void onStop() {
        super.onStop();

        Log.i(AppUtils.TAG, "DrawerActivity: onStop");

        // set client offline
        AppUtils.setClientOfflineStatus();
        // unregister from eventbus
        EventBus.getDefault().unregister(this);
        // unbind from service
        McpApplication.instance().unbindSignalRService();



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
    public void onNewCouponReceived(int count) {
        // update drawer counter
//        updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_COUPONS, +1);
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
//        updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, +1);
    }

    @Override
    public void onBulletinListReceived(ArrayList<Bulletin> newBulletinList) {
        // if InboxFragment is visible, update UI
        if (mBulletinListener != null)
            mBulletinListener.onBulletinListReceived(newBulletinList);

        // update drawer counter
//        updateDrawerCounterLabelAndSave(AppUtils.PREF_UNREAD_BULLETINS, +newBulletinList.size());
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
     * @param change change amount
     */
    public void updateDrawerCounterLabelAndSave(String countType, int change) {
        // Check count type bulletin or coupon
        TextView countLabel = countType == AppUtils.PREF_UNREAD_BULLETINS ? tvDrawerInboxCount
            : tvDrawerCouponsCount;
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

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            //actionBar.setDisplayShowHomeEnabled(true);
            //actionBar.setDisplayHomeAsUpEnabled(true);

            if (getCurrentFragment() instanceof LoginFragment
                || getCurrentFragment() instanceof TicketFragment)
                initLanguageSwitcher(toolbar);

            mDrawerLayout = (DrawerLayout) appCompatActivity.findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        } else {
            mDrawerLayout.setDrawerListener(null);
        }
    }

    private void initLanguageSwitcher(Toolbar toolbar) {
        // Populate flag icon id list
        final Integer[] flagIcons = {
            R.drawable.img_flag_english
            , R.drawable.img_flag_deutsch
            , R.drawable.img_flag_faroese
            , R.drawable.img_flag_danish};
        mLanguageSpinner = new Spinner(getSupportActionBar().getThemedContext());
        // Adapter for spinner
        LanguageSpinnerAdapter langSpinnerAdapter = new LanguageSpinnerAdapter(mContext,
            R.layout.list_item_language_spinner, flagIcons);
        mLanguageSpinner.setAdapter(langSpinnerAdapter);

        // Set the initial spinner item
        // according to the current app language
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();

        switch (currentLanguage) {
            case "en":
                mLanguageSpinner.setSelection(0);
                break;
            case "de":
                mLanguageSpinner.setSelection(1);
                break;
            case "fo":
                mLanguageSpinner.setSelection(2);
                break;
            case "da":
                mLanguageSpinner.setSelection(3);
                break;
            default:
                mLanguageSpinner.setSelection(0);
        }

        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * A known issue when itemSelected is called first time erroneously
             * without user selection
             * <p/>
             * Using a counter to check first instance
             * http://stackoverflow.com/a/10102356
             */
            int isLoaded = 0;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                int position, long arg3) {
                if (isLoaded >= 1) {
                    // Check the selected position in the icon array
                    switch (flagIcons[position]) {
                        case R.drawable.img_flag_english:
                            updateAppLocale("en");
                            break;
                        case R.drawable.img_flag_deutsch:
                            updateAppLocale("de");
                            break;
                        case R.drawable.img_flag_faroese:
                            updateAppLocale("fo");
                            break;
                        case R.drawable.img_flag_danish:
                            updateAppLocale("da");
                            break;
                    }
                }
                isLoaded++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        // Add the spinner to the right of Toolbar
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT;
        toolbar.addView(mLanguageSpinner, layoutParams);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.isDrawerIndicatorEnabled() &&
            mDrawerToggle.onOptionsItemSelected(item)) {
            Toast.makeText(getBaseContext(), "1", Toast.LENGTH_LONG).show();
            return true;
        } else if (item.getItemId() == android.R.id.home &&
            getSupportFragmentManager().popBackStackImmediate()) {
            Toast.makeText(getBaseContext(), "home", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateAppLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getBaseContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        // Save to load when app starts
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(AppUtils.PREF_LOCALE, lang);
        Log.i("MainActivity", "onCreate() - Saving locale -> " + lang);
        editor.apply();

        // Send broadcast to service
        Log.i(AppUtils.TAG, "MainActivity: sending locale changed broadcast");
        sendBroadcast(new Intent(SignalRService.ACTION_APP_SETTINGS_CHANGED));

        // Restart activity for effects to take place
        this.recreate();
        languageChangeRestart = true;
    }

    @Override
    public void onBackPressed() {

       /* if (isNavDrawerOpen())
            closeNavDrawer();
        else {
            startActivity(new Intent(this, MainGridActivity.class));
            finish();
        }*/
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            // Toast.makeText(getBaseContext(), "backstack has " + getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
//            startActivity(new Intent(this, MainGridActivity.class));
//            finish();
            //Toast.makeText(getBaseContext(), "no backstack has", Toast.LENGTH_LONG).show();
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

    @Subscribe
    public void updateNewMessageCount(NewMessageCount newMessageCount) {
        // TODO: 10/2/17
        // newMessageCount.getNewBulletinCount
        // newMessageCount.getNewChatCount
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(AppUtils.TAG, "DrawerActivity: new Intent received -> " + intent.toString());
        String fragmentName;
        Fragment fragment = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            // if received from internal notification, when App is running
            fragmentName = bundle.getString(AppUtils.KEY_START_DRAWER_FRAGMENT);
            if (fragmentName != null) {
                switch (fragmentName) {
                    case "CouponsFragment":   // From Coupon notification
                        fragment = new CouponsFragment();

                        // From Onyx internal push
                        break;
                    case "InboxFragment":     // From bulletin internal push
                        fragment = new InboxFragment();
                        break;
                    case "RealtimeChatFragment":
                        fragment = new RealtimeChatFragment();
                        // bundle has the parcelable user data
                        fragment.setArguments(bundle);
                        break;
                }
            }

            Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);

            // Check if selected fragment is already showing
            if (fragment != null && !fragment.getClass().getSimpleName()
                .equals(currentFragment.getClass().getSimpleName())) {

                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, 0)
                    .replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName())
                    .commit();
            }
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showFragment(fragmentList[0]);
        }
    }
}
