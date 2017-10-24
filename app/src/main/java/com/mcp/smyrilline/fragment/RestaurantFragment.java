package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RestaurantFragment extends Fragment {

    public static String RESTAURANT_ID = "restaurant_id";
    public static String RESTAURANT_NAME = "restaurant_name";

    private View rootView;
    private RecyclerView restaurantListRecyclerView;
    private Toolbar toolbar;
    private RestaurantAdapter mAdapter;
    private ArrayList<Child> mRestaurantList;
    private Context mContext;
    private List<ListOfRestaurants> listOfRestaurants;
    private View loadingProgressView;
    private View noConnectionView;
    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<List<ListOfRestaurants>> call;
    private Fragment thisFragment = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        mContext = getActivity();
        rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);
        initView();
        fetchRestaurantsApiData();
        return rootView;
    }

    // Init UI
    private void initView() {
        // Toolbar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackground(null);
        toolbar.setTitle(R.string.restaurants);
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        loadingProgressView = rootView.findViewById(R.id.restaurantsLoadingView);
        noConnectionView = rootView.findViewById(R.id.no_connection_layout);

        // List view
        restaurantListRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.restaurants_list_recycler_view);
        restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRestaurantList = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mContext, mRestaurantList);
        restaurantListRecyclerView.setAdapter(mAdapter);
        restaurantListRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        restaurantListRecyclerView,
                        new RecylerViewItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                                Bundle bundle = new Bundle();
                                bundle.putString(RESTAURANT_ID,
                                        listOfRestaurants.get(0).getChildren().get(position)
                                                .getId());

                                bundle.putString(RESTAURANT_NAME,
                                        listOfRestaurants.get(0).getChildren().get(position)
                                                .getName());

                                RestaurantDetailsFragment restaurantDetailsFragment = new
                                        RestaurantDetailsFragment();
                                restaurantDetailsFragment.setArguments(bundle);

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .addToBackStack(null)
                                        .replace(R.id.content_frame, restaurantDetailsFragment)
                                        .commit();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

    }


    private void fetchRestaurantsApiData() {
        if (AppUtils.isNetworkAvailable(getActivity())) {
            loadingProgressView.setVisibility(View.VISIBLE);
            retrofit = RetrofitClient.getClient();
            retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
            call = retrofitInterfaces
                .fetchAllRestaurantsAndBarsInfo(getString(R.string.wp_language_param));
            call.enqueue(new Callback<List<ListOfRestaurants>>() {
                @Override
                public void onResponse(Call<List<ListOfRestaurants>> call,
                        Response<List<ListOfRestaurants>> response) {
                    try {

                        listOfRestaurants = response.body();
                        mRestaurantList.clear();

                        for (int i = 0; i < response.body().get(0).getChildren().size(); i++) {
                            mRestaurantList.add(response.body().get(0).getChildren().get(i));
                            mAdapter.notifyDataSetChanged();
                        }

                        AppUtils.hideNoConnectionView(loadingProgressView, noConnectionView);

                    } catch (Exception e) {
                        Log.d("onResponse", "error");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<ListOfRestaurants>> call, Throwable t) {
                    Log.d("onResponse", "onFailure " + t.toString());
                    if (getActivity() != null) {
                        AppUtils.showNoConnectionView(getActivity(), thisFragment,
                            loadingProgressView,
                            noConnectionView, toolbar);
                    }
                }
            });
        } else {
            AppUtils.showNoConnectionView(getActivity(), thisFragment, loadingProgressView,
                    noConnectionView, toolbar);
        }
    }
}
