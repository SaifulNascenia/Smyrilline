package com.mcp.smyrilline.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.view.ExpandableTextView;
import com.mcp.smyrilline.view.ExpandableTextView2;
import com.mcp.smyrilline.view.ExpandableTextView3;

/**
 * Created by saiful on 6/2/17.
 */

public class DutyFreeProductDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String yourText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Ut volutpat interdum interdum. Nulla laoreet lacus diam, vitae " +
            "sodales sapien commodo faucibus. Vestibulum et feugiat enim. Donec " +
            "semper mi et euismod tempor. Sed sodales eleifend mi id varius. Nam " +
            "et ornare enim, sit amet gravida sapien. Quisque gravida et enim vel " +
            "volutpat. Vivamus egestas ut felis a blandit. Vivamus fringilla " +
            "dignissim mollis. Maecenas imperdiet interdum hendrerit. Aliquam" +
            " dictum hendrerit ultrices. Ut vitae vestibulum dolor. Donec auctor ante" +
            " eget libero molestie porta. Nam tempor fringilla ultricies. Nam sem " +
            "lectus, feugiat eget ullamcorper vitae, ornare et sem. Fusce dapibus ipsum" +
            " sed laoreet suscipit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Ut volutpat interdum interdum. Nulla laoreet lacus diam, vitae " +
            "sodales sapien commodo faucibus. Vestibulum et feugiat enim. Donec " +
            "semper mi et euismod tempor. Sed sodales eleifend mi id varius. Nam " +
            "et ornare enim, sit amet gravida sapien. Quisque gravida et enim vel " +
            "volutpat. Vivamus egestas ut felis a blandit. Vivamus fringilla " +
            "dignissim mollis. Maecenas imperdiet interdum hendrerit. Aliquam" +
            " dictum hendrerit ultrices. Ut vitae vestibulum dolor. Donec auctor ante" +
            " eget libero molestie porta. Nam tempor fringilla ultricies. Nam sem " +
            "lectus, feugiat eget ullamcorper vitae, ornare et sem. Fusce dapibus ipsum" +
            " sed laoreet suscipit.Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Ut volutpat interdum interdum. Nulla laoreet lacus diam, vitae " +
            "sodales sapien commodo faucibus. Vestibulum et feugiat enim. Donec " +
            "semper mi et euismod tempor. Sed sodales eleifend mi id varius. Nam " +
            "et ornare enim, sit amet gravida sapien. Quisque gravida et enim vel " +
            "volutpat. Vivamus egestas ut felis a blandit. Vivamus fringilla " +
            "dignissim mollis. Maecenas imperdiet interdum hendrerit. Aliquam" +
            " dictum hendrerit ultrices. Ut vitae vestibulum dolor. Donec auctor ante" +
            " eget libero molestie porta. Nam tempor fringilla ultricies. Nam sem " +
            "lectus, feugiat eget ullamcorper vitae, ornare et sem. Fusce dapibus ipsum" +
            " sed laoreet suscipit.";

    private ExpandableTextView3 expandableTextView;

    private TextView tv;

    private com.ms.square.android.expandabletextview.ExpandableTextView exptv;


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

        exptv = (com.ms.square.android.expandabletextview.ExpandableTextView) findViewById(R.id.expand_text_view);
        exptv.setText(yourText);

      /*  tv = (TextView) findViewById(R.id.expand_text1);
        tv.setText(yourText);*/
        //makeTextViewResizable(tv, 3, "View More", true);






        /*expandableTextView = (ExpandableTextView3) findViewById(R.id.expand_text);
        expandableTextView.setText(yourText);*/


    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}
