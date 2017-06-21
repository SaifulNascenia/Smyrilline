package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

import at.blogc.android.views.ExpandableTextView;

/**
 * Created by saiful on 6/21/17.
 */

public class FoodDetailsFragment extends Fragment implements View.OnClickListener {

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

    private FoodDetailsFragment thisClassContext = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.duty_free_product_details, container, false);

        initView();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            mLoadingView.setVisibility(View.GONE);
            setFoodDataOnView();

        } else {
            rootViewCoordinatorLayout.setVisibility(View.GONE);
            noInternetViewToolbar.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setTitle("Price and Menu");
            mLoadingView.setVisibility(View.GONE);
            noInternetConnetionView.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));


        }

        return _rootView;
    }

    private void setFoodDataOnView() {

        toolbar.setTitle("Price and Menu");
        toolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.smalltextColor));
        productNameTextview.setText(getArguments().getString("PRODUCT_NAME"));
        productPriceNumberTextview.setVisibility(View.GONE);
        productPriceTextView.setText(getArguments().getString("PRODUCT_PRICE"));
        productDetailsTextView.setText(getArguments().getString("PRODUCT_INFO"));

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        getArguments().getString("PRODUCT_IMAGE"))
                .placeholder(R.mipmap.ic_launcher)
                .into(productImageView);

        setProductDetailsTextViewExpandListener();

    }

    private void setProductDetailsTextViewExpandListener() {
        productDetailsTextView.post(new Runnable() {
            @Override
            public void run() {

                if (productDetailsTextView.getLineCount() == 3) {

                    // Use lineCount here
                    IndividualResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(0);

                    IndividualResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(0);

                    IndividualResturentDetailsFragment.firstLineText =
                            productDetailsTextView.getText().toString().
                                    substring(IndividualResturentDetailsFragment.startLineCount,
                                            IndividualResturentDetailsFragment.endLineCount);


                    IndividualResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(1);

                    IndividualResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(1);

                    IndividualResturentDetailsFragment.secondLineText =
                            productDetailsTextView.getText().toString().
                                    substring(IndividualResturentDetailsFragment.startLineCount,
                                            IndividualResturentDetailsFragment.endLineCount);

                    IndividualResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(2);

                    IndividualResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(2);

                    IndividualResturentDetailsFragment.thirdLineText =
                            productDetailsTextView.getText().toString().
                                    substring(IndividualResturentDetailsFragment.startLineCount,
                                            IndividualResturentDetailsFragment.endLineCount);

                    IndividualResturentDetailsFragment.totalThreeLineText =
                            IndividualResturentDetailsFragment.firstLineText +
                                    IndividualResturentDetailsFragment.secondLineText +
                                    IndividualResturentDetailsFragment.thirdLineText;


                    if (!(IndividualResturentDetailsFragment.totalThreeLineText.length() ==
                            getArguments().getString("PRODUCT_INFO").length())

                            ) {

                        expandTextView.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        productDetailsTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productDetailsTextView.isExpanded()) {
                    productDetailsTextView.collapse();
                    expandTextView.setText("view more");
                    Log.i("textline", "onexpand " + productDetailsTextView.getLineCount() + "");
                } else {
                    productDetailsTextView.expand();
                    expandTextView.setText("view less");
                    Log.i("textline", "oncollapse " + productDetailsTextView.getLineCount() + "");
                }
            }
        });


    }


    private void initView() {

        rootViewCoordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);

        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        productDetailsTextView = (ExpandableTextView) _rootView.findViewById(R.id.expandable_TextView);
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
}
