package com.mcp.smyrilline.signalr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.InboxFragment;
import com.mcp.smyrilline.fragment.RealtimeChatFragment;
import com.mcp.smyrilline.listener.BulletinListener;
import com.mcp.smyrilline.model.messaging.AcknowledgeMessageItem;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.model.messaging.ChatListItem;
import com.mcp.smyrilline.model.messaging.ChatUserClientModel;
import com.mcp.smyrilline.model.messaging.ChatUserServerModel;
import com.mcp.smyrilline.model.messaging.ClientMessageItem;
import com.mcp.smyrilline.model.messaging.LocalMessageStatus;
import com.mcp.smyrilline.model.messaging.MessagePage;
import com.mcp.smyrilline.model.messaging.ServerMessageItem;
import com.mcp.smyrilline.model.messaging.ServerMessageListEvent;
import com.mcp.smyrilline.model.messaging.ServerResponse;
import com.mcp.smyrilline.model.messaging.ServerUserListEvent;
import com.mcp.smyrilline.rest.VolleySingleton;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.AppUtils.MsgSendingStatus;
import com.mcp.smyrilline.util.AppUtils.RequestStatus;
import com.mcp.smyrilline.util.AppUtils.RequestType;
import com.mcp.smyrilline.util.InternalStorage;
import com.mcp.smyrilline.util.McpApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.PlatformComponent;
import microsoft.aspnet.signalr.client.http.HttpConnection;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by raqib on 8/26/16.
 */
public class SignalRClient {

    /* Parameters for server request
     * MUST match server side
     * DO NOT change unless changed in server side
     */
    private static final String HUB_NAME_BULLETIN = "mcp";
    private static final String HUB_NAME_CHAT = "chat";
    private static final String BULLETIN_QUEUE_KEY_TIME = "startTime";
    private static final String BULLETIN_QUEUE_ACK_SCHEDULE_ID = "scheduleId";
    private static final String BULLETIN_QUEUE_KEY_CLIENT_ID = "clientId";
    // END
    private static final int INTERVAL_ONE_MINUTE = 60000;
    private static final String TAG = "signalr";
    private static final long SIGNALR_TIMEOUT = 5000;
    private static SignalRClient mInstance = null;
    private HubConnection mHubConnection;
    private HubProxy mHubProxyBulletin;
    private HubProxy mHubProxyChat;
    private Context mContext;
    private Handler mHandler;
    private ArrayList<Bulletin> mSavedBulletinList = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private BulletinListener mListener;
    private Gson gson = new Gson();

    private SignalRClient() {
        mContext = McpApplication.instance().context();
        mHandler = new Handler();
        mSharedPreferences = McpApplication.instance().sharedPref();
        init();
    }

    // Create singleton
    public static SignalRClient getInstance() {
        if (mInstance == null) {
            synchronized (SignalRClient.class) {
                mInstance = new SignalRClient();
            }
        }
        return mInstance;
    }

