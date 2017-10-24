package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.mcp.smyrilline.adapter.DestinationDetailsAdapter2;
import com.mcp.smyrilline.model.destination.DestinationDetails;
import com.mcp.smyrilline.model.destination.DestinationDetailsChild;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/24/17.
 */

public class DestinationDetailsFragment2 extends Fragment implements View.OnClickListener {
/*

    private static final String DESTINATION_DETAILS_ID = "destination_details_id";
    private static final String DESTINATION_DETAILS_TITLE = "destination_details_title";
    private static final String DESTINATION_DETAILS_SUBTITLE = "destination_details_subtitle";
    private static final String DESTINATION_DETAILS_DESCRIPTION = "destination_details_description";
    private static final String DESTINATION_DETAILS_IMAGE_URL = "destination_details_image_url";
*/

    private View mRootView;
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View loadingProgressView;
    private View noConnectionView;
    private Button retryInternetBtn;
    private Toolbar toolbar, noInternetViewToolbar;
    private RecyclerView mDestinationDetailRecyclerView;
    private NestedScrollView nestedScrollView;
    private TextView tvDetailsHeader, tvDetailsDescriptionMoreView;
    private ExpandableTextView tvDetailsExpandableDescription;
    private ImageView featureImage;
    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<DestinationDetails> call;
    private DestinationDetails mDestinationDetails;
    private DestinationDetailsAdapter2 mDestinationDetailsAdapter;
    private DestinationDetailsFragment2 thisClassContext = this;
    private LinearLayout detailsInfoLayout;
    private List<DestinationDetailsChild> mDestinationDetailsList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_destination_details_2, container, false);
        initView();
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

        // messaging_toolbar
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
        noInternetViewToolbar = (Toolbar) mRootView.findViewById(R.id.extra_toolbar);
        retryInternetBtn = (Button) mRootView.findViewById(R.id.retry_connect);
        retryInternetBtn.setOnClickListener(this);

        // recycler view
        mDestinationDetailRecyclerView = (RecyclerView) mRootView
                .findViewById(R.id.destination_detail_2_recycler_view);
        mDestinationDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDestinationDetailRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDestinationDetailsList = new ArrayList<>();
        mDestinationDetailsAdapter = new DestinationDetailsAdapter2(getActivity(),
                mDestinationDetailsList);
        mDestinationDetailRecyclerView.setAdapter(mDestinationDetailsAdapter);

        // ==================   load dummy data [REMOVE for release] ===================

        mDestinationDetails = new DestinationDetails();
        mDestinationDetails.setName("Transport only");
        mDestinationDetails.setText1("Travel to Iceland in your own VEHICLE");
        mDestinationDetails.setText3("It is always fun and exciting to plan a special vacation " +
                "expecially when the destination is a country with so much to offer." +
                "\n" + "Beside our custom made packages completing the full circle, " +
                "we also have offers for transportation only, whether you are travelling with CAR, MC or CAMPER, "
                +
                "that allows you to be spontaneous. Below you find our most interesting offers");

        mDestinationDetailsList.add(new DestinationDetailsChild("With your own CAR",
                "/uploads/with-your-car.jpg",
                null,
                null,
                "For travellers who like to go on holiday with their family & friends"));

        mDestinationDetailsList.add(new DestinationDetailsChild("With your own CAMPER",
                "/uploads/with-your-camper.jpg",
                null,
                null,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."));

        mDestinationDetailsList.add(new DestinationDetailsChild("With your own MC",
                "/uploads/MC-Iceland-road.jpg",
                null,
                null,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."));

        mDestinationDetailsList.add(new DestinationDetailsChild("On FOOT",
                "/uploads/Godafoss-man.jpg",
                null,
                null,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."));

        mDestinationDetailsAdapter.notifyDataSetChanged();

        // feature image
        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        "/uploads/Destination3.png")
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(featureImage);

        AppUtils.setText(tvDetailsHeader, mDestinationDetails.getText1());
        AppUtils.setText(tvDetailsExpandableDescription, mDestinationDetails.getText3());
        AppUtils.handleExpandableTextView(tvDetailsExpandableDescription,
                tvDetailsDescriptionMoreView);

        loadingProgressView.setVisibility(View.GONE);

        // ==================   load dummy data [REMOVE for release] ===================
    }

    private void setWithoutInternetView() {
        coordinatorLayout.setVisibility(View.GONE);
        loadingProgressView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
        noInternetViewToolbar.setVisibility(View.VISIBLE);
    }

    private void fetchApiData() {
        retrofit = RetrofitClient.getClient();
        retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        call = retrofitInterfaces.fetchDestinationDetialsInfo(getString(R.string.wp_language_param),
                getArguments().getString(AppUtils.DESTINATION_ID));
        call.enqueue(new Callback<DestinationDetails>() {
            @Override
            public void onResponse(Call<DestinationDetails> call,
                    Response<DestinationDetails> response) {
                mDestinationDetails = response.body();
                loadingProgressView.setVisibility(View.GONE);
                setApiDataOnView(mDestinationDetails);
            }

            @Override
            public void onFailure(Call<DestinationDetails> call, Throwable t) {
                setWithoutInternetView();
            }
        });
    }

    private void setApiDataOnView(DestinationDetails destinationDetails) {
        mDestinationDetailsList = destinationDetails.getChildren();
        mDestinationDetailsAdapter.notifyDataSetChanged();

        // feature image
        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        this.mDestinationDetails.getImageUrl())
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(featureImage);

        tvDetailsExpandableDescription.setText(mDestinationDetails.getText3());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_connect:
                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                ft.detach(thisClassContext).attach(thisClassContext).commit();
                break;
        }
    }
}