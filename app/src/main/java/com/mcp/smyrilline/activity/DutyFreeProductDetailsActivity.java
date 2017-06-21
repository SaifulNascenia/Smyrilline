package com.mcp.smyrilline.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.fragment.IndividualResturentDetailsFragment;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.util.AppUtils;

import at.blogc.android.views.*;


/**
 * Created by saiful on 6/2/17.
 */

public class DutyFreeProductDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ExpandableTextView productDetailsTextView;
    private TextView toggleTextView;
    private Child dutyFreeItemObj;

    private TextView productNameTextview;
    private TextView productPriceNumberTextview;
    private TextView productPriceTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.duty_free_product_details);

        initView();

        dutyFreeItemObj = getIntent().getExtras().getParcelable("dutyFreeItemObj");
        Log.i("dutyobj", dutyFreeItemObj.getText3());

        toolbar.setTitle("Tax Free Shop");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productPriceTextView.setText("€ " + dutyFreeItemObj.getText3().
                substring(1, dutyFreeItemObj.getText3().indexOf(",")));


        productPriceNumberTextview.setText(dutyFreeItemObj.getText3().
                substring(dutyFreeItemObj.getText3().indexOf(",") + 1,
                        dutyFreeItemObj.getText3().length()));

        productNameTextview.setText(dutyFreeItemObj.getName());

        productDetailsTextView.setText(dutyFreeItemObj.getText1());

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


                /*Log.i("textline", totalThreeLineText);
                Log.i("textline", totalThreeLineText.length() + " " +
                        restaurentDetails.getOpenCloseTimeText().length() + "/n" + thirdLine);
*/

                    if (!(IndividualResturentDetailsFragment.totalThreeLineText.length() ==
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


        fetchDutyFreeItemDetails();

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
}
