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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.blogc.android.views.ExpandableTextView;
import com.google.gson.Gson;
import com.mcp.smyrilline.BuildConfig;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.fragment.ProfileDialogFragment;
import com.mcp.smyrilline.fragment.RealtimeChatFragment;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.model.messaging.ChatUserClientModel;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.model.messaging.ClientStatus;
import com.onyxbeacon.rest.model.content.Coupon;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by raqib on 5/11/17.
 */

public class AppUtils {

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://192.168.0.104/AndroidFileUpload/fileUpload.php";

    // Directory name to store captured images and videos
    public static final String MEDIA_DIRECTORY_NAME = "smyrilline_media";
    public static final int UPLOAD_MEDIA_TYPE_IMAGE = 1;
    public static final int UPLOAD_MEDIA_TYPE_VIDEO = 2;

    // Bluetooth enable request code
    public static final int REQUEST_ENABLE_BT = 101;
    // Camera activity request codes
    public static final int REQUEST_CAMERA_CAPTURE_IMAGE = 102;

    public static final String TIME_FORMAT_CHAT_MESSAGE = "h:mma";
    public static final String KEY_START_DRAWER_FRAGMENT = "start_drawer_fragment";
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
    public static final String PREF_LOCALE = "appLocale";
    public static final String PREF_LOGGED_IN = "isLoggedIn";
    public static final String PREF_COUPON_LIST = "couponsList";
    public static final String PREF_UNSEEN_COUPONS = "unseenCouponPositions";
    public static final String PREF_USED_COUPONS = "usedList";
    public static final String PREF_BULLETIN_LIST = "bulletinsList";
    public static final String PREF_HELP_INFO = "helpInfo";
    public static final String PREF_NO_ENTRY = "noEntry";
    public static final String PREF_CSS_HELP = "css_help";
    public static final String PREF_SHIP_INFO = "ship_info";
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
    // Wordpress site params to be appended to URLs
    // for organizing json response e.g. ascending order, list count 100
    public static final String WP_PARAM_ESSENTIALS = "&filter[orderby]=menu_order&filter[order]=asc&per_page=100";
    // Util constants
    public static final String FIRST_RUN = "first_run";
    public static final String POSITION_COUPON_REMOVED = "coupon_removed_position";
    public static final String ZERO = "0";
    public static final String WP_CUSTOM_FIELDS = "custom_fields";
    public static final String WP_CUSTOM_FIELD_NO_CSS = "no_css";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_HEADER = "item_header";
    public static final String ITEM_SUBHEADER = "item_subheader";
    public static final String ITEM_DESCRIPTION = "item_description";
    public static final String ITEM_IMAGE = "item_image";
    public static final String KEY_SAVED_USER_LIST = "chat_user_list";
    public static final String KEY_SAVED_ADMIN_LIST = "chat_admin_list";
    public static final String CHAT_REMOTE_USER = "chat_remote_user";
    public static final int MESSAGE_SENDING = 0;
    public static final int MESSAGE_SENDING_FAILED = 1;
    public static final int MESSAGE_SENDING_SUCCESS = 2;
    public static final String PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT = "new_chat_message_count";
    // message
    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 0;
    public static final String PREF_MY_NAME = "my_name";
    public static final String PREF_MY_BOOKING_NO = "my_booking";
    public static final String PREF_MY_DESCRIPTION = "my_description";
    public static final String PREF_MY_COUNTRY = "my_country";
    public static final String PREF_MY_GENDER = "my_gender";
    public static final String PREF_MY_IMAGE_URL = "my_image_url";
    public static final String PREF_MY_STATUS = "my_status";
    public static final String PREF_MY_VISIBILITY = "my_visibility";
    public static final int VISIBLE_TO_BOOKING = 1;
    public static final int VISIBLE_TO_ALL = 2;
    public static final int VISIBLE_TO_NONE = 3;
    public static final int CHAT_USER_LIST_MAX_MESSAGE_COUNT = 9;
    public static final int GRID_MENU_MAX_MESSSAGE_COUNT = 9;
    public static final int MESSAGE_LOAD_ITEM_COUNT = 10;
    public static final int REQUEST_ERROR = 0;
    public static final int REQUEST_SUCCESS = 1;
    public static final String KEY_SAVED_MESSAGE_LIST = "key_saved_message_list";
    public static final String PREF_PROFILE_VERIFIED = "is_profile_saved";
    public static final int AVATAR_SIZE_PX;
    private static final String PREF_DEVICE_ID = "androidID";
    private static final String MOBILE_PLATFORM = "Android";
    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 5;
    private static final String KEY_NEW_CHAT_MESSAGE_COUNT = "new_chat_message_count";
    //bundle key names
    public static String PRODUCT_ID = "product_id";
    public static String PRODUCT_NAME = "product_name";
    public static String PRODUCT_PRICE = "product_price";
    public static String PRODUCT_PRICE_NUMBER = "product_price_number";
    public static String PRODUCT_INFO = "product_info";
    public static String PRODUCT_IMAGE = "product_image";
    public static String CALLED_CLASS_NAME = "CALLED_CLASS_NAME";
    public static String DESTINATION_ID = "destination_id";
    public static String DESTINATION_NAME = "destination_name";
    private static Bundle bundle = new Bundle();

