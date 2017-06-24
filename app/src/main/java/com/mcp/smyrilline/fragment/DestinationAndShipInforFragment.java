package com.mcp.smyrilline.fragment;

import android.content.Context;
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
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.interfaces.BindDataWithView;
import com.mcp.smyrilline.model.DestinationAndShipInfoModel.Info;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.restaurentsmodel.ListOfRestaurent;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mcp.smyrilline.util.McpApplication.context;

/**
 * Created by saiful on 6/24/17.
 */

public class DestinationAndShipInforFragment extends Fragment implements View.OnClickListener, BindDataWithView {

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
    private ApiInterfaces apiInterfaces;
    private Call<Info> call;

    private Info info;

    private RestaurantAdapter infoRecylerViewAdapter;

    private DestinationAndShipInforFragment thisClassContext = this;

    private LinearLayout detilsInfoLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_info_layout, container, false);

        retrofit = ApiClient.getClient();
        apiInterfaces = retrofit.create(ApiInterfaces.class);

        initView();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            if (getArguments().getString("CALLED_CLASS_NAME").equals(AppUtils.fragmentList[5])) {

                call = apiInterfaces.fetchDestinationDetialsInfo(AppUtils.WP_PARAM_LANGUAGE,
                        getArguments().getString("ID"));

                fetchApiData();

            } else {

                call = apiInterfaces.fetchShipInfo(AppUtils.WP_PARAM_LANGUAGE);

                fetchApiData();
            }

            //
            setUpToolbar(toolbar);


        } else {
            coordinatorLayout.setVisibility(View.GONE);

            mLoadingView.setVisibility(View.GONE);

            noInternetConnetionView.setVisibility(View.VISIBLE);
            noInternetViewToolbar.setVisibility(View.VISIBLE);

            setUpToolbar(noInternetViewToolbar);

        }


        return _rootView;
    }

    private void fetchApiData() {


        call.enqueue(new Callback<Info>() {
            @Override
            public void onResponse(Call<Info> call, Response<Info> response) {

                info = response.body();

                mLoadingView.setVisibility(View.GONE);

                infoRecylerViewAdapter = new RestaurantAdapter(getActivity(), info.getChildren(), null,
                        "DestinationAndShipInforFragment");

                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                infoRecyclerView.setLayoutManager(mLayoutManager);
                infoRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
                infoRecyclerView.setItemAnimator(new DefaultItemAnimator());
                infoRecyclerView.setAdapter(infoRecylerViewAdapter);

                setApiDataOnView();


            }

            @Override
            public void onFailure(Call<Info> call, Throwable t) {

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
        Log.i("toolbarid", "on method" + toolbar.toString());


        if (getArguments().getString("CALLED_CLASS_NAME").equals(AppUtils.fragmentList[5])) {

            toolbar.setTitle(getArguments().getString("NAME"));
            /*toolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));
*/
        } else {

            toolbar.setTitle("Ship Info");
          /*  toolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));
          */
            ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        }


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
    public void setDataOnProductDetailsTextViewWithExpandTextViewListener() {


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


              /*  if (getActivity().getResources().getString(R.string.cheese_ipsum).length() >
                        AppUtils.getEllipsisedThreeLineText(getActivity(), productDetailsTextView)
                                .length()) {
                    expandTextView.setVisibility(View.VISIBLE);
                }*/


            }

        });


        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        detailsInfoTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailsInfoTextView.isExpanded()) {
                    detailsInfoTextView.collapse();
                    expandTextView.setText("view more");
                    Log.i("textline", "onexpand " + detailsInfoTextView.getLineCount() + "");
                } else {
                    detailsInfoTextView.expand();
                    expandTextView.setText("view less");
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
