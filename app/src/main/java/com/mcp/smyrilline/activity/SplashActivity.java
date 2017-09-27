package com.mcp.smyrilline.activity;

/**
 * Activity for splash screen, after time out it will start MainActivity, which hosts all the
 * fragments
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.util.AppUtils;

public class SplashActivity extends Activity {

    private static final int SPLASH_TIME_OUT = 1000;     // Splash screen timer
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

//        if (AppUtils.isNetworkAvailable(this))
//            new CheckCMSandInitURLs().execute();

        final SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start Guide screen activity if first time, else Main activity
                Intent i = null;

                if (sharedPref.getBoolean(AppUtils.FIRST_RUN, true))
                    i = new Intent(mContext, GuideActivity.class);
                else
                    i = new Intent(mContext, MainGridActivity.class);

                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}


