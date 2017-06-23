package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.interfaces.ClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.restaurentsmodel.Child;
import com.mcp.smyrilline.model.restaurentsmodel.ListOfRestaurent;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;

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

    private Retrofit retrofit;
    private ApiInterfaces apiInterfaces;
    private Call<List<ListOfRestaurent>> call;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContext = getActivity();

        rootView = inflater.inflate(R.layout.fragment_restaurents, container, false);

        retrofit = ApiClient.getClient();
        apiInterfaces = retrofit.create(ApiInterfaces.class);

        bundle = new Bundle();

        initView();

        restaurenstListRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                restaurenstListRecyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        bundle.putString("RESTAURENT_ID",
                                parentModelList.get(0).getChildren().get(position).getId());
                        bundle.putString("RESTAURENT_NAME", parentModelList.get(0).getChildren().
                                get(position).getName());

                        ResturentDetailsFragment resturentDetailsFragment = new
                                ResturentDetailsFragment();
                        resturentDetailsFragment.setArguments(bundle);

                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, resturentDetailsFragment)
                                .commit();


                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


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

        showListViewFragmentWise();

       /* if (AppUtils.isNetworkAvailable(getActivity())) {

        } else {
            *//*mLoadingView.setVisibility(View.GONE);
            tvNothingText.setVisibility(View.VISIBLE);
            AppUtils.showAlertDialog(mContext, AppUtils.ALERT_NO_WIFI);
*//*
            setWithoutInternetView();
        }*/

    }

    private void showListViewFragmentWise() {


        if (getArguments().getString("FRAGMENT_NAME").equals(AppUtils.fragmentList[4])
                && AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar("Restaurants");
            call = apiInterfaces.fetchAllRestaurentsAndBarsInfo(AppUtils.WP_PARAM_LANGUAGE);
            fetchApiData();

        } else if (getArguments().getString("FRAGMENT_NAME").equals(AppUtils.fragmentList[5])
                && AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar("Destinations");
            call = apiInterfaces.fetchAllDestinationsInfo(AppUtils.WP_PARAM_LANGUAGE);
            fetchApiData();

        } else if (getArguments().getString("FRAGMENT_NAME").equals(AppUtils.fragmentList[4])
                && !AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar("Restaurants");
            setWithoutInternetView(getArguments().getString("FRAGMENT_NAME"));

        } else if (getArguments().getString("FRAGMENT_NAME").equals(AppUtils.fragmentList[5])
                && !AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar("Destinations");
            setWithoutInternetView(getArguments().getString("FRAGMENT_NAME"));

        } else {
            Log.i("fragmentcheck", "no fragment found");
        }

    }

    private void fetchApiData() {


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

                setWithoutInternetView(getArguments().getString("FRAGMENT_NAME"));


            }
        });
    }

    private void setUpToolbar(String toolbarName) {

        toolbar.setBackground(null);
        toolbar.setTitle(toolbarName);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
    }

    private void initRestaurantList() {


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

                setWithoutInternetView(getArguments().getString("FRAGMENT_NAME"));


            }
        });


    }

    private void setWithoutInternetView(String fragmentName) {
        mLoadingView.setVisibility(View.GONE);
        noInternetConnetionView.setVisibility(View.VISIBLE);
        AppUtils.withoutInternetConnectionView(getActivity(),
                getActivity().getIntent(),
                retryInternetBtn,
                fragmentName);

        toolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }


}
