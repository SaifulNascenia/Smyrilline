package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.restaurant.Child;
import com.mcp.smyrilline.model.restaurant.ListOfRestaurants;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 7/6/17.
 */

public class RestaurantsFragment extends Fragment {

    private View rootView;

    private RecyclerView restaurenstListRecyclerView;
    private Toolbar toolbar;

    private RestaurantAdapter mAdapter;
    private ArrayList<Child> mRestaurantList;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private List<ListOfRestaurants> listOfRestaurants;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;

    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<List<ListOfRestaurants>> call;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContext = getActivity();

        rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);

        retrofit = RetrofitClient.getClient();
        retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);

        initView();

        restaurenstListRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                restaurenstListRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        AppUtils.getBundleObj().putString(AppUtils.RESTAUREANT_ID,
                                listOfRestaurants.get(0).getChildren().get(position).getId());

                        AppUtils.getBundleObj().putString(AppUtils.RESTAUREANT_NAME,
                                listOfRestaurants.get(0).getChildren().get(position).getName());

                        ResturantDetailsFragment resturentDetailsFragment = new
                                ResturantDetailsFragment();
                        resturentDetailsFragment.setArguments(AppUtils.getBundleObj());

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

    // Init UI
    private void initView() {

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mLoadingView = rootView.findViewById(R.id.restaurantsLoadingView);
        noInternetConnetionView = rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) rootView.findViewById(R.id.retry_internet);

        restaurenstListRecyclerView = (RecyclerView) rootView.findViewById(R.id.restaurants_list_recycler_view);
        restaurenstListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        mRestaurantList = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mContext, mRestaurantList);
        restaurenstListRecyclerView.setAdapter(mAdapter);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar(getActivity().getResources().getString(R.string.nav_restaurants));
            call = retrofitInterfaces.fetchAllRestaurantsAndBarsInfo(AppUtils.WP_PARAM_LANGUAGE);
            fetchRestaurantsApiData();

        } else {

            setUpToolbar(getActivity().getResources().getString(R.string.nav_restaurants));
            setWithoutInternetView(RestaurantsFragment.class.getSimpleName());
        }

    }

    private void fetchRestaurantsApiData() {

        call.enqueue(new Callback<List<ListOfRestaurants>>() {
            @Override
            public void onResponse(Call<List<ListOfRestaurants>> call, Response<List<ListOfRestaurants>> response) {
                try {

                    listOfRestaurants = response.body();

                    mLoadingView.setVisibility(View.GONE);
                    mRestaurantList.clear();

                    for (int i = 0; i < response.body().get(0).getChildren().size(); i++) {

                        mRestaurantList.add(response.body().get(0).getChildren().get(i));
                        mAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Log.d("onResponse", "error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ListOfRestaurants>> call, Throwable t) {
                Log.d("onResponse", "onFailure " + t.toString());

                setWithoutInternetView(getArguments().getString(AppUtils.CALLED_CLASS_NAME));


            }
        });
    }

    private void setUpToolbar(String toolbarName) {

        toolbar.setBackground(null);
        toolbar.setTitle(toolbarName);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
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
