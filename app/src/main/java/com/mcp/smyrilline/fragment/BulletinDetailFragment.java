package com.mcp.smyrilline.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by raqib on 1/5/16.
 */
public class BulletinDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bulletin_detail, container, false);
        // set the toolbar as actionbar
        ((DrawerActivity) getActivity())
                .setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(R.string.inbox);  // Set title

        // Get data from BulletinAdapter
        Bundle extras = getArguments();

        if (extras != null) {
            String title = extras.getString("title");
            ((TextView) rootView.findViewById(R.id.tvBulletinTitleDetail)).setText(title);

            String date = extras.getString("date");
            ((TextView) rootView.findViewById(R.id.tvBulletinDateDetail)).setText(date);

            String content = extras.getString("content");
            content = content.replace("\n", "<br>");
            WebView wvBulletinContentDetail = (WebView) rootView
                    .findViewById(R.id.wvBulletinContentDetail);

            WebSettings webSettings = wvBulletinContentDetail.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            webSettings.setLoadWithOverviewMode(true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                webSettings.setPluginState(WebSettings.PluginState.ON);
            }

            AppUtils.removeBottomSpaceFromWebView(wvBulletinContentDetail);
            wvBulletinContentDetail.setWebChromeClient(new WebChromeClient());
            wvBulletinContentDetail
                    .loadDataWithBaseURL(null, AppUtils.CSS_FIT_IMAGE_IN_WEBVIEW + content,
                            "text/html", "UTF-8", null);
            wvBulletinContentDetail.setBackgroundColor(Color.TRANSPARENT);

            ImageView imgBulletinDetail = (ImageView) rootView.findViewById(R.id.imgBulletinDetail);
            String imageURL = extras.getString("imageURL");
            if (imageURL != null) {
                Picasso.with(getActivity()).load(imageURL).error(R.drawable.img_placeholder_thumb)
                        .into(imgBulletinDetail);
            } else
                imgBulletinDetail.setVisibility(View.GONE);
        }
        return rootView;
    }
}