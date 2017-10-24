package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by saiful on 6/21/17.
 */

public class ShipInfoDetailsFragment extends Fragment {

    private View mRootView;
    private Toolbar toolbar;
    private TextView tvShipInfoDetail;
    private TextView expandTextView;
    private ImageView imgShipInfoDetailHeader;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_ship_info_details_1, container, false);
        initView();
        setDataOnView();
        return mRootView;
    }

    private void initView() {

        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        toolbar.setTitle(getArguments().getString(AppUtils.ITEM_NAME));

        collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView
                .findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        tvShipInfoDetail = (TextView) mRootView.findViewById(R.id.expandableTvShipInfoDetail);
        imgShipInfoDetailHeader = (ImageView) mRootView.findViewById(R.id.imgShipInfoDetailHeader);
    }

    private void setDataOnView() {
/*
        Bundle bundle = getArguments();
        // title
        messaging_toolbar.setTitle(bundle.getString(ShipInfoFragment.SHIP_INFO_CHILD_TITLE));


        // header image
        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        getArguments().getString(ShipInfoFragment.SHIP_INFO_CHILD_IMAGE_URL))
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(imgShipInfoDetailHeader);



        // detail
        //tvShipInfoDetail.setText(bundle.getString(ShipInfoFragment.SHIP_INFO_CHILD_SUBTITLE));
*/

        Picasso.with(getActivity())
                .load(R.drawable.img_sample_norrona_header)
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(imgShipInfoDetailHeader);

        tvShipInfoDetail.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                        "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud "
                        +
                        "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor "
                        +
                        "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
                        +
                        "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

    }
}
