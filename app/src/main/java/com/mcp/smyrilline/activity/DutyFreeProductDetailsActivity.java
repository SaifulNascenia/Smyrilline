package com.mcp.smyrilline.activity;


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
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.mcp.smyrilline.R;

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
    TextView toggleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.duty_free_product_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tax Free Shop");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productDetailsTextView = (ExpandableTextView) findViewById(R.id.expandable_TextView);
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
        toggleTextView = (TextView) findViewById(R.id.toggle_TextView);
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

}
