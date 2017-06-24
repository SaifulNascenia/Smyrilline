package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.model.DestinationAndShipInfoModel.Info;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.restaurentsmodel.ListOfRestaurent;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/24/17.
 */

public class DestinationAndShipInforFragment extends Fragment {

    private View _rootView;

    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private View mLoadingView;

    private View noInternetConnetionView;
    private Button retryInternetBtn;

    private Toolbar toolbar, noInternetViewToolbar;
    private RecyclerView infoRecyclerView;

    private TextView titleTextView, detailsInfoTextView;

    private Retrofit retrofit;
    private ApiInterfaces apiInterfaces;
    private Call<Info> call;

    private Info info;

    private RestaurantAdapter infoRecylerViewAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

            mLoadingView.setVisibility(View.GONE);
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


                infoRecylerViewAdapter = new RestaurantAdapter(getActivity(), info.getChildren(), null,
                        "DestinationAndShipInforFragment");

                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                infoRecyclerView.setLayoutManager(mLayoutManager);
                infoRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
                infoRecyclerView.setItemAnimator(new DefaultItemAnimator());
                infoRecyclerView.setAdapter(infoRecylerViewAdapter);


            }

            @Override
            public void onFailure(Call<Info> call, Throwable t) {

            }
        });

    }

    private void setUpToolbar(Toolbar toolbar) {

        if (getArguments().getString("CALLED_CLASS_NAME").equals(AppUtils.fragmentList[5])) {

            toolbar.setTitle(getArguments().getString("NAME"));
            toolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));

        } else {

            toolbar.setTitle("Ship Info");
            toolbar.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));
            ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        }


    }

    private void initView() {

        coordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);

        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);
        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);

        noInternetConnetionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        mLoadingView = _rootView.findViewById(R.id.infoLoadingView);

        infoRecyclerView = (RecyclerView) _rootView.findViewById(R.id.info_recycler_view);

        titleTextView = (TextView) _rootView.findViewById(R.id.title_textview);
        detailsInfoTextView = (TextView) _rootView.findViewById(R.id.details_info_textview);

    }
}
