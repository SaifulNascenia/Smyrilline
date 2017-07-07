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
import com.mcp.smyrilline.adapter.DestinationAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.destination.Child;
import com.mcp.smyrilline.model.destination.ListOfDestinations;
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

public class DestinationsFragment extends Fragment {

    private View rootView;

    private RecyclerView destinationsListRecyclerView;
    private Toolbar toolbar;

    private DestinationAdapter mAdapter;
    private ArrayList<Child> mDestinationsList;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private List<ListOfDestinations> listOfDestinations;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;

    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<List<ListOfDestinations>> call;

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

        destinationsListRecyclerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                destinationsListRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        DestinationDetailsfragment destinationFragment = new
                                DestinationDetailsfragment();

                        AppUtils.getBundleObj().putString(AppUtils.DESTINATION_ID,
                                listOfDestinations.get(0).getChildren().get(position).getId());

                        AppUtils.getBundleObj().putString(AppUtils.DESTINATION_NAME,
                                listOfDestinations.get(0).getChildren().
                                        get(position).getName());

                        destinationFragment.setArguments(AppUtils.getBundleObj());

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, destinationFragment)
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

        destinationsListRecyclerView = (RecyclerView) rootView.findViewById(R.id.restaurants_list_recycler_view);
        destinationsListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        mDestinationsList = new ArrayList<>();
        mAdapter = new DestinationAdapter(mContext, mDestinationsList);
        destinationsListRecyclerView.setAdapter(mAdapter);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();

        if (AppUtils.isNetworkAvailable(getActivity())) {

            setUpToolbar(getActivity().getResources().getString(R.string.nav_destinations));
            call = retrofitInterfaces.fetchAllDestinationsInfo(AppUtils.WP_PARAM_LANGUAGE);
            fetchDestinationsApiData();
        } else {
            setUpToolbar(getActivity().getResources().getString(R.string.nav_destinations));
            setWithoutInternetView(DestinationsFragment.class.getSimpleName());
        }

    }

    private void fetchDestinationsApiData() {

        call.enqueue(new Callback<List<ListOfDestinations>>() {
            @Override
            public void onResponse(Call<List<ListOfDestinations>> call, Response<List<ListOfDestinations>> response) {
                try {

                    listOfDestinations = response.body();

                    mLoadingView.setVisibility(View.GONE);
                    mDestinationsList.clear();

                    for (int i = 0; i < response.body().get(0).getChildren().size(); i++) {

                        mDestinationsList.add(response.body().get(0).getChildren().get(i));
                        mAdapter.notifyDataSetChanged();

                    }

                } catch (Exception e) {
                    Log.d("onResponse", "error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ListOfDestinations>> call, Throwable t) {

                setWithoutInternetView(DestinationsFragment.class.getSimpleName());
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
