package com.mcp.smyrilline.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import at.blogc.android.views.ExpandableTextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by saiful on 6/21/17.
 */

public class DutyFreeDetailsFragment extends Fragment implements View.OnClickListener {

    private View mRootView;

    private Toolbar toolbar, noInternetViewToolbar;
    private CoordinatorLayout rootViewCoordinatorLayout;

    private View loadingProgressView;
    private View noConnectionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ExpandableTextView productDetailsExpandableTextView;
    private TextView productDetailsMoreView;
    private TextView productNameTextview;
    private TextView pennyValueOfProductPriceTextview;
    private TextView euroValueOfProductPriceTextview;

    private ImageView productImageView;

    private DutyFreeDetailsFragment thisClassContext = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_duty_free_details, container, false);
        initView();
        setDataOnView();
        return mRootView;
    }

    private void initView() {

        rootViewCoordinatorLayout = (CoordinatorLayout) mRootView
                .findViewById(R.id.root_coordinator_layout);

        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        toolbar.setTitle(getActivity().getResources().getString(R.string.price_and_menu));

        collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView
                .findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        productDetailsExpandableTextView = (ExpandableTextView) mRootView
                .findViewById(R.id.productDetailsExpandableTextView);
        // productDetailsExpandableTextView = (TextView) mRootView.findViewById(R.id.expandable_TextView);
        productDetailsMoreView = (TextView) mRootView.findViewById(R.id.productDestailsMoreView);
        productNameTextview = (TextView) mRootView.findViewById(R.id.product_name_textview);
        pennyValueOfProductPriceTextview = (TextView) mRootView
                .findViewById(R.id.penny_value_of_product_price_textview);
        euroValueOfProductPriceTextview = (TextView) mRootView
                .findViewById(R.id.euro_value_of_product_price_textview);
        productImageView = (ImageView) mRootView.findViewById(R.id.imgShipInfoDetailHeader);
        noInternetViewToolbar = (Toolbar) mRootView.findViewById(R.id.extra_toolbar);

        loadingProgressView = mRootView.findViewById(R.id.restaurantsLoadingView);
        noConnectionView = mRootView.findViewById(R.id.no_connection_layout);
        retryInternetBtn = (Button) mRootView.findViewById(R.id.retry_connect);
        retryInternetBtn.setOnClickListener(this);
    }

    private void setDataOnView() {
        productNameTextview.setText(getArguments().getString(AppUtils.PRODUCT_NAME));
        euroValueOfProductPriceTextview.setText(getArguments().getString(AppUtils.PRODUCT_PRICE));
        productDetailsExpandableTextView.setText(getArguments().getString(AppUtils.PRODUCT_INFO));

        if (getArguments().getString(AppUtils.CALLED_CLASS_NAME)
                .equals(DutyFreeFragment.class.getSimpleName())) {
            pennyValueOfProductPriceTextview
                    .setText(getArguments().getString(AppUtils.PRODUCT_PRICE_NUMBER));
        } else {
            pennyValueOfProductPriceTextview.setVisibility(View.GONE);
        }

        if (AppUtils.isNetworkAvailable(getActivity())) {
            Picasso.with(getActivity())
                    .load(getActivity().getResources().
                            getString(R.string.image_downloaded_base_url) +
                            getArguments().getString(AppUtils.PRODUCT_IMAGE))
                    .placeholder(R.drawable.img_placeholder_thumb)
                    .into(productImageView);
        } else {
            rootViewCoordinatorLayout.setVisibility(View.GONE);
            noInternetViewToolbar.setVisibility(View.VISIBLE);
            noInternetViewToolbar
                    .setTitle(getActivity().getResources().getString(R.string.price_and_menu));
            loadingProgressView.setVisibility(View.GONE);
            noConnectionView.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.transparent));
        }

        Bitmap bitmap = ((BitmapDrawable) productImageView.getDrawable()).getBitmap();
        Log.i("imagebitmap", bitmap + "");
        AppUtils.handleExpandableTextView(productDetailsExpandableTextView, productDetailsMoreView);
        loadingProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_connect:
                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                ft.detach(thisClassContext).attach(thisClassContext).commit();
                break;
        }
    }
}
