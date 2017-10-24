package com.mcp.smyrilline.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by raqib on 1/5/16.
 */
public class BulletinDetailFragment extends DialogFragment {

    public static final String KEY_BULLETIN_DATA = "bulletin_data";

    public static BulletinDetailFragment newInstance(Bulletin bulletin) {
        BulletinDetailFragment fragment = new BulletinDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_BULLETIN_DATA, bulletin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bulletin_detail, container, false);

        // set the messaging_toolbar as actionbar
//        ((DrawerActivity) getActivity()).setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.messaging_toolbar));
//        getActivity().setTitle(R.string.inbox);  // Set title

        // Get data from BulletinAdapter
        Bundle extras = getArguments();
        Bulletin bulletin = extras.getParcelable(KEY_BULLETIN_DATA);

        if (extras != null) {
            String title = bulletin.getTitle();
            ((TextView) rootView.findViewById(R.id.tvBulletinTitleDetail)).setText(title);

            String date = bulletin.getDate();
            ((TextView) rootView.findViewById(R.id.tvBulletinDateDetail)).setText(date);

            String content = bulletin.getContent();
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
            String imageURL = bulletin.getImageUrl();
            if (imageURL != null) {
                Picasso.with(getActivity()).load(imageURL).into(imgBulletinDetail);
            } else
                imgBulletinDetail.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onResume() {
        // set size of dialog to layout file
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}
