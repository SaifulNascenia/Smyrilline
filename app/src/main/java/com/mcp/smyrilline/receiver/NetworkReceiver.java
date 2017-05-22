package com.mcp.smyrilline.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.Utils;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Utils.TAG, "NetworkReceiver: network change received");
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            Log.i(Utils.TAG, "NetworkReceiver: connected");
            context.startService(SignalRService.startIntent(context));
        } else if (networkInfo != null) {
            NetworkInfo.DetailedState state = networkInfo.getDetailedState();
            Log.i(Utils.TAG, state.name());
        } else {
            Log.i(Utils.TAG, "NetworkReceiver: lost connection");
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent operation = PendingIntent.getService(context, 0, SignalRService.pingIntent(context), PendingIntent.FLAG_NO_CREATE);
            if (operation != null) {
                am.cancel(operation);
                operation.cancel();
            }
            context.startService(SignalRService.closeIntent(context));
        }
    }
}