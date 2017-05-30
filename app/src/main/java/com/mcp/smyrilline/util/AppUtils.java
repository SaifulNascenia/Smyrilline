package com.mcp.smyrilline.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mcp.smyrilline.BuildConfig;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.activity.MainGridActivity;
import com.mcp.smyrilline.model.Bulletin;
import com.onyxbeacon.rest.model.content.Coupon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by raqib on 5/11/17.
 */

public class AppUtils extends MultiDexApplication {

    public static final String START_DRAWER_FRAGMENT = "start_drawer_fragment";
    public static final String TAG = "smyrilline";
    public static final String CONNECTION_OK = "OK";
    // Date time formats
    public static final String TIME_FORMAT_COUPON_EXPIRE = "MMM dd, HH:mm";
    public static final String TIME_FORMAT_ROUTE = "hh:mm a";
    public static final String DATE_FORMAT_ROUTE = "MMM dd (EEE), yyyy";
    public static final String DATE_FORMAT_BULLETIN = "MMM dd";
    public static final String DATE_FORMAT_BULLETIN_DETAIL = "MMM dd, yyyy hh:mm a";
    public static final String TIME_FORMAT_SIGNALR_RECEIVE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DOB = "dd/MM/yyyy";
    // For showing image properly inside webview
    public static final String CSS_FIT_IMAGE_IN_WEBVIEW =
            "<style>"
                    + "img{display:inline; height:auto; max-width:100%;}"
                    + "body{margin:0; padding:0}"
                    + "</style>";
    // Shared preference keys
    public static final String PREF_DUTY_FREE_URL = "dutyFreeURL";
    public static final String PREF_RESTAURANTS_URL = "restaurantsFreeURL";
    public static final String PREF_DESTINATIONS_URL = "destinationsURL";
    public static final String PREF_ABOUT_SHIP_URL = "shipInfoURL";
    public static final String PREF_ABOUT_SHIP_HEADER_INFO = "shipInfoHeaderInfo";
    public static final String PREF_LOCALE = "appLocale";
    public static final String PREF_LOGGED_IN = "isLoggedIn";
    public static final String PREF_COUPON_LIST = "couponsList";
    public static final String PREF_UNSEEN_COUPONS = "unseenCouponPositions";
    public static final String PREF_USED_COUPONS = "usedList";
    public static final String PREF_BULLETIN_LIST = "bulletinsList";
    public static final String PREF_HELP_INFO = "helpInfo";
    public static final String PREF_NO_ENTRY = "noEntry";
    public static final String PREF_CSS_HELP = "css_help";
    // Ticket information
    public static final String PREF_BOOKING_NAME = "bookingName";
    public static final String PREF_BOOKING_NUMBER = "bookingNumber";
    public static final String PREF_CABIN = "cabin";
    public static final String PREF_ADULT = "adult";
    public static final String PREF_CHILD = "child";
    public static final String PREF_CHILD12 = "child12";
    public static final String PREF_CHILD15 = "child15";
    public static final String PREF_INFANT = "infant";
    public static final String PREF_CAR = "car";
    public static final String PREF_ROUTE_LIST = "routeList";
    public static final String PREF_PASSENGER_LIST = "passengerList";
    public static final String PREF_MEAL_LIST = "mealList";
    public static final String PREF_UNREAD_BULLETINS = "unread_bulletins";
    public static final String PREF_UNREAD_COUPONS = "unread_coupons";
    public static final String PREF_SIGNALR_LAST_RECEIVE_TIME = "signalr_disconnect_time";
    public static final String PREF_CUSTOM_CSS_FROM_WP = "custom_css_from_wordpress";
    public static final String PREF_MESSAGE_FILTER_AGE = "message_filter_age";
    public static final String PREF_MESSAGE_FILTER_GENDER = "message_filter_gender";