    public void init() {
        try {
            // We discard AndroidPlatformComponent(), because of build problem using latest aar
            // it supported OS <= Froyo (api 8), no need cuz project min is 18
            // we avoid it and use defaultHttpConnection based on AndroidPlatformComponent() src
            Platform.loadPlatformComponent(new PlatformComponent() {
                @Override
                public HttpConnection createHttpConnection(Logger logger) {
                    return Platform.createDefaultHttpConnection(logger);
                }

                @Override
                public String getOSName() {
                    return "android";
                }
            });

            // Create a new console logger
            Logger logger = new Logger() {
                @Override
                public void log(String message, LogLevel level) {
                    Log.i(AppUtils.TAG, "SignalR logger -> " + message);
                }
            };

            Log.i(AppUtils.TAG, "SignalRService: url -> " + mContext.getResources()
                    .getString(R.string.url_signalr_service));

            // Create server connection
            mHubConnection = new HubConnection(
                    mContext.getResources().getString(R.string.url_signalr_service), "", true,
                    logger);

   /*         // === Bulletin ===//
            // Create the hubProxy
            mHubProxyBulletin = mHubConnection.createHubProxy(HUB_NAME_BULLETIN);
            // Subscribe for broadcast message, signature have to match with server side
            mHubProxyBulletin.subscribe(new Object() {
                @SuppressWarnings("unused")
                public void onBulletinSent(BulletinBroadcastViewModel broadcastBulletin) {
                    Log.i(AppUtils.TAG,
                            "SignalR (conn id-" + mHubConnection.getConnectionId() + ") " +
                                    "bulletin received -> id " + broadcastBulletin.getId());
                    bulletinReceived(broadcastBulletin);
                }
            });
*/
            // === Chat === //
            mHubProxyChat = mHubConnection.createHubProxy(HUB_NAME_CHAT);
            // Subscribe for broadcast message, signature have to match with server side

            mHubProxyChat.subscribe(new Object() {
                public void onChatMessageToClient(String jsonMessageString) {
                    Log.e(TAG, "onChatMessageToClient received: " + jsonMessageString);
                    // we will update UI after acknowledge
                    ServerMessageItem serverMessageItem = gson
                        .fromJson(jsonMessageString, ServerMessageItem.class);
                    acknowledgeChatMessageToServer(serverMessageItem);
                }
            });

            // Subscribe for user list string, signature have to match with server side
            mHubProxyChat.subscribe(new Object() {
                public void onUserListToClient(String jsonUserArray) {
                    System.out.println("Received user list string -> " + jsonUserArray);
                    try {
                        JSONArray jsonArray = new JSONArray(jsonUserArray);
                        ArrayList<ChatUserServerModel> userList = new ArrayList<ChatUserServerModel>();

                        // this array consists of all user in server at the moment
                        // including self, so need to exclude self
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (!jsonObject.getString("deviceId").trim()
                                .equals(AppUtils.getDeviceId())) {

                                ChatUserServerModel chatUserServerModel =
                                    gson.fromJson(jsonObject.toString(),
                                        ChatUserServerModel.class);
                                userList.add(chatUserServerModel);
                            }
                        }

                        // save chatUserList
                        InternalStorage
                            .writeObject(mContext, AppUtils.KEY_SAVED_USER_LIST, userList);

                        Log.i(AppUtils.TAG, "Signalr posting user list received event..");
                        // send event to UserListFragment to populate list
                        EventBus.getDefault().post(new ServerUserListEvent(userList));
                    } catch (JSONException e) {
                        Log.e(AppUtils.TAG,
                            "Signalr: user list from server FAILED -> " + e.getMessage());
                    }
                }
            });

            // Subscribe for message list
            mHubProxyChat.subscribe(new Object() {
                public void onMessageListOfSender(String jsonMessageArray) {
                    Log.i(AppUtils.TAG, "onMessageListOfSenderReceived " + jsonMessageArray);
                    try {
                        JSONArray jsonArray = new JSONArray(jsonMessageArray);
                        ArrayList<ChatListItem> messageList = new ArrayList<>();
                        ChatListItem chatListItem = null;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            chatListItem =
                                gson.fromJson(jsonObject.toString(),
                                    ChatListItem.class);
                            messageList.add(chatListItem);
                        }

                        if (messageList != null) {
//                            // Save message list of sender
//                            InternalStorage.writeObject(mContext,
//                                    AppUtils.KEY_SAVED_MESSAGE_LIST + "_" + chatListItem
//                                            .getSenderDeviceId(), messageList);

                            // Post message list to RealtimeChatFragment
                            EventBus.getDefault().post(new ServerMessageListEvent(messageList));
                        }
                    } catch (JSONException e) {
                        Log.e(AppUtils.TAG,
                            "SignalRClient onMessageListOfSender exception -> " + e);
                    }
                }
            });

