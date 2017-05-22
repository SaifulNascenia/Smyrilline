package com.mcp.smyrilline.signalr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.InboxFragment;
import com.mcp.smyrilline.fragment.SettingsFragment;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.model.Bulletin;
import com.mcp.smyrilline.util.Utils;
import com.mcp.smyrilline.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by raqib on 8/26/16.
 */
public class SignalRClient {

    /* Parameters for server request
     * MUST match server side
     * DO NOT change unless changed in server side
     */
    private static final String HUB_NAME = "mcp";
    private static final String BULLETIN_QUEUE_KEY_TIME = "startTime";
    private static final String BULLETIN_QUEUE_ACK_SCHEDULE_ID = "scheduleId";
    private static final String BULLETIN_QUEUE_KEY_CLIENT_ID = "clientId";
    // END
    private static final int INTERVAL_ONE_MINUTE = 60000;
    private static SignalRClient mInstance = null;
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Context mContext;
    private Handler mHandler;
    private ArrayList<Bulletin> mSavedBulletinList = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private BulletinListener mListener;
    private Gson gson = new Gson();

    private SignalRClient() {
    }

    // Create singleton
    public static SignalRClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SignalRClient();
            mInstance.mContext = context;
            mInstance.mHandler = new Handler();
            mInstance.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mInstance.init();
        }
        return mInstance;
    }

    public void init() {
        // Signalr
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        // Create a new console logger
        Logger logger = new Logger() {
            @Override
            public void log(String message, LogLevel level) {
//                Log.i(Utils.TAG, "SignalR logger -> " + message);
            }
        };

        Log.i(Utils.TAG, "SignalRService: url ->" + mContext.getResources().getString(R.string.url_signalr_service));

        // Create server connection
        mHubConnection = new HubConnection(mContext.getResources().getString(R.string.url_signalr_service), "", true, logger);

        // Create the hubProxy
        mHubProxy = mHubConnection.createHubProxy(HUB_NAME);

        // Subscribe for broadcast message, signature have to match with server side
        mHubProxy.subscribe(new Object() {
            @SuppressWarnings("unused")
            public void onBulletinSent(BulletinBroadcastViewModel broadcastBulletin) {
                Log.i(Utils.TAG, "SignalR (conn id-" + mHubConnection.getConnectionId() + ") " +
                        "bulletin received -> id " + broadcastBulletin.getId());
                bulletinReceived(broadcastBulletin);
            }
        });

        // Subscribe to the error event
        mHubConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                Log.e(Utils.TAG, "SignalR hubConnection error!\n" + error.getMessage());
            }
        });

        // Subscribe to the closed event
        mHubConnection.closed(new Runnable() {
            @Override
            public void run() {
                Log.i(Utils.TAG, "SignalR hubConnection -> DISCONNECTED!");

                // try reconnect
                if (Utils.isNetworkAvailable(mContext))
                    tryReconnect();
            }
        });
    }

    private void bulletinReceived(final BulletinBroadcastViewModel broadcastBulletin) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PowerManager.WakeLock wakelock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE))
                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SignalRService");
                wakelock.acquire();

                // Save the time of receiving to fetch bulletins later
                saveBulletinReceivedTime();

                // For title, use JSoup to parse only the text from html
                // eg. in case there are html tags in content
                String title = broadcastBulletin.getTitle();
                if (title != null) {
                    Document doc = Jsoup.parse(title);
                    title = doc.body().text();
                } else
                    title = "-";

                int id = broadcastBulletin.getId();
                String content = broadcastBulletin.getDescription();
                String date = Utils.getCurrentDateAsString(Utils.DATE_FORMAT_BULLETIN_DETAIL);
                String imageUrl = mContext.getResources().getString(R.string.url_signalr_root) + broadcastBulletin.getImageUrl();

                Bulletin newBulletin = new Bulletin(id, title, content, date, imageUrl, false);
                Log.i(Utils.TAG, "SignalR message arrived! -> "
                        + "\nId: " + id
                        + "\nTitle: " + title
                        + "\nContent: " + content
                        + "\nDate: " + date
                        + "\nimageUrl: " + imageUrl
                        + "\nseen: " + newBulletin.isSeen());


                // Need to load saved list everytime a new bulletin comes
                // Since bulletin status maybe updated and saved in InboxFragment
                String savedBulletinListAsString = mSharedPreferences.getString(Utils.PREF_BULLETIN_LIST,
                        Utils.PREF_NO_ENTRY);
                if (!savedBulletinListAsString.equals(Utils.PREF_NO_ENTRY)) {
                    mSavedBulletinList = gson.fromJson(savedBulletinListAsString,
                            new TypeToken<ArrayList<Bulletin>>() {
                            }.getType());
                }

                // Add new bulletin to list
                mSavedBulletinList.add(0, newBulletin);

                // Save new list in memory
                Utils.saveListInSharedPref(mSavedBulletinList, Utils.PREF_BULLETIN_LIST);

                // If app is running, update UI
                if (mListener != null)
                    mListener.onBulletinReceived(newBulletin);
                else {
                    Log.i(Utils.TAG, "SignalRService: MainActivity listener is null");
                    // Update unread bulletin count
                    Utils.updateUnreadBulletinCount(+1);
                }

                // Show notification
                Utils.generateNotification(mContext, mContext.getResources().getString(R.string.app_name),
                        title, R.mipmap.ic_launcher, InboxFragment.class.getSimpleName()
                );

                // Acknowledge bulletin to server
                acknowledgeBulletin(id);

                wakelock.release();
            }
        });
    }

    public void setBulletinListener(BulletinListener listener) {
        mListener = listener;
    }

    /**
     * Send message to register method
     * signature is:
     * 'register(string phoneId, string phoneType, string appVersion,
     * string appLanguage, string groupName, string ageGroup, string gender)'
     */
    public void register() {
        mHubProxy.invoke("register",
                Utils.getAndroidID(),
                Utils.getDeviceName(),
                Utils.getAppVersion(),
                Utils.getCurrentAppLanguage(),
                Utils.getSavedMessageFilter(Utils.PREF_MESSAGE_FILTER_AGE, SettingsFragment.MESSAGE_FILTER_AGE_ALL),
                Utils.getSavedMessageFilter(Utils.PREF_MESSAGE_FILTER_GENDER, SettingsFragment.MESSAGE_FILTER_GENDER_BOTH))
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(Utils.TAG, "SignalR -> register message SENT!" + "\n"
                                + "(" + Utils.getAndroidID()
                                + ", " + Utils.getDeviceName()
                                + ", " + Utils.getAppVersion()
                                + ", " + Utils.getCurrentAppLanguage()
                                + ", " + Utils.getSavedMessageFilter(Utils.PREF_MESSAGE_FILTER_AGE, SettingsFragment.MESSAGE_FILTER_AGE_ALL)
                                + ", " + Utils.getSavedMessageFilter(Utils.PREF_MESSAGE_FILTER_GENDER, SettingsFragment.MESSAGE_FILTER_GENDER_BOTH)
                                + ")");
                    }
                });
    }

    public void startConnection() {
        mHubConnection.start()
                .done(new Action<Void>() {

                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(Utils.TAG, "SignalR hubConnection start... Done Connecting!" +
                                "\n" + "Connection ID -> " + mHubConnection.getConnectionId());

                        // Send register message
                        register();

                        // Get last received time, and request for bulletin queue
                        String lastReceivedTime = mSharedPreferences
                                .getString(Utils.PREF_SIGNALR_LAST_RECEIVE_TIME, null);
                        Log.i(Utils.TAG, "SignalR retrieving last received time -> " + lastReceivedTime);
                        if (lastReceivedTime != null)
                            fetchBulletinsSinceTime(lastReceivedTime);

                        // Stop the ongoing ping operation
                        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
                                SignalRService.pingIntent(mContext), PendingIntent.FLAG_NO_CREATE);
                        if (pendingIntent != null) {
                            alarmManager.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    }
                });
    }

    /**
     * Checks the connection status
     *
     * @return true if 'connected', 'connecting', 'reconnecting'
     */
    public boolean isConnected() {
        return mHubConnection.getState() == ConnectionState.Connected
                || mHubConnection.getState() == ConnectionState.Connecting
                || mHubConnection.getState() == ConnectionState.Reconnecting;
    }

    /**
     * Stop the signalr connection
     */
    public void stopConnection() {
        Log.i(Utils.TAG, "SignalRClient -> stopConnection() called");
        try {
            if (mHubConnection != null)
                mHubConnection.stop();
        } catch (NullPointerException e) {

        }
    }

    /**
     * Try to reconnect every one minute
     */
    private void tryReconnect() {
        Log.i(Utils.TAG, "SignalRClient -> trying to reconnect");

        // Start ping every minute
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, SignalRService.pingIntent(mContext),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                INTERVAL_ONE_MINUTE, pendingIntent);
    }

    /**
     * Fetch for queued bulletins since a specific time
     * using http POST, with params
     * Param must be time format 'yyyy-MM-dd HH:mm:ss' e.g. '2017-01-01 10:15:20'
     *
     * @param startTime time from which queue is requested
     */
    private void fetchBulletinsSinceTime(String startTime) {
        Log.w(Utils.TAG, "SignalR fetchBulletinsSinceTime: " + startTime);
        final HashMap<String, String> post_params = new HashMap<>();
        post_params.put(BULLETIN_QUEUE_KEY_TIME, startTime);
        post_params.put(BULLETIN_QUEUE_KEY_CLIENT_ID, Utils.getAndroidID());

        StringRequest jsonArrayRequest = new StringRequest(
                Request.Method.POST,
                mContext.getResources().getString(R.string.url_signalr_bulletin_queue),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(Utils.TAG, "SignalR bulletin queue post response -> " + response);
                        updateBulletinListAndAcknowledge(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w(Utils.TAG, "SignalR bulletin queue request error -> " + volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return post_params;
            }
        };

        // Adding request to the request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }

    private void saveBulletinReceivedTime() {
        Log.i(Utils.TAG, "SignalR saving received time -> "
                + Utils.getCurrentTimeInGmt(Utils.TIME_FORMAT_SIGNALR_RECEIVE_TIME));
        mSharedPreferences
                .edit()
                .putString(Utils.PREF_SIGNALR_LAST_RECEIVE_TIME,
                        Utils.getCurrentTimeInGmt(Utils.TIME_FORMAT_SIGNALR_RECEIVE_TIME))
                .apply();

    }

    /**
     * Save the received bulletins and update the UI if possible
     * then acknowledge back to server
     *
     * @param bulletinQueueJsonArray json array of bulletins in string
     */
    private void updateBulletinListAndAcknowledge(final String bulletinQueueJsonArray) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PowerManager.WakeLock wakelock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE))
                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SignalRService");
                wakelock.acquire();

                try {
                    ArrayList<Bulletin> newBulletinList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(bulletinQueueJsonArray);
                    StringBuilder idListString = new StringBuilder("");
                    if (jsonArray.length() != 0) {

                        // Save the time of receiving to fetch bulletins later
                        saveBulletinReceivedTime();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                            int id = jsonObject.getInt("id");
                            idListString.append(String.valueOf(id) + ",");

                            /**
                             * For title, use JSoup to parse only the text from html
                             * eg. in case there are html tags
                             */
                            String title = jsonObject.getString("title");
                            if (title != null) {
                                Document doc = Jsoup.parse(title);
                                title = doc.body().text();
                            } else
                                title = "-";

                            String content = jsonObject.getString("description");
                            String date = Utils.getCurrentDateAsString(Utils.DATE_FORMAT_BULLETIN_DETAIL);
                            String imageUrl = mContext.getString(R.string.url_signalr_root) + jsonObject.getString("image_url");

                            Bulletin newBulletin = new Bulletin(id, title, content, date, imageUrl, false);
                            Log.i(Utils.TAG, "SignalR message arrived! -> "
                                    + "\nId: " + id
                                    + "\nTitle: " + title
                                    + "\nContent: " + content
                                    + "\nDate: " + date
                                    + "\nimageUrl: " + imageUrl
                                    + "\nseen: " + newBulletin.isSeen());

                            newBulletinList.add(0, newBulletin);
                        }

                        // Need to load saved list everytime a new bulletin comes
                        // Since bulletin status maybe updated and saved in InboxFragment
                        String savedBulletinListAsString = mSharedPreferences
                                .getString(Utils.PREF_BULLETIN_LIST, Utils.PREF_NO_ENTRY);
                        if (!savedBulletinListAsString.equals(Utils.PREF_NO_ENTRY)) {
                            mSavedBulletinList = gson.fromJson(savedBulletinListAsString,
                                    new TypeToken<ArrayList<Bulletin>>() {
                                    }.getType());
                        }


                        mSavedBulletinList.addAll(0, newBulletinList);
                        Log.i(Utils.TAG, "SignalRService: Showing updated bulletin list -");
                        Utils.printBulletinList(mSavedBulletinList);

                        // Must be saved into memory before update UI
                        // because inbox fragment reads from saved list
                        Utils.saveListInSharedPref(mSavedBulletinList, Utils.PREF_BULLETIN_LIST);

                        // If app is running, update UI
                        if (mListener != null)
                            mListener.onBulletinListReceived(newBulletinList);
                        else {
                            // Save updated unread bulletin count
                            Log.i(Utils.TAG, "SignalRService: MainActivity listener is null");
                            Utils.updateUnreadBulletinCount(+newBulletinList.size());
                        }

                        // Show notification
                        Utils.generateNotification(mContext,
                                mContext.getResources().getString(R.string.app_name),
                                newBulletinList.size() + " " + mContext.getString(R.string.bulletins_since_last_time),
                                R.mipmap.ic_launcher,
                                InboxFragment.class.getSimpleName()
                        );

                        bulkAcknowledgeBulletins(idListString.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                wakelock.release();
            }
        });
    }

    /**
     * Send message to acknowledge bulletin
     * signature is:
     * 'BulletinAck(String bulletinId, String phoneId)'
     */
    public void acknowledgeBulletin(final int bulletinId) {
        mHubProxy.invoke("BulletinAck", String.valueOf(bulletinId), Utils.getAndroidID())
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(Utils.TAG, "SignalR -> acknowledge message SENT!" + "\n"
                                + "(" + bulletinId
                                + ", " + Utils.getAndroidID()
                                + ")");
                    }
                });
    }

    /**
     * Send bulk acknowledgement string back to server
     * using bulletin ids and client id
     *
     * @param idListString string with list of bulletin ids, seperated by ','
     */
    private void bulkAcknowledgeBulletins(String idListString) {
        Log.w(Utils.TAG, "SignalR queued bulletin ids to ACK: " + idListString);
        final HashMap<String, String> post_params = new HashMap<>();
        post_params.put(BULLETIN_QUEUE_ACK_SCHEDULE_ID, idListString);
        post_params.put(BULLETIN_QUEUE_KEY_CLIENT_ID, Utils.getAndroidID());

        StringRequest acknowledgePostRequest = new StringRequest(
                Request.Method.POST,
                mContext.getResources().getString(R.string.url_signalr_bulletin_queue_ack),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(Utils.TAG, "SignalR bulletin queue ACK request response -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w(Utils.TAG, "SignalR bulletin queue ACK request error -> " + volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return post_params;
            }
        };

        // Adding request to the request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(acknowledgePostRequest);
    }
}

