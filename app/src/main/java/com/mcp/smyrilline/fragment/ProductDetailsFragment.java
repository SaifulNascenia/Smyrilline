package com.mcp.smyrilline.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.interfaces.ViewDataBinder;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

import at.blogc.android.views.ExpandableTextView;

/**
 * Created by saiful on 6/21/17.
 */

public class ProductDetailsFragment extends Fragment implements View.OnClickListener, ViewDataBinder {

    private View _rootView;

    private Toolbar toolbar, noInternetViewToolbar;
    private CoordinatorLayout rootViewCoordinatorLayout;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ExpandableTextView productDetailsTextView;
    private TextView expandTextView;
    private TextView productNameTextview;
    private TextView productPriceNumberTextview;
    private TextView productPriceTextView;

    private ImageView productImageView;

    private ProductDetailsFragment thisClassContext = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.duty_free_product_details, container, false);

        initView();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            mLoadingView.setVisibility(View.GONE);
            setDataOnView();

        } else {
            rootViewCoordinatorLayout.setVisibility(View.GONE);
            noInternetViewToolbar.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setTitle(getActivity().getResources().getString(R.string.price_and_menu));
            mLoadingView.setVisibility(View.GONE);
            noInternetConnetionView.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));


        }

        return _rootView;
    }

    private void setDataOnView() {

        toolbar.setTitle(getActivity().getResources().getString(R.string.price_and_menu));
        //toolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.smalltextColor));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.smalltextColor));
       /* AppCompatActivity appCompatActivity = (DrawerActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ((DrawerActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       */

        productNameTextview.setText(getArguments().getString(AppUtils.PRODUCT_NAME));
        productPriceTextView.setText(getArguments().getString(AppUtils.PRODUCT_PRICE));
        productDetailsTextView.setText(getArguments().getString(AppUtils.PRODUCT_INFO));
        // productDetailsTextView.setText(getActivity().getResources().getString(R.string.cheese_ipsum));

        if (getArguments().getString(AppUtils.CALLED_CLASS_NAME).equals(DutyFreeFragment.class.getSimpleName())) {

            productPriceNumberTextview.setText(getArguments().getString(AppUtils.PRODUCT_PRICE_NUMBER));

        } else {
            productPriceNumberTextview.setVisibility(View.GONE);
        }

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        getArguments().getString(AppUtils.PRODUCT_IMAGE))
                .placeholder(R.mipmap.ic_launcher)
                .into(productImageView);

        Bitmap bitmap = ((BitmapDrawable) productImageView.getDrawable()).getBitmap();
        Log.i("imagebitmap", bitmap + "");

        handleExpandableTextViewListener();

    }


    private void initView() {

        rootViewCoordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);

        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        productDetailsTextView = (ExpandableTextView) _rootView.findViewById(R.id.expandable_TextView);
        // productDetailsTextView = (TextView) _rootView.findViewById(R.id.expandable_TextView);
        expandTextView = (TextView) _rootView.findViewById(R.id.toggle_TextView);
        productNameTextview = (TextView) _rootView.findViewById(R.id.product_name_textview);
        productPriceNumberTextview = (TextView) _rootView.findViewById(R.id.product_price_number_textview);
        productPriceTextView = (TextView) _rootView.findViewById(R.id.product_price);
        productImageView = (ImageView) _rootView.findViewById(R.id.product_imageview);
        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);

        mLoadingView = _rootView.findViewById(R.id.restaurantsLoadingView);
        noInternetConnetionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        retryInternetBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.retry_internet:
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(thisClassContext).attach(thisClassContext).commit();
                break;
        }

    }

    @Override
    public void handleExpandableTextViewListener() {


        ViewTreeObserver vto = productDetailsTextView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    productDetailsTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    productDetailsTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                AppUtils.setVisibilityOfExpandTextview(productDetailsTextView, expandTextView);


              /*  if (getActivity().getResources().getString(R.string.cheese_ipsum).length() >
                        AppUtils.getEllipsisedThreeLineText(getActivity(), productDetailsTextView)
                                .length()) {
                    expandTextView.setVisibility(View.VISIBLE);
                }*/


            }

        });


        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        productDetailsTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productDetailsTextView.isExpanded()) {
                    productDetailsTextView.collapse();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_expanded_time));
                    Log.i("textline", "onexpand " + productDetailsTextView.getLineCount() + "");
                } else {
                    productDetailsTextView.expand();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_collapse_time));
                    Log.i("textline", "oncollapse " + productDetailsTextView.getLineCount() + "");
                }
            }
        });

    }
}
