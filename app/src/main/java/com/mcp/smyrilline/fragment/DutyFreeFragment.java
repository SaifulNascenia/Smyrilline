package com.mcp.smyrilline.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.activity.DutyFreeProductDetailsActivity;
import com.mcp.smyrilline.adapter.DemoRestaurentAdapter;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.interfaces.ClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.DemoRestaurent;
import com.mcp.smyrilline.model.dutyfreemodels.DutyFree;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mcp.smyrilline.util.McpApplication.context;

/**
 * Created by raqib on 5/11/17.
 */

public class DutyFreeFragment extends Fragment {


    private View _rootView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RecyclerView dutyFreeRecyclerView;
    private DutyFreeAdapter dutyFreeAdapter;

    private DutyFree dutyFree;

    private CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        _rootView = inflater.inflate(R.layout.fragment_duty_free, container, false);

        initView();
        toolbar.setTitle("Tax Free Shop");
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        collapsingToolbarLayout.setTitleEnabled(false);

        dutyFreeRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                dutyFreeRecyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        startActivity(new Intent(getActivity(), DutyFreeProductDetailsActivity.class));

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


        loadDutyFreeproductList();


        return _rootView;
    }

    private void initView() {


        coordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);
        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);
        dutyFreeRecyclerView = (RecyclerView) _rootView.findViewById(R.id.recycler_view);
    }

    private void loadDutyFreeproductList() {
        Retrofit retrofit = ApiClient.getClient();
        ApiInterfaces apiInterfaces = retrofit.create(ApiInterfaces.class);
        Call<DutyFree> call = apiInterfaces.fetchDutyFreeProductsList(AppUtils.WP_PARAM_LANGUAGE);

        call.enqueue(new Callback<DutyFree>() {
            @Override
            public void onResponse(Call<DutyFree> call, Response<DutyFree> response) {

                try {

                    dutyFree = response.body();
                    setUprestaurentRecyclerView();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<DutyFree> call, Throwable t) {
                Log.d("onResponse", "onFailure " + t.toString());
            }
        });


    }


    private void setUprestaurentRecyclerView() {


        dutyFreeAdapter = new DutyFreeAdapter(getActivity(), dutyFree.getChildren());

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
