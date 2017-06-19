package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.activity.DutyFreeProductDetailsActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.interfaces.ClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.InternalStorage;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.parentmodel.ParentModel;
import com.mcp.smyrilline.model.restaurentsmodel.Child;
import com.mcp.smyrilline.model.restaurentsmodel.ListOfRestaurent;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by raqib on 5/11/17.
 */

public class RestaurantsFragment extends Fragment {

    private View rootView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView restaurenstListRecyclerView;
    /*private List<DemoRestaurent> demoRestaurentList = new ArrayList<>();
    private AppCompatActivity actionBar;
    private DemoRestaurentAdapter restaurentRecyclerViewAdapter;
    */private Toolbar toolbar;


    private static final String RESTAURANT_LIST = "restaurantList";
    private MaterialRefreshLayout materialRefreshLayout;
    private RestaurantAdapter mAdapter;
    private ArrayList<Restaurant> mRestaurantList;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private TextView tvNothingText;
    private List<ListOfRestaurent> parentModelList;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;

    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContext = getActivity();

        rootView = inflater.inflate(R.layout.fragment_restaurents, container, false);

        bundle = new Bundle();

        initView();
        restaurenstListRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                restaurenstListRecyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        Toast.makeText(getActivity(), parentModelList.size() + " " +
                                        parentModelList.get(0).getChildren().get(position).getName(),
                                Toast.LENGTH_LONG).show();

                        bundle.putString("RESTAURENT_ID",
                                parentModelList.get(0).getChildren().get(position).getId());
                        bundle.putString("RESTAURENT_NAME", parentModelList.get(0).getChildren().
                                get(position).getName());

                        IndividualResturentDetailsFragment individualResturentDetailsFragment = new
                                IndividualResturentDetailsFragment();
                        individualResturentDetailsFragment.setArguments(bundle);

                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, individualResturentDetailsFragment)
                                .commit();


                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


        toolbar.setBackground(null);
        toolbar.setTitle("Restaurants");
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        //  setUprestaurentRecyclerView();


        return rootView;
    }

    private void initView() {

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mLoadingView = rootView.findViewById(R.id.restaurantsLoadingView);
        noInternetConnetionView = rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) rootView.findViewById(R.id.retry_internet);
        // Init UI
        tvNothingText = (TextView) rootView.findViewById(R.id.tvRestaurantsNothingText);
        tvNothingText.setVisibility(View.GONE);
        restaurenstListRecyclerView = (RecyclerView) rootView.findViewById(R.id.restaurents_list_recycler_view);
        restaurenstListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        // List of items, will be populated in AsyncTask below
        mRestaurantList = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mContext, mRestaurantList, tvNothingText, AppUtils.fragmentList[4]);
        restaurenstListRecyclerView.setAdapter(mAdapter);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());


       /* materialRefreshLayout = (MaterialRefreshLayout) rootView.findViewById(R.id.refreshRestaurants);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onfinish() {
                super.onfinish();
            }

            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                // refreshing...
                if (AppUtils.isNetworkAvailable(mContext))
                    initRestaurantList();
                else {
                    mLoadingView.setVisibility(View.GONE);
                    tvNothingText.setVisibility(View.VISIBLE);
                    materialRefreshLayout.finishRefresh();
                    AppUtils.showAlertDialog(mContext, AppUtils.ALERT_NO_WIFI);
                }
            }
        });
*/

        if (AppUtils.isNetworkAvailable(getActivity())) {

            initRestaurantList();

        } else {
            /*mLoadingView.setVisibility(View.GONE);
            tvNothingText.setVisibility(View.VISIBLE);
            AppUtils.showAlertDialog(mContext, AppUtils.ALERT_NO_WIFI);
*/
            setWithoutInternetView();
        }


    }


    private void initRestaurantList() {

        Retrofit retrofit = ApiClient.getClient();
        ApiInterfaces apiInterfaces = retrofit.create(ApiInterfaces.class);
        Call<List<ListOfRestaurent>> call = apiInterfaces.fetchAllRestaurentsAndBarsInfo();

        call.enqueue(new Callback<List<ListOfRestaurent>>() {
            @Override
            public void onResponse(Call<List<ListOfRestaurent>> call, Response<List<ListOfRestaurent>> response) {
                try {

                    parentModelList = response.body();
                    Log.d("onResponse", parentModelList + "");

                    mLoadingView.setVisibility(View.GONE);
                    //   materialRefreshLayout.finishRefresh();
                    mRestaurantList.clear();

                    for (int i = 0; i < response.body().get(0).getChildren().size(); i++) {

                        Child restaturent = response.body().get(0).getChildren().get(i);
                        Restaurant restaurant = new Restaurant(
                                restaturent.getId(),
                                restaturent.getName(),
                                null,
                                restaturent.getImageUrl(),
                                null,
                                false);
                        mRestaurantList.add(restaurant);
                        mAdapter.notifyDataSetChanged();

                    }

                    //Toast.makeText(getActivity(), "Dataloading complete " + mRestaurantList.size(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.d("onResponse", "error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ListOfRestaurent>> call, Throwable t) {
                Log.d("onResponse", "onFailure " + t.toString());

                setWithoutInternetView();


            }
        });


    }

    private void setWithoutInternetView() {
        mLoadingView.setVisibility(View.GONE);
        noInternetConnetionView.setVisibility(View.VISIBLE);
        AppUtils.withoutInternetConnectionView(getActivity(),
                getActivity().getIntent(),
                retryInternetBtn);

        toolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setUprestaurentRecyclerView() {


        /*demoRestaurentList.add(new DemoRestaurent("Coffee", "Nice taste"));
        demoRestaurentList.add(new DemoRestaurent("Coffee", "Freshly brewed coffee.Awesome taste."));
        demoRestaurentList.add(new DemoRestaurent("Coffee", "Freshly brewed coffee"));
        demoRestaurentList.add(new DemoRestaurent("Coffee", "Freshly brewed coffee"));
        demoRestaurentList.add(new DemoRestaurent("Coffee", "Freshly brewed coffee"));


        restaurentRecyclerViewAdapter = new DemoRestaurentAdapter(getActivity(), demoRestaurentList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        restaurentRecyclerView.setLayoutManager(mLayoutManager);
        restaurentRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        restaurentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        restaurentRecyclerView.setAdapter(restaurentRecyclerViewAdapter);*/


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
