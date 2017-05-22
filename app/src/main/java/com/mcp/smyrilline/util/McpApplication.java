package com.mcp.smyrilline.util;

import android.content.Context;

import android.support.multidex.MultiDexApplication;

/**
 * Created by raqib on 5/4/17.
 */

public class McpApplication extends MultiDexApplication {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        StaticData.initGridNavList();
    }
}