    // Intent extra keys
    public static final String KEY_ONYX_PUSH = "coupon_push";
    public static final String KEY_COUPON_NOTI = "coupon_notification";
    public static final String KEY_BULLETIN_PUSH = "bulletin_push";
    // Wordpress site params to be appended to URLs
    // for organizing json response e.g. ascending order, list count 100
    public static final String WP_PARAM_ESSENTIALS = "&filter[orderby]=menu_order&filter[order]=asc&per_page=100";
    // Util constants
    public static final String FIRST_RUN = "first_run";
    public static final int GUIDE_SCREEN_PAGE_COUNT = 6;
    public static final String POSITION_COUPON_REMOVED = "coupon_removed_position";
    public static final String ZERO = "0";
    public static final String WP_CUSTOM_FIELDS = "custom_fields";
    public static final String WP_CUSTOM_FIELD_NO_CSS = "no_css";
    private static final String PREF_ANDROID_ID = "androidID";
    private static final String MOBILE_PLATFORM = "Android";
    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 5;
    // Wordpress language param
    public static String WP_PARAM_LANGUAGE;
    // Translatable texts
    public static String ALERT_NO_WIFI;
    public static String ALERT_SERVER_DOWN;
    public static String ALERT_SERVER_TIMEOUT;
    public static String ALERT_DOWNLOADING;
    public static String ALERT_DOWNLOAD_CANCELLED;
    public static String ALERT_ERROR;
    public static String ALERT_DOWNLOAD_FAILED;
    public static String ALERT_FILE_SAVED_TO;
    public static String ALERT_NO_PDF_READER;
    public static String ALERT_TURN_ON_BLUETOOTH;
    public static String LABEL_NEW;
    private Locale locale = null;
    public static String[] fragmentList = {"LoginFragment", "ShipTrackerFragment", "InboxFragment", "DutyFreeFragment",
            "RestaurantsFragment", "DestinationsFragment", "CouponsFragment", "SettingsFragment", "InfoFragment",
            "HelpFragment",
    };

    private static SharedPreferences mSharedPref;
    public static Context mContext;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Check Http connection for a given url
     *
     * @param context   The context of the alert
     * @param domainURL The url to check
     * @return The status message
     */
    public static String isDomainAvailable(final Context context, String domainURL) {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL(domainURL).openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(CONNECTION_TIMEOUT_MS);
            urlc.setReadTimeout(CONNECTION_TIMEOUT_MS);
            urlc.connect();

            // checking response code
            switch (urlc.getResponseCode()) {
                case (200):
                    return CONNECTION_OK;
                default:
                    return mContext.getResources().getString(R.string.alert_server_down);
            }
        } catch (SocketTimeoutException e) {
            return mContext.getResources().getString(R.string.alert_server_timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return mContext.getResources().getString(R.string.alert_server_down);
        } catch (Exception e) {
            e.printStackTrace();
            return mContext.getResources().getString(R.string.alert_error);
        }
    }

