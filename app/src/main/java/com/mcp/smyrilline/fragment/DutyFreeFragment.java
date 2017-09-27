package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.dutyfree.DutyFree;
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
 * Created by raqib on 5/11/17.
 */

public class DutyFreeFragment extends Fragment {

    private View mRootView;
    private Toolbar toolbar;
    private Toolbar noConnectionToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView dutyFreeRecyclerView;
    private DutyFreeAdapter dutyFreeAdapter;
    private DutyFree mDutyFree;
    private CoordinatorLayout coordinatorLayout;
    private View noConnectionView;
    private View loadingProgressView;
    private TextView timeTextview;
    private ImageView shopImageView;
    private Fragment thisFragment = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_duty_free, container, false);
        initView();
        fetchDutyFreeProductList();
        return mRootView;
    }

    private void initView() {
        // toolbar
        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        toolbar.setTitle(getActivity().getResources().getString(R.string.duty_free));

        coordinatorLayout = (CoordinatorLayout) mRootView
          .findViewById(R.id.root_coordinator_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView
          .findViewById(R.id.collapsing_toolbar);
        noConnectionView = mRootView.findViewById(R.id.no_connection_layout);
        noConnectionToolbar = (Toolbar) mRootView.findViewById(R.id.extra_toolbar);
        loadingProgressView = mRootView.findViewById(R.id.loadingProgressView);
        timeTextview = (TextView) mRootView.findViewById(R.id.time_textview);
        shopImageView = (ImageView) mRootView.findViewById(R.id.shop_image);

        // recycler view
        dutyFreeRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        dutyFreeRecyclerView.setLayoutManager(mLayoutManager);
        dutyFreeRecyclerView
          .addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
        dutyFreeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dutyFreeRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                dutyFreeRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID,
                          mDutyFree.getChildren().get(position).getId());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME,
                          mDutyFree.getChildren().get(position).getName());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE,
                          "€ " + mDutyFree.getChildren().get(position).getPrice().
                                        substring(1,
                                          mDutyFree.getChildren().get(position).getPrice()
                                                        .indexOf(","))
                        );

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE_NUMBER,
                          mDutyFree.getChildren().get(position).getPrice().
                            substring(
                              mDutyFree.getChildren().get(position).getPrice().indexOf(",") + 1,
                              mDutyFree.getChildren().get(position).getPrice().length())
                        );


                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO,
                          mDutyFree.getChildren().get(position).getDescription());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE,
                          mDutyFree.getChildren().get(position).getImageUrl());

                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME, DutyFreeFragment.class.getSimpleName());

                        DutyFreeDetailsFragment dutyFreeDetailsFragment = new DutyFreeDetailsFragment();
                        dutyFreeDetailsFragment.setArguments(AppUtils.getBundleObj());

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                          .replace(R.id.content_frame, dutyFreeDetailsFragment)
                                .commit();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    private void fetchDutyFreeProductList() {
        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<DutyFree> call = retrofitInterfaces.fetchDutyFreeProductsList(AppUtils.WP_PARAM_LANGUAGE);

        call.enqueue(new Callback<DutyFree>() {
            @Override
            public void onResponse(Call<DutyFree> call, Response<DutyFree> response) {
                try {
                    mDutyFree = response.body();
                    loadApiDataOnView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DutyFree> call, Throwable t) {
                AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                  loadingProgressView, coordinatorLayout, noConnectionView,
                  noConnectionToolbar, getString(R.string.duty_free));
            }
        });
    }

    private void loadApiDataOnView() {
        loadingProgressView.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);
        coordinatorLayout.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setTitleEnabled(false);
        dutyFreeAdapter = new DutyFreeAdapter(getActivity(), mDutyFree.getChildren(),
                DutyFreeFragment.class.getSimpleName());
        dutyFreeRecyclerView.setAdapter(dutyFreeAdapter);
        timeTextview.setText(mDutyFree.getHeader());
        Picasso.with(getActivity())
          .load(getActivity().getResources().
            getString(R.string.image_downloaded_base_url) +
            mDutyFree.getImageUrl())
          .placeholder(R.drawable.img_placeholder_thumb)
          .into(shopImageView);
    }
}
