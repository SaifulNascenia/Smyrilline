package com.mcp.smyrilline.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.AppUtils;

/**
 * Created by raqib on 9/29/17.
 */

public class SettingsChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(AppUtils.TAG, "SignalRService: received broadcast -> " + intent.getAction());
        if (intent.getAction().equals(SignalRService.ACTION_APP_SETTINGS_CHANGED)) {
            context.startService(SignalRService.settingsChangeIntent(context));
        }
    }
}
