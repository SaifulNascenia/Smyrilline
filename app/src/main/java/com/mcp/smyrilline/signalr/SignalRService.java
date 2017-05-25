package com.mcp.smyrilline.signalr;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.listener.BulletinListener;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

/**
 * Service to connect to SignalR
 */
public class SignalRService extends Service {

    public static final String ACTION_PING = "com.mcp.smyrilline.ACTION_PING";
    public static final String ACTION_CONNECT = "com.mcp.smyrilline.ACTION_CONNECT";
    public static final String ACTION_SHUT_DOWN = "com.mcp.smyrilline.ACTION_SHUT_DOWN";
    public static final String ACTION_APP_SETTINGS_CHANGED = "com.mcp.smyrilline.ACTION_APP_SETTINGS_CHANGED";
    public static boolean mShutDown = false;
    private final IBinder mBinder = new Binder();
    private SignalRClient mSignalRClient;
    private Context mContext;

    // Broadcast receiver for app language change
    private boolean mReceiverRegistered = false;
    private BroadcastReceiver mSettingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(AppUtils.TAG, "SignalRService: received broadcast -> " + intent.getAction());
            // We need to register with socket again with new language param [SKIP FOR NOW! Enable when Console supports language]
            if (intent.getAction().equals(ACTION_APP_SETTINGS_CHANGED)) {
                if (mSignalRClient != null)
                    if (mSignalRClient.isConnected()) {
                        mSignalRClient.register();
                    } else if (AppUtils.isNetworkAvailable(mContext)) {
                        mSignalRClient.startConnection();
                    }
            }
        }
    };

    public static Intent startIntent(Context context) {
        Intent i = new Intent(context, SignalRService.class);
        i.setAction(ACTION_CONNECT);
        return i;
    }

    public static Intent pingIntent(Context context) {
        Intent i = new Intent(context, SignalRService.class);
        i.setAction(ACTION_PING);
        return i;
    }

    public static Intent closeIntent(Context context) {
        Intent i = new Intent(context, SignalRService.class);
        i.setAction(ACTION_SHUT_DOWN);
        return i;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mContext = getBaseContext();

        // Register language change receiver
        if (mSettingsChangeReceiver != null) {
            registerReceiver(mSettingsChangeReceiver, new IntentFilter(ACTION_APP_SETTINGS_CHANGED));
            mReceiverRegistered = true;
        }

        Log.i(AppUtils.TAG, "Creating Service " + this.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SignalRService");
        wakelock.acquire();
        Log.i(AppUtils.TAG, "SignalRService start command");
        if (intent != null) Log.i(AppUtils.TAG, intent.toUri(0));

        mShutDown = !AppUtils.isNetworkAvailable(mContext);

        // Get signalr client singleton
        mSignalRClient = SignalRClient.getInstance(mContext);

        // Connect when intent == null (service restarts - START_STICKY),
        // or PING intent,
        // or CONNECT intent
        if ((intent == null || intent.getAction().equals(ACTION_CONNECT)
                || intent.getAction().equals(ACTION_PING))
                && AppUtils.isNetworkAvailable(mContext)) {
            if (!mSignalRClient.isConnected())
                mSignalRClient.startConnection();
        } else if (intent != null && ACTION_SHUT_DOWN.equals(intent.getAction())) {   // SHUT DOWN intent
            mShutDown = true;
            stopSelf();
        }

        wakelock.release();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(AppUtils.TAG, "SignalRService: onDestroy");
        if (mSignalRClient != null && mSignalRClient.isConnected())
            mSignalRClient.stopConnection();

        if (mReceiverRegistered) {
            unregisterReceiver(mSettingsChangeReceiver);
            mReceiverRegistered = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public synchronized void setListener(BulletinListener listener) {
        if (mSignalRClient != null)
            mSignalRClient.setBulletinListener(listener);
    }

    public class Binder extends android.os.Binder {
        public SignalRService getService() {
            return SignalRService.this;
        }
    }
}
