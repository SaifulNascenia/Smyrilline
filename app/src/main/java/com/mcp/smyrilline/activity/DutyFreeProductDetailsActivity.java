package com.mcp.smyrilline.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.util.AppUtils;

import at.blogc.android.views.*;


/**
 * Created by saiful on 6/2/17.
 */

public class DutyFreeProductDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Ut volutpat interdum interdum. Nulla laoreet lacus diam, vitae " +
            "sodales sapien commodo faucibus. Vestibulum et feugiat enim. Donec " +
            "semper mi et euismod tempor. Sed sodales eleifend mi id varius. Nam " +
            "et ornare enim, sit amet gravida sapien. Quisque gravida et enim vel " +
            "volutpat. Vivamus egestas ut felis a blandit. Vivamus fringilla " +
            "dignissim mollis.dictum hendrerit ultrices. Ut vitae vestibulum dolor. Donec auctor ante" +
            " eget libero molestie porta. Nam tempor fringilla ultricies. Nam sem " +
            "lectus, feugiat eget ";

    private ExpandableTextView productDetailsTextView;
    private TextView toggleTextView;
    private Child dutyFreeItemObj;

    private TextView productNameTextview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.duty_free_product_details);

        initView();

        dutyFreeItemObj = getIntent().getParcelableExtra("dutyFreeItemObj");

        toolbar.setTitle("Tax Free Shop");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productDetailsTextView.setText(text);

        productDetailsTextView.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = productDetailsTextView.getLineCount();
                // Use lineCount here
                Log.i("textline", productDetailsTextView.getLineCount() + "");

                if (lineCount == 3) {
                    toggleTextView.setVisibility(View.VISIBLE);
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
