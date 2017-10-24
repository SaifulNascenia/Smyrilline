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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.blogc.android.views.ExpandableTextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.ShipInfoAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.shipinfo.ShipInfo;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.InternalStorage;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by raqib on 5/11/17.
 */

public class ShipInfoFragment extends Fragment {

    public static final String SHIP_INFO_CHILD_ID = "ship_info_child_id";
    public static final String SHIP_INFO_CHILD_TITLE = "ship_info_child_title";
    public static final String SHIP_INFO_CHILD_SUBTITLE = "ship_info_child_subtitle";
    public static final String SHIP_INFO_CHILD_DESCRIPTION = "ship_info_child_description";
    public static final String SHIP_INFO_CHILD_IMAGE_URL = "ship_info_child_image_url";

    private View mRootView;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View loadingProgressView;
    private View noConnectionView;
    private Button retryInternetBtn;
    private Toolbar toolbar, noConnectionToolbar;
    private RecyclerView shipInfoRecyclerView;
    private NestedScrollView nestedScrollView;
    private TextView tvShipInfoHeaderText, tvShipInfoDescriptionExpand;
    private ExpandableTextView tvShipInfoDescription;
    private ImageView featureImage;
    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<ShipInfo> call;
    private ShipInfo mShipInfo;
    private ShipInfoAdapter shipInfoAdapter;
    private ShipInfoFragment thisFragment = this;
    private LinearLayout detailsInfoLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_ship_info, container, false);
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
        detailsInfoLayout = (LinearLayout) mRootView.findViewById(R.id.details_info_layout);
        nestedScrollView.smoothScrollBy(0, detailsInfoLayout.getTop());
        loadingProgressView = mRootView.findViewById(R.id.loadingProgressView);

        // Toolbar
        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        toolbar.setTitle(R.string.ship_info);

        // No connection
        noConnectionToolbar = (Toolbar) mRootView.findViewById(R.id.extra_toolbar);
        noConnectionView = mRootView.findViewById(R.id.no_connection_layout);

        // Details
        featureImage = (ImageView) mRootView.findViewById(R.id.feature_image);
        tvShipInfoHeaderText = (TextView) mRootView.findViewById(R.id.tvShipInfoHeaderText);
        tvShipInfoDescription = (ExpandableTextView) mRootView
                .findViewById(R.id.tvShipInfoDescription);
        tvShipInfoDescriptionExpand = (TextView) mRootView.findViewById(R.id.expandTextView);

        // List view
        shipInfoRecyclerView = (RecyclerView) mRootView.findViewById(R.id.ship_info_recycler_view);
        shipInfoAdapter = new ShipInfoAdapter(getActivity(), new ArrayList());
        shipInfoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        shipInfoRecyclerView
                .addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
        shipInfoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        shipInfoRecyclerView.setAdapter(shipInfoAdapter);
        shipInfoRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                shipInfoRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        if (mShipInfo != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(AppUtils.ITEM_ID,
                                    mShipInfo.getChildren().get(position).getId());
                            bundle.putString(AppUtils.ITEM_NAME,
                                    mShipInfo.getChildren().get(position).getName());
                            bundle.putString(AppUtils.ITEM_HEADER,
                                    mShipInfo.getChildren().get(position).getHeader());
                            bundle.putString(AppUtils.ITEM_DESCRIPTION,
                                    mShipInfo.getChildren().get(position).getDescription());
                            bundle.putString(AppUtils.ITEM_IMAGE,
                                    mShipInfo.getChildren().get(position).getImageUrl());

                            ShipInfoDetailsFragment shipInfoDetailsFragment = new ShipInfoDetailsFragment();
                            shipInfoDetailsFragment.setArguments(bundle);

                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.content_frame, shipInfoDetailsFragment)
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
            call = retrofitInterfaces.fetchShipInfo(getString(R.string.wp_language_param));
            call.enqueue(new Callback<ShipInfo>() {
                @Override
                public void onResponse(Call<ShipInfo> call, Response<ShipInfo> response) {
                    mShipInfo = response.body();
                    loadingProgressView.setVisibility(View.GONE);
                    setApiDataOnView();
                    // save data
                    InternalStorage.writeObject(getActivity(), AppUtils.PREF_SHIP_INFO, mShipInfo);
                }

                @Override
                public void onFailure(Call<ShipInfo> call, Throwable t) {
                    AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                            loadingProgressView, coordinatorLayout, noConnectionView,
                            noConnectionToolbar,
                            getString(R.string.ship_info));
                }
            });
        } else
            AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                    loadingProgressView, coordinatorLayout, noConnectionView, noConnectionToolbar,
                    getString(R.string.ship_info));
    }

    private void setApiDataOnView() {
        coordinatorLayout.setVisibility(View.VISIBLE);
        shipInfoAdapter.setShipInfoList(mShipInfo.getChildren());
        shipInfoAdapter.notifyDataSetChanged();

        if (getActivity() != null) {
            Picasso.with(getActivity())
                    .load(getActivity().getResources().
                            getString(R.string.image_downloaded_base_url) +
                            mShipInfo.getImageUrl())
                    .placeholder(R.drawable.img_placeholder_thumb)
                    .into(featureImage);
        }

        AppUtils.setText(tvShipInfoHeaderText,
                "MS Norröna will sail you to Iceland and the Faroe Islands in style");
        AppUtils.setText(tvShipInfoDescription,
                "Sail with one of the newest ships in the North Atlantic. " +
                        "From the deck of M/S Norröna you get an incredible view over the horizon - waves, sea and the sky.");
        AppUtils.handleExpandableTextView(tvShipInfoDescription, tvShipInfoDescriptionExpand);
    }
}
