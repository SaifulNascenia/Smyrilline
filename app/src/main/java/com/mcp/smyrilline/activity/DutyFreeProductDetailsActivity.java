package com.mcp.smyrilline.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.ResturentDetailsFragment;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

import at.blogc.android.views.*;


/**
 * Created by saiful on 6/2/17.
 */

public class DutyFreeProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar, noInternetViewToolbar;
    private CoordinatorLayout rootViewCoordinatorLayout;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ExpandableTextView productDetailsTextView;
    private TextView toggleTextView;
    private Child dutyFreeItemObj;

    private TextView productNameTextview;
    private TextView productPriceNumberTextview;
    private TextView productPriceTextView;

    private ImageView productImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.duty_free_product_details);

        initView();

        mLoadingView.setVisibility(View.GONE);


        dutyFreeItemObj = getIntent().getExtras().getParcelable("dutyFreeItemObj");
        Log.i("dutyobj", dutyFreeItemObj.getText3());

        toolbar.setTitle("Tax Free Shop");
        toolbar.setTitleTextColor(getResources().getColor(R.color.smalltextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Picasso.with(DutyFreeProductDetailsActivity.this)
                .load(getResources().
                        getString(R.string.image_downloaded_base_url) +
                        dutyFreeItemObj.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(productImageView);


        productPriceTextView.setText("€ " + dutyFreeItemObj.getText3().
                substring(1, dutyFreeItemObj.getText3().indexOf(",")));


        productPriceNumberTextview.setText(dutyFreeItemObj.getText3().
                substring(dutyFreeItemObj.getText3().indexOf(",") + 1,
                        dutyFreeItemObj.getText3().length()));

        productNameTextview.setText(dutyFreeItemObj.getName());

        productDetailsTextView.setText(dutyFreeItemObj.getText1());

        setProductDetailsTextViewExpandListener();


        fetchDutyFreeItemDetails();

    }

    private void setProductDetailsTextViewExpandListener() {

        productDetailsTextView.post(new Runnable() {
            @Override
            public void run() {

               /* int lineCount = productDetailsTextView.getLineCount();
                // Use lineCount here
                Log.i("textline", productDetailsTextView.getLineCount() + "");

                if (lineCount == 3) {
                    toggleTextView.setVisibility(View.VISIBLE);
                }*/


                if (productDetailsTextView.getLineCount() == 3) {

                    // Use lineCount here


                    ResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(0);

                    ResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(0);

                    ResturentDetailsFragment.firstLineText =
                            productDetailsTextView.getText().toString().
                                    substring(ResturentDetailsFragment.startLineCount,
                                            ResturentDetailsFragment.endLineCount);


                    ResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(1);

                    ResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(1);

                    ResturentDetailsFragment.secondLineText =
                            productDetailsTextView.getText().toString().
                                    substring(ResturentDetailsFragment.startLineCount,
                                            ResturentDetailsFragment.endLineCount);

                    ResturentDetailsFragment.startLineCount =
                            productDetailsTextView.getLayout().getLineStart(2);

                    ResturentDetailsFragment.endLineCount =
                            productDetailsTextView.getLayout().getLineEnd(2);

                    ResturentDetailsFragment.thirdLineText =
                            productDetailsTextView.getText().toString().
                                    substring(ResturentDetailsFragment.startLineCount,
                                            ResturentDetailsFragment.endLineCount);

                    ResturentDetailsFragment.totalThreeLineText =
                            ResturentDetailsFragment.firstLineText +
                                    ResturentDetailsFragment.secondLineText +
                                    ResturentDetailsFragment.thirdLineText;


                /*Log.i("textline", totalThreeLineText);
                Log.i("textline", totalThreeLineText.length() + " " +
                        restaurentDetails.getOpenCloseTimeText().length() + "/n" + thirdLine);
*/

                    if (!(ResturentDetailsFragment.totalThreeLineText.length() ==
                            dutyFreeItemObj.getText1().length())

                            ) {

                        toggleTextView.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        productDetailsTextView.setAnimationDuration(300L);
        toggleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productDetailsTextView.isExpanded()) {
                    productDetailsTextView.collapse();
                    toggleTextView.setText("view more");
                    Log.i("textline", "onexpand " + productDetailsTextView.getLineCount() + "");
                } else {
                    productDetailsTextView.expand();
                    toggleTextView.setText("view less");
                    Log.i("textline", "oncollapse " + productDetailsTextView.getLineCount() + "");
                }
            }
        });


    }

    private void fetchDutyFreeItemDetails() {


    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        productDetailsTextView = (ExpandableTextView) findViewById(R.id.expandable_TextView);
        toggleTextView = (TextView) findViewById(R.id.toggle_TextView);
        productNameTextview = (TextView) findViewById(R.id.product_name_textview);
        productPriceNumberTextview = (TextView) findViewById(R.id.product_price_number_textview);
        productPriceTextView = (TextView) findViewById(R.id.product_price);
        productImageView = (ImageView) findViewById(R.id.product_imageview);

        noInternetViewToolbar = (Toolbar) findViewById(R.id.extra_toolbar);

        mLoadingView = findViewById(R.id.restaurantsLoadingView);
        noInternetConnetionView = findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) findViewById(R.id.retry_internet);
        retryInternetBtn.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.setClass(this, DrawerActivity.class);
                intent.putExtra(AppUtils.START_DRAWER_FRAGMENT, AppUtils.fragmentList[3]);
                startActivity(intent);
                // close this activity
                finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
