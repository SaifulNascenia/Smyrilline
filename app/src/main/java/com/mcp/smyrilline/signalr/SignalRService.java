package com.mcp.smyrilline.signalr;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.model.messaging.ChatListItem;
import com.mcp.smyrilline.model.messaging.ClientStatus;
import com.mcp.smyrilline.model.messaging.MessagePage;
import com.mcp.smyrilline.receiver.NetworkReceiver;
import com.mcp.smyrilline.receiver.SettingsChangeReceiver;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.McpApplication;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Service to connect to SignalR
 */
public class SignalRService extends Service {

    public static final String ACTION_PING = "com.mcp.smyrilline.ACTION_PING";
    public static final String ACTION_CONNECT = "com.mcp.smyrilline.ACTION_CONNECT";
    public static final String ACTION_SHUT_DOWN = "com.mcp.smyrilline.ACTION_SHUT_DOWN";
    public static final String ACTION_APP_SETTINGS_CHANGED = "com.mcp.smyrilline.ACTION_APP_SETTINGS_CHANGED";
    private final IBinder mBinder = new Binder();
    private Handler mHandler = new Handler();
    private SignalRClient mSignalRClient;
    private Context mContext;

    // Broadcast receiver for app language change
    private BroadcastReceiver mSettingsChangeReceiver = new SettingsChangeReceiver();
    private boolean mSettingsChangeReceiverRegistered = false;

    // Broadcast receiver for network change
    private BroadcastReceiver mNetworkChangeReceiver = new NetworkReceiver();
    private boolean mNetworkChangeReceiverRegistered = false;

    public static Intent startIntent(Context context) {
        Intent i = new Intent(context, SignalRService.class);
        i.setAction(ACTION_CONNECT);
        return i;
    }

    public static Intent pingIntent(Context context) {
        Intent intent = new Intent(context, SignalRService.class);
        intent.setAction(ACTION_PING);
        return intent;
    }

    public static Intent closeIntent(Context context) {
        Intent intent = new Intent(context, SignalRService.class);
        intent.setAction(ACTION_SHUT_DOWN);
        return intent;
    }

    public static Intent settingsChangeIntent(Context context) {
        Intent intent = new Intent(context, SignalRService.class);
        intent.setAction(ACTION_APP_SETTINGS_CHANGED);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
/*

        // Register language change receiver
        if (mSettingsChangeReceiver != null) {
            registerReceiver(mSettingsChangeReceiver, new IntentFilter(ACTION_APP_SETTINGS_CHANGED));
            mSettingsChangeReceiverRegistered = true;
        }

        // Register network change receiver
        if (mNetworkChangeReceiver != null) {
            registerReceiver(mNetworkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            mNetworkChangeReceiverRegistered = true;
        }
*/

        Log.i(AppUtils.TAG, "Creating Service " + this.toString());
        McpApplication.registerWithEventBus(this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE))
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SignalRService");
                wakelock.acquire();
                Log.i(AppUtils.TAG, "SignalRService start command");
                if (intent != null)
                    Log.i(AppUtils.TAG, intent.toUri(0));

                // Get signalr client singleton
                mSignalRClient = SignalRClient.getInstance();

                // Connect when intent == null (service restarts - START_STICKY),
                // or PING intent,
                // or CONNECT intent
                if ((intent == null || intent.getAction().equals(ACTION_CONNECT)
                    || intent.getAction().equals(ACTION_PING))
                    && AppUtils.isNetworkAvailable(mContext)) {
                    if (!(mSignalRClient.isConnected() || mSignalRClient.isConnecting()))
                        mSignalRClient.startConnection(null);
                } else if (intent != null && ACTION_APP_SETTINGS_CHANGED
                    .equals(intent.getAction())) {
                    // We need to registerBulletin with socket again with new language param
//            registerBulletinAgain();
                } else if (intent != null && ACTION_SHUT_DOWN
                    .equals(intent.getAction())) {   // SHUT DOWN intent
                    stopSelf();
                }

                wakelock.release();
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // unregister from eventbus first, receive no more events
        EventBus.getDefault().unregister(this);

        Log.i(AppUtils.TAG, "SignalRService: onDestroy");
        // send offline status to server
        clientStatusChange(new ClientStatus(AppUtils.USER_OFFLINE));

        // stop connection
        if (mSignalRClient != null && mSignalRClient.isConnected())
            mSignalRClient.stopConnection();


/*
        if (mSettingsChangeReceiverRegistered) {
            unregisterReceiver(mSettingsChangeReceiver);
            mSettingsChangeReceiverRegistered = false;
        }
        if (mNetworkChangeReceiverRegistered) {
            unregisterReceiver(mNetworkChangeReceiver);
            mNetworkChangeReceiverRegistered = false;
        }*//*
*/
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public synchronized void setListener(BulletinListener listener) {
        if (mSignalRClient != null)
            mSignalRClient.setBulletinListener(listener);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void chatMessageFromClient(ChatListItem chatListItem) {
        Log.i(AppUtils.TAG,
            "SignalRService: chatMessageFromClient -> " + chatListItem.getMessage());
        if (mSignalRClient != null) {
            if (mSignalRClient.isConnected()) {
                mSignalRClient.sendChatMessageToServer(chatListItem);
            } else if (mSignalRClient.isDisconnected()) {
                mSignalRClient.startConnection(chatListItem);
            } else if (mSignalRClient.isConnecting()) {
                mSignalRClient.resetConnection(chatListItem);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void clientStatusChange(ClientStatus status) {
        Log.i(AppUtils.TAG, "SignaRService: received status " + status.getStatus());
        if (mSignalRClient != null) {
            if (mSignalRClient.isConnected()) {
                Log.i(AppUtils.TAG, "SignalRService: signalr isConnected! registering again..");
                // register chat will use the updated status
                mSignalRClient.registerChat();

            } else if (mSignalRClient.isConnecting()) {
                Log.i(AppUtils.TAG, "SignalRService: signalr isConnecting! reseting connection..");
                // do nothing, it will register after connected
            } else {
                // start connection, it will call register after starting
                Log.i(AppUtils.TAG,
                    "SignalRService: signalr isDisconnected! call startConnection..");
                mSignalRClient.startConnection(null);
            }
        } else {
            Log.i(AppUtils.TAG, "SignalRService: signalr is NULL! init & starting..");
            mSignalRClient = SignalRClient.getInstance();
            mSignalRClient.startConnection(null);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void requestMessageListOfSender(MessagePage requestPage) {
        if (mSignalRClient != null && mSignalRClient.isConnected()) {
            mSignalRClient.requestMessageListOfSender(requestPage);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(AppUtils.TAG, "SignalRService: onTaskRemoved");
        // send offline status to server
        EventBus.getDefault().post(new ClientStatus(AppUtils.USER_OFFLINE));

        // stop connection
        if (mSignalRClient != null && mSignalRClient.isConnected())
            mSignalRClient.stopConnection();

        EventBus.getDefault().unregister(this);

        super.onTaskRemoved(rootIntent);
    }

    public class Binder extends android.os.Binder {
        public SignalRService getService() {
            return SignalRService.this;
        }
    }
}