    /**
     * Method to show Alert dialog with custom message
     *
     * @param context The context of the Alert
     * @param message The message to show
     */
    public static void showAlertDialog(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
/*
    *//**
     * Method to show Alert dialog with custom layout & message
     *
     * @param context  The context of the Alert
     * @param message  The message to show
     * @param layoutID The id of the layout
     *//*
    public static void showAlertDialogWithLayout(Context context, String message, int layoutID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final View dialogView = inflater.inflate(layoutID, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();

        TextView tvPushMessage = (TextView) dialogView.findViewById(R.id.tvAlertMessage);
        tvPushMessage.setText(message);

        Button btnPushOk = (Button) dialogView.findViewById(R.id.btnAlertOk);
        btnPushOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

 */

    /**
     * Method to show toast in UI thread
     *
     * @param message The message to show
     */
    public static void showToastInMainUI(final Context context, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removing bottom space inside webview for jelly bean or below
     * http://stackoverflow.com/questions/12930987
     *
     * @param webView
     */
    public static void removeBottomSpaceFromWebView(WebView webView) {
        if (android.os.Build.VERSION.SDK_INT < 19) {
            webView.loadUrl("");
            webView.loadUrl("");
        }
    }


    /**
     * Update translations of strings used in code
     * Called when app started, language changed in-app, and in-phone
     */
    public static void updateTextTranslations() {
        WP_PARAM_LANGUAGE = mContext.getResources().getString(R.string.wp_language_param);
        ALERT_TURN_ON_BLUETOOTH = mContext.getResources().getString(R.string.alert_turn_on_bluetooth);
        ALERT_NO_WIFI = mContext.getResources().getString(R.string.alert_no_wifi);
        ALERT_SERVER_DOWN = mContext.getResources().getString(R.string.alert_server_down);
        ALERT_SERVER_TIMEOUT = mContext.getResources().getString(R.string.alert_server_timeout);
        ALERT_DOWNLOADING = mContext.getResources().getString(R.string.alert_downloading);
        ALERT_DOWNLOAD_CANCELLED = mContext.getResources().getString(R.string.alert_download_cancelled);
        ALERT_FILE_SAVED_TO = mContext.getResources().getString(R.string.alert_file_saved_to);
        ALERT_NO_PDF_READER = mContext.getResources().getString(R.string.alert_no_pdf_reader);
        ALERT_DOWNLOAD_FAILED = mContext.getResources().getString(R.string.alert_download_failed);
        ALERT_ERROR = mContext.getResources().getString(R.string.alert_error);
        LABEL_NEW = mContext.getResources().getString(R.string.new_label);
    }

    public static String convertDateFormat(String givenString, String givenFormat, String requiredFormat) {
        SimpleDateFormat givenDateFormat = new SimpleDateFormat(givenFormat);
        SimpleDateFormat ourDateFormat = new SimpleDateFormat(requiredFormat);

        String convertedString = null;
        try {
            Date date = givenDateFormat.parse(givenString);
            convertedString = ourDateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedString;
    }

    /**
     * Generates a notification in the notification tray
     *
     * @param context       the context
     * @param title         the title of notification
     * @param text          the text of notification
     * @param iconID        the drawable ID of the notification icon
     * @param startFragment the fragment to start when pressed on the notification
     */
    public static void generateNotification(Context context, String title, String text, int iconID, String startFragment) {

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibratePattern = {500, 500, 500, 500};

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setVibrate(vibratePattern)
                        .setSound(notificationSound);

        Intent resultIntent = new Intent(context, DrawerActivity.class);
        resultIntent.putExtra(START_DRAWER_FRAGMENT, startFragment);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int notificationId = 1;
        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, builder.build());
    }

    /**
     * Store list in shared preference
     *
     * @param list The list to store
     * @param key  The key for shared pref
     */
    public static synchronized void saveListInSharedPref(ArrayList list, String key) {
        Gson gson = new Gson();
        mSharedPref.edit().putString(key, gson.toJson(list)).apply();
    }

    public static synchronized void printCouponIdList(ArrayList<Long> list) {
        for (int i = 0; i < list.size(); i++)
            Log.i(TAG, String.format("list (%d) -> %s", i, String.valueOf(list.get(i))));
    }

    public static synchronized void printCouponList(ArrayList<Coupon> list) {
        for (int i = 0; i < list.size(); i++)
            Log.i(TAG, String.format("list (%d) -> %s", i, String.valueOf(list.get(i).couponId)));
    }

    public static synchronized void printBulletinList(ArrayList<Bulletin> list) {
        for (int i = 0; i < list.size(); i++)
            Log.i(TAG, "list (" + i + ") -> " + String.valueOf(list.get(i).getTitle()) + ", seen: " + list.get(i).isSeen());
    }

    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getAndroidID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String androidID = preferences.getString(PREF_ANDROID_ID, "");

        if (androidID.isEmpty() || androidID == null) {
            androidID = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            preferences.edit().putString(PREF_ANDROID_ID, androidID).apply();
        }
        return androidID;
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName;

        if (model.startsWith(manufacturer)) {
            deviceName = capitalize(model);
        } else {
            deviceName = capitalize(manufacturer) + " " + model;
        }

        return MOBILE_PLATFORM + " (" + deviceName + ")";
    }

    private static String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }

        char first = string.charAt(0);

