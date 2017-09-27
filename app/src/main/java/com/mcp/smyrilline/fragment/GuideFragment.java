package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.GuideActivity;

/**
 * Fragment to show each page of the Guide screen
 */
public class GuideFragment extends android.support.v4.app.Fragment {

    private int mPosition;

    public static GuideFragment newInstance(int position) {
        GuideFragment guideFragment = new GuideFragment();
        guideFragment.mPosition = position;
        return guideFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide_screen, container, false);

        TextView tvGuideScreenHeader = (TextView) rootView.findViewById(R.id.tvGuideScreenHeader);
        tvGuideScreenHeader.setText(((GuideActivity) getActivity()).getGuideScreenTitle(mPosition));

        TextView tvGuideScreenSubHeader = (TextView) rootView
                .findViewById(R.id.tvGuideScreenSubHeader);
        tvGuideScreenSubHeader
                .setText(((GuideActivity) getActivity()).getGuideScreenSubTitle(mPosition));

        ImageView imgGuideScreen = (ImageView) rootView.findViewById(R.id.imgGuideScreen);
        imgGuideScreen
                .setImageResource(((GuideActivity) getActivity()).getGuideScreenImage(mPosition));

        return rootView;
    }
}
