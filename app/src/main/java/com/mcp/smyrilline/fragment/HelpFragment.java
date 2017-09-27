package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.rest.JSONParser;
import com.mcp.smyrilline.util.AppUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment for Help information in Welcome page
 */
public class HelpFragment extends Fragment {

    private Context mContext;
    private WebView wvHelpInfo;
    private View mLoadingView;
    private TextView tvNothingText;
    private TextView tvAppVersion;
    private TextView tvClientId;
    private String mHelpInfo;
    private boolean mApplyCss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Init UI
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        getActivity().invalidateOptionsMenu();
        mContext = getActivity();
        // set the toolbar as actionbar
        ((DrawerActivity) getActivity())
                .setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(R.string.help);  // Set title

        wvHelpInfo = (WebView) rootView.findViewById(R.id.wvHelpInfo);
        wvHelpInfo.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));

        // show app version
        tvAppVersion = (TextView) rootView.findViewById(R.id.tvAppVersion);
        tvAppVersion.setText(AppUtils.getAppVersion());

        // show client id
        tvClientId = (TextView) rootView.findViewById(R.id.tvClientID);
        tvClientId.setText(AppUtils.getAndroidID());

        mLoadingView = rootView.findViewById(R.id.helpLoadingView);
        tvNothingText = (TextView) rootView.findViewById(R.id.tvHelpNothingText);

        // get saved info
        String savedHelpInfo = "<p><strong>Wi-Fi Connectivity</strong></p>\n" +
                "<p>Remember to connect to our local Wi-Fi network to take full advantage of our Smyril Line APP."
                +
                "</p>\n<p><strong>Ship Tracker</strong></p>\n<p>Ship Tracker gives you a quick glimpse of next port, "
                +
                "previous port, time of arrival and path of journey.</p>\n<p><strong>Coupons and Offers</strong></p>\n"
                +
                "<p>You can see all the available offers at a glance in this section. Hurry up before the promotional offers expire.</p>\n"
                +
                "<p><strong>Restaurants</strong></p>\n<p>You will find list of all the restaurants in our ship and their menu. Enjoy delicious food at our cozy restaurants."
                +
                "</p>\n<p><strong>About the Ship</strong></p>\n<p>You will find all the info about Norröna in this section.</p>\n";

//                PreferenceManager.getDefaultSharedPreferences(mContext)
//                .getString(AppUtils.PREF_HELP_INFO + "_" + AppUtils.getCurrentAppLanguage(), null);
        mApplyCss = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(AppUtils.PREF_CSS_HELP, true);  // default apply css

        if (savedHelpInfo == null) {
            mLoadingView.setVisibility(View.VISIBLE);
            startHelpThread();
        } else {
            String cssTextColorWhite = "<style type=\"text/css\">body{color: #fff;}"
                    + "</style>";
            AppUtils.showContentInWebview(wvHelpInfo, /*cssTextColorWhite + */savedHelpInfo,
                    mApplyCss);
        }

        return rootView;
    }

    /**
     * First check wifi/data
     * Next run a thread to check http connectivity
     * If available, start AsyncTask to retrieve data
     */
    private void startHelpThread() {
        if (AppUtils.isNetworkAvailable(mContext)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String response = AppUtils.isDomainAvailable(mContext,
                            mContext.getResources().getString(R.string.url_wordpress));
                    if (response.equals(AppUtils.CONNECTION_OK))
                        new LoadHelpInfoTask().execute();
                    else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingView.setVisibility(View.GONE);
                                    tvNothingText.setVisibility(View.VISIBLE);
                                    AppUtils.showAlertDialog(mContext, response);
                                }
                            });
                        }
                    }
                }
            }).start();
        } else {
            mLoadingView.setVisibility(View.GONE);
            tvNothingText.setVisibility(View.VISIBLE);
            AppUtils.showAlertDialog(mContext, AppUtils.ALERT_NO_WIFI);
        }
    }

    public class LoadHelpInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Creating new JSON Parser
            JSONParser jParser = new JSONParser();

            // Getting JSON array from URL
            JSONArray jArray = jParser.getJSONArrayFromUrl(mContext.getResources()
                    .getString(R.string.url_wordpress_parent) + "=0" + AppUtils.WP_PARAM_LANGUAGE);

            // Double check
            if (jArray == null)
                return null;

            try {
                // Getting JSON object for Welcome

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject rootJson = jArray.getJSONObject(i);
                    if (rootJson.getString("template").equals("page_welcome.php")) {
                        mHelpInfo = rootJson.getJSONObject("content").getString("rendered");
                        mApplyCss = !AppUtils.isNoCss(rootJson);
                    }
                }
                return mHelpInfo;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);

            mLoadingView.setVisibility(View.GONE);

            if (content != null) {
                AppUtils.showContentInWebview(wvHelpInfo, content, mApplyCss);

                // Save info
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(mContext).edit();
                editor.putString(AppUtils.PREF_HELP_INFO + "_" + AppUtils.getCurrentAppLanguage(),
                        content);
                editor.putBoolean(AppUtils.PREF_CSS_HELP, mApplyCss);
                editor.apply();
            } else
                tvNothingText.setVisibility(View.VISIBLE);
        }
    }
}
