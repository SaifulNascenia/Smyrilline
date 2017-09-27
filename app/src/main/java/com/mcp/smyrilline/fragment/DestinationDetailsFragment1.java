package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.blogc.android.views.ExpandableTextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DestinationDetailsAdapter1;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.destination.DestinationDetails;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/24/17.
 */

public class DestinationDetailsFragment1 extends Fragment {

    private View mRootView;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View loadingProgressView;
    private View noConnectionView;
    private Toolbar toolbar, noConnectionToolbar;
    private RecyclerView mDestinationDetailRecyclerView;
    private NestedScrollView nestedScrollView;
    private TextView tvDetailsHeader, tvDetailsDescriptionMoreView;
    private ExpandableTextView tvDetailsExpandableDescription;
    private ImageView featureImage;
    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<DestinationDetails> call;
    private DestinationDetails mDestinationDetails;
    private DestinationDetailsAdapter1 mRecyclerViewAdapter;
    private DestinationDetailsFragment1 thisFragment = this;
    private RelativeLayout detailsInfoLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_destination_details_1, container, false);
        initView();
        fetchApiData();
        return mRootView;
    }

    private void initView() {
        coordinatorLayout = (CoordinatorLayout) mRootView
                .findViewById(R.id.root_coordinator_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView
                .findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        nestedScrollView = (NestedScrollView) mRootView.findViewById(R.id.nested_scrollView);
        detailsInfoLayout = (RelativeLayout) mRootView.findViewById(R.id.details_info_layout);
        nestedScrollView.smoothScrollBy(0, detailsInfoLayout.getTop());
        loadingProgressView = mRootView.findViewById(R.id.loadingProgressView);

        // toolbar
        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        toolbar.setTitle(getArguments().getString(AppUtils.ITEM_NAME));

        // content title, description, image
        tvDetailsHeader = (TextView) mRootView.findViewById(R.id.tvDestinationHeaderText);
        tvDetailsExpandableDescription = (ExpandableTextView) mRootView
                .findViewById(R.id.tvDestinationDetailsText);
        tvDetailsDescriptionMoreView = (TextView) mRootView
                .findViewById(R.id.tvDestinationDetailsMore);
        featureImage = (ImageView) mRootView.findViewById(R.id.destinationDetailsFeatureImage);

        // no connection
        noConnectionView = mRootView.findViewById(R.id.no_connection_layout);
        noConnectionToolbar = (Toolbar) mRootView.findViewById(R.id.extra_toolbar);

        // recycler view
        mDestinationDetailRecyclerView = (RecyclerView) mRootView
                .findViewById(R.id.destination_detail_1_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mDestinationDetailRecyclerView.setLayoutManager(mLayoutManager);
        mDestinationDetailRecyclerView
                .addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
        mDestinationDetailRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDestinationDetailRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        mDestinationDetailRecyclerView,
                        new RecylerViewItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                if (mDestinationDetails != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(AppUtils.ITEM_ID,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getId());
                                    bundle.putString(AppUtils.ITEM_NAME,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getName());
                                    bundle.putString(AppUtils.ITEM_HEADER,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getText1());
                                    bundle.putString(AppUtils.ITEM_SUBHEADER,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getText2());
                                    bundle.putString(AppUtils.ITEM_DESCRIPTION,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getText3());
                                    bundle.putString(AppUtils.ITEM_IMAGE,
                                            mDestinationDetails.getChildren().get(position)
                                                    .getImageUrl());

                                    DestinationDetailsFragment2 destinationDetailsFragment2 = new DestinationDetailsFragment2();
                                    destinationDetailsFragment2.setArguments(bundle);

                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .addToBackStack(null)
                                            .replace(R.id.content_frame,
                                                    destinationDetailsFragment2)
                                            .commit();
                                }
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));
    }

    private void fetchApiData() {
        if (AppUtils.isNetworkAvailable(getActivity())) {
            retrofit = RetrofitClient.getClient();
            retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
            call = retrofitInterfaces.fetchDestinationDetialsInfo(AppUtils.WP_PARAM_LANGUAGE,
                    getArguments().getString(AppUtils.ITEM_ID));
            call.enqueue(new Callback<DestinationDetails>() {
                @Override
                public void onResponse(Call<DestinationDetails> call,
                        Response<DestinationDetails> response) {
                    mDestinationDetails = response.body();
                    loadingProgressView.setVisibility(View.GONE);
                    setApiDataOnView();
                }

                @Override
                public void onFailure(Call<DestinationDetails> call, Throwable t) {
                    AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                            loadingProgressView, coordinatorLayout, noConnectionView,
                            noConnectionToolbar, getArguments().getString(AppUtils.ITEM_NAME));
                }
            });
        } else
            AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                    loadingProgressView, coordinatorLayout, noConnectionView,
                    noConnectionToolbar, getArguments().getString(AppUtils.ITEM_NAME));
    }

    private void setApiDataOnView() {
        coordinatorLayout.setVisibility(View.VISIBLE);
        mRecyclerViewAdapter = new DestinationDetailsAdapter1(getActivity(),
                mDestinationDetails.getChildren());
        mDestinationDetailRecyclerView.setAdapter(mRecyclerViewAdapter);

        // feature image
        if (getActivity() != null) {
            Picasso.with(getActivity())
                    .load(getActivity().getResources().
                            getString(R.string.image_downloaded_base_url) +
                            mDestinationDetails.getImageUrl())
                    .placeholder(R.drawable.img_placeholder_thumb)
                    .into(featureImage);
        }
//        AppUtils.setText(tvDetailsHeader, mDestinationDetails.getHeader());
//        AppUtils.setText(tvDetailsExpandableDescription, mDestinationDetails.getDescription());
        AppUtils.setText(tvDetailsHeader,
                "MS Norröna will sail you to Iceland and the Faroe Islands in style");
        AppUtils.setText(tvDetailsExpandableDescription,
                "Sail with one of the newest ships in the North Atlantic. " +
                        "From the deck of M/S Norröna you get an incredible view over the horizon - waves, sea and the sky.");
        AppUtils.handleExpandableTextView(tvDetailsExpandableDescription,
                tvDetailsDescriptionMoreView);
    }
}