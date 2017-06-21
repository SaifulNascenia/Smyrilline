package com.mcp.smyrilline.fragment;

import android.content.res.Resources;
import android.graphics.Rect;
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
import android.util.TypedValue;
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
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.interfaces.ClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.dutyfreemodels.DutyFree;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;
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
    private Toolbar extraToolbar;
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
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        /*Child dutyFreeItemObj = dutyFree.getChildren().get(position);
                        startActivity(new Intent(getActivity(), DutyFreeProductDetailsActivity.class)
                                .putExtra("dutyFreeItemObj", dutyFreeItemObj));
*/
                        Bundle bundle = new Bundle();

                        bundle.putString("PRODUCT_ID",
                                dutyFree.getChildren().get(position).getId());

                        bundle.putString("PRODUCT_NAME",
                                dutyFree.getChildren().get(position).getName());

                        bundle.putString("PRODUCT_PRICE",
                                "€ " + dutyFree.getChildren().get(position).getText3().
                                        substring(1,
                                                dutyFree.getChildren().get(position).getText3()
                                                        .indexOf(","))
                        );

                        bundle.putString("PRODUCT_PRICE_NUMBER", dutyFree.getChildren().get(position).getText3().
                                substring(dutyFree.getChildren().get(position).getText3().indexOf(",") + 1,
                                        dutyFree.getChildren().get(position).getText3().length())
                        );


                        bundle.putString("PRODUCT_INFO",
                                dutyFree.getChildren().get(position).getText1());

                        bundle.putString("PRODUCT_IMAGE",
                                dutyFree.getChildren().get(position).getImageUrl());

                        bundle.putString("USED_CLASS_NAME", AppUtils.fragmentList[3]);

                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        productDetailsFragment.setArguments(bundle);

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


        loadDutyFreeproductList();


        return _rootView;
    }

    private void initView() {

        extraToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);
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

    private void loadDutyFreeproductList() {
        Retrofit retrofit = ApiClient.getClient();
        ApiInterfaces apiInterfaces = retrofit.create(ApiInterfaces.class);
        Call<DutyFree> call = apiInterfaces.fetchDutyFreeProductsList(AppUtils.WP_PARAM_LANGUAGE);

        call.enqueue(new Callback<DutyFree>() {
            @Override
            public void onResponse(Call<DutyFree> call, Response<DutyFree> response) {
                try {
                    //  Toast.makeText(getActivity(), "onT" , Toast.LENGTH_LONG).show();

                    dutyFree = response.body();
                    //  Log.i("dutydata", dutyFree.toString());
                    toolbar.setTitle("Tax Free Shop");
                    ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
                    collapsingToolbarLayout.setTitleEnabled(false);

                    dutyFreeLoadingProgressBar.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);

                    timeTextview.setText(dutyFree.getText1());


                    Picasso.with(getActivity())
                            .load(getActivity().getResources().
                                    getString(R.string.image_downloaded_base_url) +
                                    dutyFree.getImageUrl())
                            .placeholder(R.mipmap.ic_launcher)
                            .into(shopImageView);

                    Log.i("dutyimage", getActivity().getResources().
                            getString(R.string.image_downloaded_base_url) +
                            dutyFree.getImageUrl());
                    setUprestaurentRecyclerView();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<DutyFree> call, Throwable t) {
                //Log.d("onResponse", "onFailure " + t.toString());
                // Toast.makeText(getActivity(), "onfailure " + t.toString(), Toast.LENGTH_LONG).show();

                dutyFreeLoadingProgressBar.setVisibility(View.GONE);
                extraToolbar.setVisibility(View.VISIBLE);
                extraToolbar.setTitle("Tax Free Shop");
                ((DrawerActivity) getActivity()).setToolbarAndToggle(extraToolbar);
                noInternetConnetionView.setVisibility(View.VISIBLE);
                AppUtils.withoutInternetConnectionView(getActivity(),
                        getActivity().getIntent(),
                        retryInternetBtn,
                        AppUtils.fragmentList[3]);
                rootLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.windowBackground));
            }
        });


    }


    private void setUprestaurentRecyclerView() {


        dutyFreeAdapter = new DutyFreeAdapter(getActivity(), dutyFree.getChildren(), AppUtils.fragmentList[3]);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        dutyFreeRecyclerView.setLayoutManager(mLayoutManager);
        dutyFreeRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        dutyFreeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dutyFreeRecyclerView.setAdapter(dutyFreeAdapter);

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
