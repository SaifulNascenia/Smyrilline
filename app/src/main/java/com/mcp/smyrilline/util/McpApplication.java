package com.mcp.smyrilline.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.signalr.SignalRService;
import io.fabric.sdk.android.Fabric;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by raqib on 5/4/17.
 */

public class McpApplication extends MultiDexApplication {

    private static McpApplication mInstance;
    private static EventBus eventBus;
    public ServiceConnection mSignalRServiceConnection;
    public boolean mSignalRServiceBound;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private Locale locale = null;
    private SignalRService mSignalRService;
    private boolean userOnline;

    public static McpApplication instance() {
        if (mInstance == null) {
            synchronized (McpApplication.class) {
                if (mInstance == null) {
                    mInstance = new McpApplication();
                }
            }
        }
        return mInstance;
    }

    public static void registerWithEventBus(Object subscriber) {
        if (!eventBus.isRegistered(subscriber)) {
            eventBus.register(subscriber);
        }
    }

    public SharedPreferences sharedPref() {
        return mSharedPref;
    }

    public Context context() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;       // init the Application singleton first
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        AppUtils.clearSharedPrefIfNecessary(mContext);
        EventBus.builder().eventInheritance(false).installDefaultEventBus();
        eventBus = EventBus.getDefault();
        Configuration config = getBaseContext().getResources().getConfiguration();

        // Get saved Locale from previous selection
        String lang = mSharedPref.getString(AppUtils.PREF_LOCALE, "");
        Log.i("AppUtils", "onCreate() - Getting saved locale -> " + lang);

        if (!"".equals(lang)) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources()
                .updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
/*

        // === sample USER INFO , will take from booking or show profile layout ===//
        String name, imageUrl = null*/
/*"https://image.freepik.com/free-icon/male-user-close-up-shape-for-facebook_318-37635.jpg"*//*
;
        switch (AppUtils.getDeviceId()) {
            case "46c9bda4f3a09321":
                name = "Nexus 5x emu";
//                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Nexus_5X_%28White%29.jpg/800px-Nexus_5X_%28White%29.jpg";
                break;
            case "b0f62cf8a3c5ec0a":
                name = "Nexus 5 emu";
//                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/Nexus_5_Front_View.png/320px-Nexus_5_Front_View.png";
                break;
            case "7165e55842393054":
                name = "Nexus 5 emu (22)";
//                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/Nexus_5_Front_View.png/320px-Nexus_5_Front_View.png";
                break;
            case "f09c185b30833884":
                name = "Nexus 6";
                break;
            case "71a9685b4d21ebb7":
                name = "Primo RX4";
//                imageUrl = "http://techsmn.com/wp-content/uploads/2015/08/Primo-RX4-Price-In-Bangladesh-Hands-On-review.jpg";
                break;
            case "d6152f9f94abd561":
                name = "Nadim";
//                imageUrl = "https://i.imgur.com/51j2kcR.png";
                break;
            case "bc83c45381b231f1":
                name = "Huawei";
//                imageUrl = "https://c.76.my/Malaysia/huawei-y3ii-gold-lua-u22-gloo-1706-30-Gloo@2.jpg";
                break;
            case "565CF988815A1B32":
                name = "Samsung";
                break;
            case "c0ba6252fcab09ce":
                name = "Walton GH6";
//                imageUrl = "http://static.tech99news.com/wp-content/uploads/2016/07/Walton-primo-GH6-1.jpg";
                break;
            default:
                name = "Akash";

        }

        SharedPreferences.Editor editor = McpApplication.instance().sharedPref().edit();
        editor.putString(PREF_MY_NAME, name);
        editor.putInt(PREF_MY_BOOKING_NO, 35261555);
        editor.putString(PREF_MY_DESCRIPTION, getString(R.string.lorem_ipsum));
        editor.putString(PREF_MY_COUNTRY, "Bangladesh");
        editor.putString(PREF_MY_GENDER, "male");
        editor.putString(PREF_MY_IMAGE_URL, imageUrl);
        editor.apply();
*/

        // start the service
        initSignalRService();
//        startService(SignalRService.startIntent(mContext));

        int pic_dimension = (int) getResources().getDimension(R.dimen.chat_user_list_pic_size);
        Log.e(AppUtils.TAG,
            String.format("Chat user list pro pic dimensions -> %d x %d", pic_dimension,
                pic_dimension));
        pic_dimension = (int) getResources().getDimension(R.dimen.chat_messaging_pic_size);
        Log.e(AppUtils.TAG,
            String.format("Chat messaging pro pic dimensions -> %d x %d", pic_dimension,
                pic_dimension));
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
        mSharedPref.edit().putString(AppUtils.PREF_LOCALE, saveLocale.getLanguage()).apply();
        Log.i("AppUtils", "onConfigChange() - Saving locale -> " + saveLocale.getLanguage());
    }

    private void initSignalRService() {
        // Setup service connection
        // it will be called after the activity is bound to the service with bindService()
        mSignalRServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSignalRService = ((SignalRService.Binder) service).getService();
                mSignalRServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSignalRService = null;
                mSignalRServiceBound = false;
            }
        };

        // start the service
        startService(SignalRService.startIntent(mContext));
    }

    public void bindSignalRService(Intent intent) {
        bindService(intent, mSignalRServiceConnection,
            Context.BIND_IMPORTANT);
    }

    public void unbindSignalRService() {
        if (mSignalRServiceBound) {
            unbindService(mSignalRServiceConnection);
            mSignalRServiceBound = false;
        }
    }
}