    static {
        AVATAR_SIZE_PX = McpApplication.instance().context().getResources()
            .getDimensionPixelSize(R.dimen.profile_pic_size);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Check Http connection for a given url
     *
     * @param context The context of the alert
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
                    return context.getResources().getString(R.string.alert_server_down);
            }
        } catch (SocketTimeoutException e) {
            return context.getResources().getString(R.string.alert_server_timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return context.getResources().getString(R.string.alert_server_down);
        } catch (Exception e) {
            e.printStackTrace();
            return context.getResources().getString(R.string.alert_error);
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
            .setPositiveButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

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
     */
    public static void removeBottomSpaceFromWebView(WebView webView) {
        if (android.os.Build.VERSION.SDK_INT < 19) {
            webView.loadUrl("");
            webView.loadUrl("");
        }
    }

    /**
     * Convert given timestamp to required format
     */
    public static String convertDateFormat(String givenString, String givenFormat,
        String requiredFormat) {
        Context context = McpApplication.instance().context();
        SimpleDateFormat givenDateFormat = new SimpleDateFormat(givenFormat, Locale.US);
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat(requiredFormat, Locale.US);
        // change AM/PM to am/pm
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(
            new String[]{context.getString(R.string.am), context.getString(R.string.pm)});
        requiredDateFormat.setDateFormatSymbols(symbols);

        String convertedString = null;
        try {
            Date date = givenDateFormat.parse(givenString);
            convertedString = requiredDateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedString;
    }

    /**
     * Generates a notification in the notification tray
     *
     * @param context the context
     * @param title the title of notification
     * @param text the text of notification
     * @param startFragment the fragment to start when pressed on the notification
     */
    public static void generateNotification(Context context, String title, String text,
        String startFragment, ChatUserServerModel chatUserData) {

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibratePattern = {500, 500, 500, 500};

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setVibrate(vibratePattern)
                .setSound(notificationSound);

        Intent resultIntent = new Intent(context, DrawerActivity.class);
        resultIntent.putExtra(KEY_START_DRAWER_FRAGMENT, startFragment);

        // if this is chat notification, add chat user data
        if (startFragment.equals(RealtimeChatFragment.class.getSimpleName())) {
            resultIntent.putExtra(CHAT_REMOTE_USER, (Parcelable) chatUserData);
        }

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
        NotificationManager notifyMgr = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, builder.build());
    }

    /**
     * Store list in shared preference
     *
     * @param list The list to store
     * @param key The key for shared pref
     */
    public static synchronized void saveListInSharedPref(ArrayList list, String key) {
        Gson gson = new Gson();
        McpApplication.instance().sharedPref().edit().putString(key, gson.toJson(list)).apply();
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
            Log.i(TAG,
                "list (" + i + ") -> " + String.valueOf(list.get(i).getTitle()) + ", seen: "
                    + list
                    .get(i).isSeen());
    }

    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getDeviceId() {
        SharedPreferences preferences = McpApplication.instance().sharedPref();
        String androidID = preferences.getString(PREF_DEVICE_ID, "");

        if (androidID.isEmpty() || androidID == null) {
            androidID = Settings.Secure
                .getString(McpApplication.instance().context().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            preferences.edit().putString(PREF_DEVICE_ID, androidID).apply();
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
            deviceName = capitalizeFirstChar(model);
        } else {
            deviceName = capitalizeFirstChar(manufacturer) + " " + model;
        }

        return MOBILE_PLATFORM + " (" + deviceName + ")";
    }

    /**
     * Capitalize a given string
     */
    public static String capitalizeFirstChar(String string) {
        if (string == null)
            return null;

        if (string.isEmpty())
            return "";

        char first = string.charAt(0);

        if (Character.isUpperCase(first)) {
            return string;
        } else {
            return Character.toUpperCase(first) + string.substring(1);
        }
    }

    public static boolean isBluetoothAvailable(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            BluetoothManager e = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = e.getAdapter();
            return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    public static void updateUnreadBulletinCount(int increment) {
        SharedPreferences sharedPref = McpApplication.instance().sharedPref();
        int currentCount = sharedPref.getInt(PREF_UNREAD_BULLETINS, 0);
        sharedPref.edit().putInt(PREF_UNREAD_BULLETINS, currentCount + increment).apply();
    }

    // save the new message count with respect to user id
    public static void updateNewChatMessageCountOfUser(String userDeviceId, int increment) {
        SharedPreferences sharedPref = McpApplication.instance().sharedPref();
        int currentCount = sharedPref.getInt(KEY_NEW_CHAT_MESSAGE_COUNT, 0);
        sharedPref.edit()
            .putInt(KEY_NEW_CHAT_MESSAGE_COUNT + "_" + userDeviceId, currentCount + increment)
            .apply();
    }

    /**
     * Return the saved message filter from shared preference
     *
     * @return saved filter value
     */
    public static String getSavedMessageFilter(String sharedPrefKey, String defaultVal) {
        String filterGroup = McpApplication.instance().sharedPref()
            .getString(sharedPrefKey, defaultVal);
        // Remove ending comma
        if (filterGroup.endsWith(","))
            filterGroup = filterGroup.substring(0, filterGroup.length() - 1);
        return filterGroup;
    }

    /**
     * Return value of given key from a json object, null otherwise
     */
    public static String getStringFromJsonObject(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.length() == 0 || jsonObject.isNull(key) || jsonObject.getString(key)
                .isEmpty())
                return null;
            else
                return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentAppLanguage() {
        return McpApplication.instance().sharedPref().getString(PREF_LOCALE, "en");
    }

    /**
     * Returns the current system time in give format
     *
     * @return date string
     */
    public static String getCurrentDateAsString(String requiredFormat) {
        Context context = McpApplication.instance().context();
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat(requiredFormat, Locale.US);
        // change AM/PM to am/pm
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(
            new String[]{context.getString(R.string.am), context.getString(R.string.pm)});
        requiredDateFormat.setDateFormatSymbols(symbols);
        return requiredDateFormat.format(new Date());
    }

    /**
     * Returns current system time in GMT
     */
    public static String getCurrentTimeInGmt(String requiredTimeFormat) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(requiredTimeFormat, Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(new Date());
    }

    /**
     * Returns give long millisecond time in required date format
     */
    public static String convertMillisToFormat(long longMillis, String requiredTimeFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(requiredTimeFormat, Locale.US);
        return dateFormat.format(new Date(longMillis));
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
     * @return the pdf url
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
     * @param body the content to load
     * @param applyCss if custom css from wordpress will be applied
     */
    public static void showContentInWebview(WebView webView, String body,
        boolean applyCss) {
        // remove bottom space
        removeBottomSpaceFromWebView(webView);
        body = CSS_FIT_IMAGE_IN_WEBVIEW + body;

        // apply css
        if (applyCss)
            body = McpApplication.instance().sharedPref().getString(PREF_CUSTOM_CSS_FROM_WP, "")
                + body;
        webView.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
    }

    /**
     * Method to hide the soft input keyboard
     */
    public static void hideKeyboard(Activity activity) {
        if (null == activity) {
            return;
        }

        View view = activity.getCurrentFocus();
        if (null == view) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) activity
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp) {
        Resources resources = McpApplication.instance().context().getResources();
        return Math
            .round(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    resources.getDisplayMetrics()));
    }

    public static Bundle getBundleObj() {
        return bundle;
    }

    /**
     * Handle given texview expansion, and show/hide 'more' or 'less'
     */
    public static void handleExpandableTextView(final ExpandableTextView tvExpandableDescription,
        final TextView tvDescriptionMore) {
        ViewTreeObserver vto = tvExpandableDescription.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    tvExpandableDescription.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    tvExpandableDescription.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
                }

                Layout layout = tvExpandableDescription.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (lines > 0) {
                        int ellipsisCount = layout.getEllipsisCount(lines - 1);
                        if (ellipsisCount > 0) {
                            Log.i("totaltext", "elip\n");
                            tvDescriptionMore.setVisibility(View.VISIBLE);
                        } else {
                            Log.i("totaltext", "not elip\n");
                        }
                    }
                }
            }
        });

        tvExpandableDescription.setAnimationDuration(500L);
        tvExpandableDescription.setInterpolator(new OvershootInterpolator());

        tvDescriptionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvExpandableDescription.isExpanded()) {
                    tvExpandableDescription.collapse();
                    tvDescriptionMore.setText(R.string.more);
                } else {
                    tvExpandableDescription.expand();
                    tvDescriptionMore.setText(R.string.less);
                }
            }
        });
    }

    public static void setText(TextView textView, String text) {
        if (text != null && !text.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else
            textView.setVisibility(View.GONE);
    }

    /**
     * Show the view for no connection available
     */
    public static void showNoConnectionView(final Activity rootActivity, final Fragment fragment,
        View loadingProgressView, View noConnectionView, Toolbar toolbar) {
        loadingProgressView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(rootActivity, R.color.transparent));
        Button retryBtn = (Button) noConnectionView.findViewById(R.id.retry_connect);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart fragment
                FragmentTransaction ft = ((DrawerActivity) rootActivity).getSupportFragmentManager()
                    .beginTransaction();
                ft.detach(fragment).attach(fragment).commit();
            }
        });
    }

    /**
     * Show the view for no connection available with the given extra toolbar
     */
    public static void showNoConnectionViewWithExtraToolbar(final Activity rootActivity,
        final Fragment fragment, View loadingProgressView, View coordinatorLayout,
        View noConnectionView, Toolbar noConnectionToolbar,
        String toolbarTitle) {
        loadingProgressView.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
        noConnectionToolbar.setVisibility(View.VISIBLE);
        noConnectionToolbar.setTitle(toolbarTitle);
        noConnectionToolbar.setBackgroundColor(
            ContextCompat.getColor(rootActivity, R.color.transparent));
        ((DrawerActivity) rootActivity).setToolbarAndToggle(noConnectionToolbar);
        Button retryBtn = (Button) noConnectionView.findViewById(R.id.retry_connect);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // restart fragment
                FragmentTransaction ft = ((DrawerActivity) rootActivity).getSupportFragmentManager()
                    .beginTransaction();
                ft.detach(fragment).attach(fragment).commit();
            }
        });
    }

    public static void hideNoConnectionView(View loadingProgressView, View noConnectionView) {
        loadingProgressView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
    }

    public static void restartFragment(Activity rootActivity, Fragment fragment) {
        FragmentTransaction ft = ((DrawerActivity) rootActivity).getSupportFragmentManager()
            .beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }

    /**
     * Method to clear the shared preference if the version code is <= 4
     * because there are some Gson implementation changes for saved values
     */
    public static void clearSharedPrefIfNecessary(Context context) {
        int versionCode = 0;
        try {
            // Get the current version code
            versionCode = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0).versionCode;
            SharedPreferences sharedPreferences = McpApplication.instance().sharedPref();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.getInt("lastUpdate", 0) != versionCode) {
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

    public static String convertMillisToLocalTimeZoneInFormat(long sendTime,
        String requiredTimeFormat) {
        // get local timezone offset with UTC + daylight savings
        int gmtOffset =
            TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        sendTime = sendTime + gmtOffset;
        // convert millis to required format
        SimpleDateFormat dateFormat = new SimpleDateFormat(requiredTimeFormat);
        return dateFormat.format(new Date(sendTime));
    }

    public static void setClientOnlineStatus() {
        Log.i(TAG, "setClientOnlineStatus()");
        // save status online
        McpApplication.instance().sharedPref().edit().
            putInt(AppUtils.PREF_MY_STATUS, AppUtils.USER_ONLINE)
            .apply();
        // send status update event to service
        Log.i(TAG, "sending client ONLINE event");
        EventBus.getDefault().post(new ClientStatus(AppUtils.USER_ONLINE));
    }

    public static void setClientOfflineStatus() {
        Log.i(TAG, "setClientOfflineStatus()");
        // save status offline
        McpApplication.instance().sharedPref().edit().
            putInt(AppUtils.PREF_MY_STATUS, AppUtils.USER_OFFLINE)
            .apply();
        // send status event offline
        Log.i(TAG, "sending client OFFLINE event");
        EventBus.getDefault().post(new ClientStatus(AppUtils.USER_OFFLINE));
    }

    public static int isUserOnline() {
        return McpApplication.instance().sharedPref().
            getInt(AppUtils.PREF_MY_STATUS, AppUtils.USER_OFFLINE);
    }

    // check if currently chatting with given user
    public static boolean isCurrentlyChattingWithUser(String senderDeviceId) {
        return RealtimeChatFragment.getCurrentRemoteUser() != null
            && RealtimeChatFragment.getCurrentRemoteUser().getDeviceId()
            .equals(senderDeviceId);
    }

    /**
     * Show/hide & update the counter label
     *
     * @param tvCounterLabel the counter textview
     * @param newMessageCount the value to set
     * @param maxCount the maximum value after which '+' will be appended
     */
    public static void updateCounterLabel(TextView tvCounterLabel, int newMessageCount,
        int maxCount) {
        if (newMessageCount > 0) {
            tvCounterLabel.setVisibility(View.VISIBLE);
            if (newMessageCount > maxCount)
                tvCounterLabel.setText(maxCount + "+");
            else
                tvCounterLabel.setText(String.valueOf(newMessageCount));
        } else
            tvCounterLabel.setVisibility(View.GONE);
    }

    // update the total message count, by deducting given value
    public static void updateTotalMessageCount(int deductCount) {
        SharedPreferences sharedPref = McpApplication.instance().sharedPref();
        int totalNewMessageCount = sharedPref
            .getInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT, 0);
        if (totalNewMessageCount > 0) {
            sharedPref.edit().putInt(AppUtils.PREF_TOTAL_NEW_CHAT_MESSAGE_COUNT,
                totalNewMessageCount - deductCount).apply();
        }
    }

    // Reset the new message count of the user to 0
    // also adjust the total new message count
    public static void resetNewCountOfUser(String remoteUserId) {
        ArrayList<ChatUserServerModel> savedUserList = (ArrayList<ChatUserServerModel>) InternalStorage
            .readObject(McpApplication.instance().context(), AppUtils.KEY_SAVED_USER_LIST);

        for (ChatUserServerModel user : savedUserList) {
            if (user.getDeviceId().equals(remoteUserId)) {
                int currentCount = user.getNewMessageCount();
                user.setNewMessageCount(0);
                // deduct from the total message count and save
                AppUtils.updateTotalMessageCount(currentCount);
                break;
            }
        }
        // save updated user
        InternalStorage.writeObject(McpApplication.instance().context(),
            AppUtils.KEY_SAVED_USER_LIST, savedUserList);
    }

    // disable input in edittext and change background
    public static void disableInputText(EditText editText, Drawable editTextBackground) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        if (editTextBackground != null) {
            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                editText.setBackgroundDrawable(editTextBackground);
            } else {
                editText.setBackground(editTextBackground);
            }
        }
    }

    // disable input in edittext and change background
    public static void enableInputText(EditText editText, Drawable editTextBackground) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        if (editTextBackground != null) {
            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                editText.setBackgroundDrawable(editTextBackground);
            } else {
                editText.setBackground(editTextBackground);
            }
        }
    }

    public static void showAlertForVerfiyProfile(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
            .setTitle(context.getString(R.string.chat_profile))
            .setMessage(context.getString(R.string.please_verify_your_profile)
                + "\n\n"
                + AppUtils.getSpannedText(context.getString(R.string.you_can_review_profile_from)))
            .setPositiveButton(context.getString(R.string.view_profile),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {

                        // close
                        dialog.dismiss();

                        // show the profile dialog
                        showProfileDialog(context);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static void showProfileDialog(Context context) {
        SharedPreferences sharedPref = McpApplication.instance()
            .sharedPref();
        ProfileDialogFragment profileDialogFragment = ProfileDialogFragment
            .newInstance(
                new ChatUserClientModel(AppUtils.getDeviceId(),
                    sharedPref
                        .getInt(AppUtils.PREF_MY_BOOKING_NO,
                            0),    // booking no.
                    sharedPref.getString(  // name
                        AppUtils.PREF_MY_NAME, null),
                    sharedPref.getString(
                        AppUtils.PREF_MY_IMAGE_URL,
                        null), // image url
                    sharedPref.getString(
                        AppUtils.PREF_MY_DESCRIPTION,
                        null), // description
                    sharedPref.getString(
                        AppUtils.PREF_MY_COUNTRY,
                        null), // country
                    sharedPref.getString(
                        AppUtils.PREF_MY_GENDER,
                        null),  // gender
                    sharedPref
                        .getInt(AppUtils.PREF_MY_STATUS,
                            AppUtils.USER_ONLINE),
                    // online status
                    sharedPref
                        .getInt(AppUtils.PREF_MY_VISIBILITY,
                            AppUtils.VISIBLE_TO_ALL)));

        profileDialogFragment.show(((DrawerActivity) context).getSupportFragmentManager(),
            profileDialogFragment.getClass().getSimpleName());
    }

    /**
     * Checking device has camera hardware or not
     */
    public static boolean isDeviceSupportCamera() {
        // this device has a camera
// no camera on this device
        return McpApplication.instance().context().getPackageManager().hasSystemFeature(
            PackageManager.FEATURE_CAMERA);
    }

    /**
     * Creating file uri to store image/video
     */
    public static Uri getOutputMediaFileUri(int type) {
        File mediaFile = getOutputMediaFile(type);
        if (mediaFile == null)
            return null;
        else
            return Uri.fromFile(mediaFile);
    }

    /**
     * returning image / video
     * file name format: "IMG_yyyymmdd_hhmmss.jpg"
     */
    public static File getOutputMediaFile(int type) {

        // internal phone memory location
        File mediaStorageDir = new File(
            Environment
                .getExternalStorageDirectory(),
            MEDIA_DIRECTORY_NAME);

        // first check permission
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            externalStorageAvailable = externalStorageWriteable = false;
        }

        Log.i(AppUtils.TAG, String
            .format("External storage READABLE %s, WRITABLE %s", externalStorageAvailable,
                externalStorageWriteable));

        // ====== END ====== //

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create directory: "
                    + MEDIA_DIRECTORY_NAME);
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == UPLOAD_MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        } else if (type == UPLOAD_MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public enum RequestType {SERVER_CONNECT, REQUEST_MESSAGE_LIST, SEND_MESSAGE}

    public enum RequestStatus {SUCCESS, FAIL}

    public enum MsgSendingStatus {SENDING, SENT, FAILED}
}