            // Subscribe to the error event
            mHubConnection.error(new ErrorCallback() {
                @Override
                public void onError(Throwable error) {
                    Log.e(AppUtils.TAG, "SignalR hubConnection error!\n" + error);
                }
            });

            // Subscribe to the closed event
            mHubConnection.closed(new Runnable() {
                @Override
                public void run() {
                    Log.e(AppUtils.TAG, "SignalR hubConnection -> DISCONNECTED!");
                    // post disconnect event
                    EventBus.getDefault()
                        .post(new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
                    // try reconnect
                    if (AppUtils.isNetworkAvailable(mContext)) {
                        tryReconnect();
                    }
                }
            });
        } catch (Exception e) {
            // catch any exception
            e.printStackTrace();
        }
    }

    private void bulletinReceived(final BulletinBroadcastViewModel broadcastBulletin) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PowerManager.WakeLock wakelock = ((PowerManager) mContext
                        .getSystemService(Context.POWER_SERVICE))
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
                String date = AppUtils.getCurrentDateAsString(AppUtils.DATE_FORMAT_BULLETIN_DETAIL);
                String imageURL = mContext.getResources().getString(R.string.url_signalr_root)
                        + broadcastBulletin.getImageUrl();

                Bulletin newBulletin = new Bulletin(id, title, content, date, imageURL, false);
                Log.i(AppUtils.TAG, "SignalR message arrived! -> "
                        + "\nId: " + id
                        + "\nTitle: " + title
                        + "\nContent: " + content
                        + "\nDate: " + date
                        + "\nimageURL: " + imageURL
                        + "\nseen: " + newBulletin.isSeen());

                // Need to load saved list everytime a new bulletin comes
                // Since bulletin status maybe updated and saved in InboxFragment
                String savedBulletinListAsString = mSharedPreferences
                        .getString(AppUtils.PREF_BULLETIN_LIST,
                                AppUtils.PREF_NO_ENTRY);
                if (!savedBulletinListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                    mSavedBulletinList = gson.fromJson(savedBulletinListAsString,
                            new TypeToken<ArrayList<Bulletin>>() {
                            }.getType());
                }

                // Add new bulletin to list
                mSavedBulletinList.add(0, newBulletin);

                // Save new list in memory
                AppUtils.saveListInSharedPref(mSavedBulletinList, AppUtils.PREF_BULLETIN_LIST);

                // If app is running, update UI
                if (mListener != null)
                    mListener.onBulletinReceived(newBulletin);
                else {
                    Log.i(AppUtils.TAG, "SignalRService: DrawerActivity listener is null");
                    // Update unread bulletin count
                    AppUtils.updateUnreadBulletinCount(+1);
                }

                // Show notification
                AppUtils.generateNotification(mContext,
                        mContext.getResources().getString(R.string.app_name),
                    title,
                    InboxFragment.class.getSimpleName(), null);

                // Acknowledge bulletin to server
                acknowledgeBulletin(id);

                wakelock.release();
            }
        });
    }

    public void setBulletinListener(BulletinListener listener) {
        mListener = listener;
    }

    /*
     * Send message to registerBulletin method
     * signature is:
     * 'registerBulletin(string phoneId, string phoneType, string appVersion,
     * string appLanguage, string ageGroup, string gender)'
     */
    public void registerBulletin() {
        try {
            mHubProxyBulletin.invoke("register",
                AppUtils.getDeviceId(),
                    AppUtils.getDeviceName(),
                    AppUtils.getAppVersion(),
                    AppUtils.getCurrentAppLanguage(),
                "all",
                "both")
                    .done(new Action<Void>() {
                        @Override
                        public void run(Void obj) throws Exception {
                            Log.i(AppUtils.TAG, "SignalR -> registerBulletin message SENT!" + "\n"
                                + "(" + AppUtils.getDeviceId()
                                    + ", " + AppUtils.getDeviceName()
                                    + ", " + AppUtils.getAppVersion()
                                    + ", " + AppUtils.getCurrentAppLanguage()
                                + ", " + "all"
                                + ", " + "both"
                                    + ")");
                        }
                    });
        } catch (Exception e) {
        }
    }

    /**
     * Start the signalr connection
     *
     * @param messageToSend the message to send, if applicable
     */
    public void startConnection(final ChatListItem messageToSend) {
        Log.i(AppUtils.TAG, "SignalRClient: startConnection() called..");
        if (AppUtils.isNetworkAvailable(mContext)) {
            try {
                mHubConnection.start(new ServerSentEventsTransport(mHubConnection.getLogger()))
                        .done(new Action<Void>() {
                            @Override
                            public void run(Void obj) throws Exception {
                                Log.i(AppUtils.TAG,
                                        "SignalR hubConnection start... Done Connecting!" +
                                                "\n" + "Connection ID -> " + mHubConnection
                                                .getConnectionId());

                                // Stop the ongoing ping operation
                                AlarmManager alarmManager = (AlarmManager) mContext
                                    .getSystemService(Context.ALARM_SERVICE);
                                PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
                                    SignalRService.pingIntent(mContext),
                                    PendingIntent.FLAG_NO_CREATE);
                                if (pendingIntent != null) {
                                    alarmManager.cancel(pendingIntent);
                                    pendingIntent.cancel();
                                }

                                // Send bulletin registerBulletin message
//                                registerBulletin();

                                // Send chat registerBulletin message
                                registerChat();

                                // Get last received time, and request for bulletin queue
                                String lastReceivedTime = mSharedPreferences
                                        .getString(AppUtils.PREF_SIGNALR_LAST_RECEIVE_TIME, null);
                                Log.i(AppUtils.TAG,
                                        "SignalR retrieving last received time -> "
                                                + lastReceivedTime);
                                if (lastReceivedTime != null) {
                                    fetchBulletinsSinceTime(lastReceivedTime);
                                }

                                // if there is a message to send
                                if (messageToSend != null)
                                    sendChatMessageToServer(messageToSend);
                            }
                        })
                    .onError(new ErrorCallback() {
                        @Override
                        public void onError(Throwable error) {
                            Log.e(AppUtils.TAG, "SignalR: startConnection error -> " + error);
                            // post failed
                            EventBus.getDefault().post(
                                new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
                        }
                    }).get(SIGNALR_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Log.e(AppUtils.TAG, "Signalr start connection FAILED -> Disconnect . . .\n" + e);
                EventBus.getDefault()
                    .post(new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
            } catch (ExecutionException e) {
                Log.e(AppUtils.TAG, "Signalr start connection FAILED -> Error . . .\n" + e);
                EventBus.getDefault()
                    .post(new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
            } catch (Exception e) {
                Log.e(AppUtils.TAG, "Signalr start connection FAILED -> Error . . .\n" + e);
                EventBus.getDefault()
                    .post(new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
            }
        } else {
            Log.e(AppUtils.TAG, "No connectivity!");
            EventBus.getDefault()
                .post(new ServerResponse(RequestType.SERVER_CONNECT, RequestStatus.FAIL));
        }
    }

    /**
     * Send register message to signalr server with client information
     */
    public void registerChat() {
        try {
            final ChatUserClientModel chatUserClientModel = new ChatUserClientModel(
                AppUtils.getDeviceId(),
                mSharedPreferences.getInt(AppUtils.PREF_MY_BOOKING_NO, 0),    // booking no.
                mSharedPreferences.getString(AppUtils.PREF_MY_NAME, "null"),
                // user name
                mSharedPreferences.getString(AppUtils.PREF_MY_IMAGE_URL, "null"),
                // image url
                mSharedPreferences.getString(AppUtils.PREF_MY_DESCRIPTION, "null"),
                mSharedPreferences.getString(AppUtils.PREF_MY_COUNTRY, "null"),
                mSharedPreferences.getString(AppUtils.PREF_MY_GENDER, "null"),
                mSharedPreferences.getInt(AppUtils.PREF_MY_STATUS, AppUtils.USER_ONLINE),
                mSharedPreferences
                    .getInt(AppUtils.PREF_MY_VISIBILITY, AppUtils.VISIBLE_TO_ALL));

            Log.d(TAG, "Sending registerChat... " + chatUserClientModel.toString());

            mHubProxyChat.invoke("registerChat", chatUserClientModel)
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.d(TAG,
                            "Register chat SUCCESS! -> " + chatUserClientModel.toString());
                    }

                })
                .onError(new ErrorCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Log.e(AppUtils.TAG,
                            "Signalr: registerChat FAILED! -> " + error);
                        // since register chat failed, so message list will not be received
                        // post fail status (will be received by UserListFragment)
                        EventBus.getDefault().post(
                            new ServerResponse(RequestType.REQUEST_MESSAGE_LIST,
                                RequestStatus.FAIL));
                    }
                });
        } catch (Exception e) {
            EventBus.getDefault()
                .post(new ServerResponse(RequestType.REQUEST_MESSAGE_LIST, RequestStatus.FAIL));
        }
    }

    /**
     * Checks the connection status
     *
     * @return true if 'connected'
     */
    public boolean isConnected() {
        return mHubConnection.getState() == ConnectionState.Connected;
    }

    public boolean isConnecting() {
        return mHubConnection.getState() == ConnectionState.Connecting
                || mHubConnection.getState() == ConnectionState.Reconnecting;
    }

    public boolean isDisconnected() {
        return mHubConnection.getState() == ConnectionState.Disconnected;
    }

    /**
     * Stop the signalr connection
     */
    public void stopConnection() {
        Log.i(AppUtils.TAG, "SignalRClient -> stopConnection() called");
        try {
            if (mHubConnection != null)
                mHubConnection.stop();
        } catch (Exception e) {
        }
    }

    /**
     * Try to reconnect every one minute
     */
    private void tryReconnect() {
        Log.i(AppUtils.TAG, "SignalRClient -> trying to reconnect");

        // Start ping every minute
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent
                .getService(mContext, 0, SignalRService.pingIntent(mContext),
                        PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
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
        Log.w(AppUtils.TAG, "SignalR fetchBulletinsSinceTime: " + startTime);
        final HashMap<String, String> post_params = new HashMap<>();
        post_params.put(BULLETIN_QUEUE_KEY_TIME, startTime);
        post_params.put(BULLETIN_QUEUE_KEY_CLIENT_ID, AppUtils.getDeviceId());

        StringRequest jsonArrayRequest = new StringRequest(
                Request.Method.POST,
                mContext.getResources().getString(R.string.url_signalr_bulletin_queue),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(AppUtils.TAG, "SignalR bulletin queue post response -> " + response);
                        updateBulletinListAndAcknowledge(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w(AppUtils.TAG, "SignalR bulletin queue request error -> " + volleyError
                                .getMessage());
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

    /**
     * Save the current system time as the last message received time
     * Used to request for offline bulletin queue after next connection
     */
    private void saveBulletinReceivedTime() {
        Log.i(AppUtils.TAG, "SignalR saving received time -> "
                + AppUtils.getCurrentTimeInGmt(AppUtils.TIME_FORMAT_SIGNALR_RECEIVE_TIME));

        mSharedPreferences
                .edit()
                .putString(AppUtils.PREF_SIGNALR_LAST_RECEIVE_TIME,
                        AppUtils.getCurrentTimeInGmt(AppUtils.TIME_FORMAT_SIGNALR_RECEIVE_TIME))
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
                PowerManager.WakeLock wakelock = ((PowerManager) mContext
                        .getSystemService(Context.POWER_SERVICE))
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
                            } else {
                                title = "-";
                            }

                            String content = jsonObject.getString("description");
                            String date = AppUtils
                                    .getCurrentDateAsString(AppUtils.DATE_FORMAT_BULLETIN_DETAIL);

                            String imageURL =
                                    mContext.getString(R.string.url_signalr_root) + jsonObject
                                            .getString("image_url");

                            Bulletin newBulletin = new Bulletin(id, title, content, date, imageURL,
                                    false);
                            Log.i(AppUtils.TAG, "SignalR message arrived! -> "
                                    + "\nId: " + id
                                    + "\nTitle: " + title
                                    + "\nContent: " + content
                                    + "\nDate: " + date
                                    + "\nimageURL: " + imageURL
                                    + "\nseen: " + newBulletin.isSeen());

                            newBulletinList.add(0, newBulletin);
                        }

                        // Need to load saved list everytime a new bulletin comes
                        // Since bulletin status maybe updated and saved in InboxFragment
                        String savedBulletinListAsString = mSharedPreferences
                                .getString(AppUtils.PREF_BULLETIN_LIST, AppUtils.PREF_NO_ENTRY);

                        if (!savedBulletinListAsString.equals(AppUtils.PREF_NO_ENTRY)) {
                            mSavedBulletinList = gson.fromJson(savedBulletinListAsString,
                                    new TypeToken<ArrayList<Bulletin>>() {
                                    }.getType());
                        }

                        mSavedBulletinList.addAll(0, newBulletinList);
                        Log.i(AppUtils.TAG, "SignalRService: Showing updated bulletin list -");
                        AppUtils.printBulletinList(mSavedBulletinList);

                        // Must be saved into memory before update UI
                        // because inbox fragment reads from saved list
                        AppUtils.saveListInSharedPref(mSavedBulletinList,
                                AppUtils.PREF_BULLETIN_LIST);

                        // If app is running, update UI
                        if (mListener != null) {
                            mListener.onBulletinListReceived(newBulletinList);
                        } else {
                            // Save updated unread bulletin count
                            Log.i(AppUtils.TAG, "SignalRService: DrawerActivity listener is null");
                            AppUtils.updateUnreadBulletinCount(+newBulletinList.size());
                        }

                        // Show notification
                        AppUtils.generateNotification(mContext,
                                mContext.getResources().getString(R.string.app_name),
                                newBulletinList.size() + " " + mContext
                                        .getString(R.string.bulletins_since_last_time),
                            InboxFragment.class.getSimpleName(), null);

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
        mHubProxyBulletin.invoke("BulletinAck", String.valueOf(bulletinId), AppUtils.getDeviceId())
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(AppUtils.TAG, "SignalR -> acknowledge message SENT!" + "\n"
                                + "(" + bulletinId
                            + ", " + AppUtils.getDeviceId()
                                + ")");
                    }
                })
            .onError(new ErrorCallback() {
                @Override
                public void onError(Throwable error) {
                    Log.e(AppUtils.TAG, "Signalr acknowledge bulletin FAILED! -> " + error);
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
        Log.w(AppUtils.TAG, "SignalR queued bulletin ids to ACK: " + idListString);
        final HashMap<String, String> post_params = new HashMap<>();
        post_params.put(BULLETIN_QUEUE_ACK_SCHEDULE_ID, idListString);
        post_params.put(BULLETIN_QUEUE_KEY_CLIENT_ID, AppUtils.getDeviceId());

        StringRequest acknowledgePostRequest = new StringRequest(
                Request.Method.POST,
                mContext.getResources().getString(R.string.url_signalr_bulletin_queue_ack),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(AppUtils.TAG,
                                "SignalR bulletin queue ACK request response -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.w(AppUtils.TAG,
                                "SignalR bulletin queue ACK request error -> " + volleyError
                                        .getMessage());
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

    /**
     * Send given message to signalr server
     */
    public void sendChatMessageToServer(final ChatListItem chatListItem) {
        try {
            Log.i(AppUtils.TAG, "Signalr: sendChatMessageToServer() sending Chat message -> "
                + chatListItem.getMessage());
            mHubProxyChat.invoke("onChatMessageToServer",
                new ClientMessageItem(
                    chatListItem.getSenderDeviceId(),
                    chatListItem.getReceiverDeviceId(), chatListItem.getMessage()
                ))
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(AppUtils.TAG, "Signalr -> sent Chat message to server!" + "\n"
                            + "(" + "sender: " + chatListItem.getSenderDeviceId()
                            + ", " + "receiver: " + chatListItem.getReceiverDeviceId()
                            + "message: " + chatListItem.getMessage() + ")");

                        // post success of sending
                        EventBus.getDefault().post(
                            new LocalMessageStatus(chatListItem.getUniqueId(),
                                MsgSendingStatus.SENT));

                        // save the response with the item

                    }
                })
                .onError(new ErrorCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Log.e(AppUtils.TAG,
                            "Signalr: sending Chat message FAILED! -> " + error);
                        // post fail event
                        EventBus.getDefault().post(
                            new LocalMessageStatus(chatListItem.getUniqueId(),
                                MsgSendingStatus.FAILED));
                    }
                });
        } catch (Exception e) {
            Log.e(AppUtils.TAG, "SignalRClient: sendChatMessageToServer() error -> " + e);
            // post fail event
            EventBus.getDefault()
                .post(new LocalMessageStatus(chatListItem.getUniqueId(), MsgSendingStatus.FAILED));
        }
    }

    /**
     * Acknowledge chat message and update UI
     *
     * @param serverMessageItem the message item to acknowledge
     */
    private void acknowledgeChatMessageToServer(final ServerMessageItem serverMessageItem) {
        try {
            mHubProxyChat.invoke("onClientAcknowledgeMessage",
                new AcknowledgeMessageItem(
                    serverMessageItem.getMessageId()
                ))
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(AppUtils.TAG, "Chat -> sent ACK message to server for message id"
                            + serverMessageItem.getMessageId());
                        EventBus.getDefault().post(serverMessageItem);

                        // show notification only if chat is not going on with sender
                        if (!(RealtimeChatFragment.isFragmentShowing() && AppUtils
                            .isCurrentlyChattingWithUser(
                                serverMessageItem.getSenderDeviceId()))) {

                            // generate notification
                            AppUtils.generateNotification(mContext,
                                mContext.getString(R.string.new_message_from_user,
                                    serverMessageItem.getSenderName()),
                                serverMessageItem.getMessage(),
                                RealtimeChatFragment.class.getSimpleName(),
                                serverMessageItem.getSenderChatUserServerModel());

                            // update new chat message count
                            AppUtils.updateNewChatMessageCountOfUser(
                                serverMessageItem.getSenderDeviceId(), +1);

                        }
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "onChatMessageToClient exception: " + e);
        }
    }

    /**
     * To stop and start the connection with a small delay
     */
    public void resetConnection(final ChatListItem chatListItem) {
        stopConnection();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startConnection(chatListItem);
            }
        }, 2000);
    }

    public void requestMessageListOfSender(final MessagePage requestPage) {
        try {
            mHubProxyChat.invoke("loadMoreFromSender",
                requestPage)
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        Log.i(AppUtils.TAG,
                            "Signalr -> requested for message list SUCCESS " + requestPage);
                        // message list will be handled inside
                    }
                })
                .onError(new ErrorCallback() {
                    @Override
                    public void onError(Throwable error) {
                        Log.e(AppUtils.TAG,
                            "Signalr: request for message list -> " + error);

                        // load more request failed, notify realtime chat fragment
                        EventBus.getDefault()
                            .post(new ServerResponse(RequestType.REQUEST_MESSAGE_LIST,
                                RequestStatus.FAIL));
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "requestMessageListOfSender exception: " + e);
            // load more request failed, notify realtime chat fragment
            EventBus.getDefault()
                .post(new ServerResponse(RequestType.REQUEST_MESSAGE_LIST,
                    RequestStatus.FAIL));
        }
    }
}

