package com.mcp.smyrilline.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.DestinationDetailsAdapter;
import com.mcp.smyrilline.interfaces.ViewDataBinder;
import com.mcp.smyrilline.model.destination.DestinationDetailsInfo;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;

import at.blogc.android.views.ExpandableTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/24/17.
 */

public class DestinationDetailsfragment extends Fragment implements View.OnClickListener, ViewDataBinder {

    private View _rootView;

    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private View mLoadingView;

    private View noInternetConnetionView;
    private Button retryInternetBtn;

    private Toolbar toolbar, noInternetViewToolbar;
    private RecyclerView infoRecyclerView;

    private NestedScrollView nestedScrollView;

    private TextView titleTextView, expandTextView;

    private ExpandableTextView detailsInfoTextView;

    private ImageView featureImage;

    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<DestinationDetailsInfo> call;

    private DestinationDetailsInfo info;

    private DestinationDetailsAdapter infoRecylerViewAdapter;

    private DestinationDetailsfragment thisClassContext = this;

    private LinearLayout detilsInfoLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_info_layout, container, false);

        retrofit = RetrofitClient.getClient();
        retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);

        initView();

        return _rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar(toolbar);

            call = retrofitInterfaces.fetchDestinationDetialsInfo(AppUtils.WP_PARAM_LANGUAGE,
                    getArguments().getString(AppUtils.DESTINATION_ID));
            fetchApiData();

        } else
            setWithoutInternetView();
    }

    private void setWithoutInternetView() {

        coordinatorLayout.setVisibility(View.GONE);

        mLoadingView.setVisibility(View.GONE);
        noInternetConnetionView.setVisibility(View.VISIBLE);
        noInternetViewToolbar.setVisibility(View.VISIBLE);

        setUpToolbar(noInternetViewToolbar);
    }

    private void fetchApiData() {


        call.enqueue(new Callback<DestinationDetailsInfo>() {
            @Override
            public void onResponse(Call<DestinationDetailsInfo> call, Response<DestinationDetailsInfo> response) {

                info = response.body();

                mLoadingView.setVisibility(View.GONE);

                infoRecylerViewAdapter = new DestinationDetailsAdapter(getActivity(), info.getChildren());

                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                infoRecyclerView.setLayoutManager(mLayoutManager);
                infoRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
                infoRecyclerView.setItemAnimator(new DefaultItemAnimator());
                infoRecyclerView.setAdapter(infoRecylerViewAdapter);

                setApiDataOnView();
            }

            @Override
            public void onFailure(Call<DestinationDetailsInfo> call, Throwable t) {
                setWithoutInternetView();
            }
        });

    }

    private void setApiDataOnView() {

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        info.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(featureImage);

        detailsInfoTextView.setText(info.getText1());

    }

    private void setUpToolbar(Toolbar toolbar) {


        toolbar.setTitle(getArguments().getString(AppUtils.DESTINATION_NAME));


    }

    private void initView() {

        coordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);

        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        nestedScrollView = (NestedScrollView) _rootView.findViewById(R.id.nested_scrollView);
        detilsInfoLayout = (LinearLayout) _rootView.findViewById(R.id.detils_info_layout);
        nestedScrollView.smoothScrollBy(0, detilsInfoLayout.getTop());

        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);
        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);


        noInternetConnetionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        mLoadingView = _rootView.findViewById(R.id.infoLoadingView);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        retryInternetBtn.setOnClickListener(this);

        infoRecyclerView = (RecyclerView) _rootView.findViewById(R.id.info_recycler_view);

        titleTextView = (TextView) _rootView.findViewById(R.id.title_textview);
        detailsInfoTextView = (ExpandableTextView) _rootView.findViewById(R.id.details_info_textview);
        expandTextView = (TextView) _rootView.findViewById(R.id.expand_TextView);

        featureImage = (ImageView) _rootView.findViewById(R.id.feature_image);

    }

    @Override
    public void handleExpandableTextViewListener() {


        ViewTreeObserver vto = detailsInfoTextView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    detailsInfoTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    detailsInfoTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                AppUtils.setVisibilityOfExpandTextview(detailsInfoTextView, expandTextView);

            }

        });


        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        detailsInfoTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailsInfoTextView.isExpanded()) {
                    detailsInfoTextView.collapse();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_expanded_time));
                    Log.i("textline", "onexpand " + detailsInfoTextView.getLineCount() + "");
                } else {
                    detailsInfoTextView.expand();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_collapse_time));
                    Log.i("textline", "oncollapse " + detailsInfoTextView.getLineCount() + "");
                }
            }
        });

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