        if (Character.isUpperCase(first)) {
            return string;
        } else {
            return Character.toUpperCase(first) + string.substring(1);
        }
    }

    public static boolean isBluetoothAvailable() {
        if (mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            BluetoothManager e = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = e.getAdapter();
            return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    public static void updateUnreadBulletinCount(int increment) {
        int currentCount = mSharedPref.getInt(PREF_UNREAD_BULLETINS, 0);
        mSharedPref.edit().putInt(PREF_UNREAD_BULLETINS, currentCount + increment).apply();

    }

    /**
     * Return the saved message filter from shared preference
     *
     * @param sharedPrefKey
     * @param defaultVal
     * @return saved filter value
     */
    public static String getSavedMessageFilter(String sharedPrefKey, String defaultVal) {
        String filterGroup = mSharedPref.getString(sharedPrefKey, defaultVal);
        // Remove ending comma
        if (filterGroup.endsWith(","))
            filterGroup = filterGroup.substring(0, filterGroup.length() - 1);
        return filterGroup;
    }

    public static String getStringFromJsonObject(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.isNull(key) || jsonObject.getString(key).isEmpty())
            return null;
        else
            return jsonObject.getString(key);
    }

    public static String getCurrentAppLanguage() {
        return mSharedPref.getString(PREF_LOCALE, "en");
    }

    /**
     * Returns the current system time in give format
     *
     * @param requiredDateFormat
     * @return date string
     */
    public static String getCurrentDateAsString(String requiredDateFormat) {
        SimpleDateFormat ourDateFormat = new SimpleDateFormat(requiredDateFormat);
        return ourDateFormat.format(new Date());
    }

    /**
     * Returns current system time in GMT
     *
     * @param requiredTimeFormat
     * @return
     */
    public static String getCurrentTimeInGmt(String requiredTimeFormat) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(requiredTimeFormat);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(new Date());
    }

    /**
     * Get the pdf link
     * <p>
     * eg.  custom_fields:
     * {
     * file_all:[
     * "http://....."
     * ],
     * file_en:[
     * "http://....."
     * ],
     * file_de:[
     * "http://....."
     * ]
     * }
     * <p>
     * 'file_<lang>' will take precedence, otherwise check 'file_all'
     *
     * @param rootJson
     * @return the pdf url
     * @throws JSONException
     */
    public static String getPdfLink(JSONObject rootJson) throws JSONException {

        if (rootJson.isNull("custom_fields")
                || rootJson.get("custom_fields") instanceof JSONArray) // incompatible type
            return null;
        else {

            JSONObject custom_fields = rootJson.getJSONObject("custom_fields");

            // Check 'file_<lang>'
            String pdfLink = getValidCustomField(
                    custom_fields,
                    "file_" + getCurrentAppLanguage());
            if (pdfLink != null)
                return pdfLink;

            // Check 'file_all'
            pdfLink = getValidCustomField(custom_fields, "file_all");
            if (pdfLink != null)
                return pdfLink;

            return null;
        }
    }

    /**
     * Check for null, empty array, empty value.
     */
    public static String getValidCustomField(JSONObject custom_fields, String fieldName)
            throws JSONException {
        if (custom_fields.isNull(fieldName)
                || custom_fields.getJSONArray(fieldName).length() == 0
                || custom_fields.getJSONArray(fieldName).getString(0).isEmpty())
            return null;
        else
            return custom_fields.getJSONArray(fieldName).getString(0);
    }

    /**
     * To check if "no_css" is applied
     * By default css will be applied to all pages
     *
     * @param rootJson to parse for "no_css" custom field
     * @return
     * @throws JSONException
     */
    public static boolean isNoCss(JSONObject rootJson) throws JSONException {
        // if there's no custom field, keep default i.e. return false
        if (rootJson.isNull(WP_CUSTOM_FIELDS)
                || rootJson.get(WP_CUSTOM_FIELDS) instanceof JSONArray) {
            return false;
        } else
            // if "no_css" is not null, that means it exists
            return !rootJson
                    .getJSONObject(WP_CUSTOM_FIELDS)
                    .isNull(WP_CUSTOM_FIELD_NO_CSS);
    }

    /**
     * Method will load the content in a webview
     *
     * @param webView
     * @param body     the content to load
     * @param applyCss if custom css from wordpress will be applied
     */
    public static void showContentInWebview(WebView webView, String body,
                                            boolean applyCss) {
        // remove bottom space
        removeBottomSpaceFromWebView(webView);
        body = CSS_FIT_IMAGE_IN_WEBVIEW + body;

        // apply css
        if (applyCss)
            body = mSharedPref.getString(PREF_CUSTOM_CSS_FROM_WP, "") + body;
        webView.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
    }

    /**
     * Method to clear the shared preference if the version code is <= 4
     * because there are some Gson implementation changes for saved values
     */
    void clearSharedPrefIfNecessary() {
        int versionCode = 0;
        try {
            // Get the current version code
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            SharedPreferences.Editor editor = mSharedPref.edit();
            if (mSharedPref.getInt("lastUpdate", 0) != versionCode) {
                // Clear everything
                editor.clear();
                // Save current version
                editor.putInt("lastUpdate", versionCode);
                editor.apply();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            // update failed, or cancelled
        }
    }

    /**
     * Method to hide the soft input keyboard
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        if (null == activity) {
            return;
        }

        View view = activity.getCurrentFocus();
        if (null == view) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("AppUtils", "onCreate()");
        mContext = getApplicationContext();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        clearSharedPrefIfNecessary();
        Configuration config = getBaseContext().getResources().getConfiguration();

        // Get saved Locale from previous selection
        String lang = mSharedPref.getString(PREF_LOCALE, "");
        Log.i("AppUtils", "onCreate() - Getting saved locale -> " + lang);

        if (!"".equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        // Update code strings
        updateTextTranslations();
    }

    /**
     * Called when phone config is changed
     * in our case, were handling Locale change
     *
     * @param newConfig The new configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Save the new locale
        Locale saveLocale = new Locale(newConfig.locale.getLanguage());

        // Save it in memory
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSharedPref.edit().putString(PREF_LOCALE, saveLocale.getLanguage()).apply();
        Log.i("AppUtils", "onConfigChange() - Saving locale -> " + saveLocale.getLanguage());

        // Update texts used in code
        updateTextTranslations();
    }


}