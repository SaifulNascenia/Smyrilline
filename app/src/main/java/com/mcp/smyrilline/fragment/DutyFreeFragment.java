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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    private View _rootView;
    private Toolbar toolbar;
    private Toolbar noInternetViewToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RecyclerView dutyFreeRecyclerView;
    private DutyFreeAdapter dutyFreeAdapter;

    private DutyFree dutyFree;

    private CoordinatorLayout coordinatorLayout;
    private View noInternetConnetionView;

    private Button retryInternetBtn;
    private View dutyFreeLoadingProgressBar;
    private RelativeLayout rootLayout;
    private TextView timeTextview;

    private ImageView shopImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_duty_free, container, false);

        initView();

        dutyFreeRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                dutyFreeRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID,
                                dutyFree.getChildren().get(position).getId());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME,
                                dutyFree.getChildren().get(position).getName());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE,
                                "€ " + dutyFree.getChildren().get(position).getPrice().
                                        substring(1,
                                                dutyFree.getChildren().get(position).getPrice()
                                                        .indexOf(","))
                        );

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE_NUMBER,
                                dutyFree.getChildren().get(position).getPrice().
                                        substring(dutyFree.getChildren().get(position).getPrice().indexOf(",") + 1,
                                                dutyFree.getChildren().get(position).getPrice().length())
                        );


                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO,
                                dutyFree.getChildren().get(position).getDescription());

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE,
                                dutyFree.getChildren().get(position).getImageUrl());

                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME, DutyFreeFragment.class.getSimpleName());

                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        productDetailsFragment.setArguments(AppUtils.getBundleObj());

                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, productDetailsFragment)
                                .commit();


                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        fetchDutyFreeProductList();

        return _rootView;
    }

    private void initView() {

        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);
        coordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);
        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        dutyFreeRecyclerView = (RecyclerView) _rootView.findViewById(R.id.recycler_view);
        noInternetConnetionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        dutyFreeLoadingProgressBar = _rootView.findViewById(R.id.dutyFreeLoadingView);
        rootLayout = (RelativeLayout) _rootView.findViewById(R.id.root_layout);
        timeTextview = (TextView) _rootView.findViewById(R.id.time_textview);
        shopImageView = (ImageView) _rootView.findViewById(R.id.shop_image);
    }

    private void fetchDutyFreeProductList() {
        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<DutyFree> call = retrofitInterfaces.fetchDutyFreeProductsList(AppUtils.WP_PARAM_LANGUAGE);

        call.enqueue(new Callback<DutyFree>() {
            @Override
            public void onResponse(Call<DutyFree> call, Response<DutyFree> response) {
                try {

                    dutyFree = response.body();
                    toolbar.setTitle(getActivity().getResources().getString(R.string.tax_free_shop_title));
                    ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
                    collapsingToolbarLayout.setTitleEnabled(false);

                    dutyFreeLoadingProgressBar.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);

                    timeTextview.setText(dutyFree.getHeader());


                    Picasso.with(getActivity())
                            .load(getActivity().getResources().
                                    getString(R.string.image_downloaded_base_url) +
                                    dutyFree.getImageUrl())
                            .placeholder(R.mipmap.ic_launcher)
                            .into(shopImageView);

                    prepareDutyFreeRecyclerViewAttributes();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<DutyFree> call, Throwable t) {

                dutyFreeLoadingProgressBar.setVisibility(View.GONE);
                noInternetViewToolbar.setVisibility(View.VISIBLE);
                noInternetViewToolbar.setTitle(getActivity().getResources().getString(R.string.tax_free_shop_title));
                ((DrawerActivity) getActivity()).setToolbarAndToggle(noInternetViewToolbar);
                noInternetConnetionView.setVisibility(View.VISIBLE);
                AppUtils.withoutInternetConnectionView(getActivity(),
                        getActivity().getIntent(),
                        retryInternetBtn,
                        DutyFreeFragment.class.getSimpleName());
                rootLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.windowBackground));
            }
        });


    }


    private void prepareDutyFreeRecyclerViewAttributes() {


        dutyFreeAdapter = new DutyFreeAdapter(getActivity(), dutyFree.getChildren(),
                DutyFreeFragment.class.getSimpleName());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        dutyFreeRecyclerView.setLayoutManager(mLayoutManager);
        dutyFreeRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, AppUtils.dpToPx(10), true));
        dutyFreeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dutyFreeRecyclerView.setAdapter(dutyFreeAdapter);

    }


}
