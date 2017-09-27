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
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DestinationAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.destination.Destinations;
import com.mcp.smyrilline.model.destination.DestinationsChild;
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

public class DestinationFragment extends Fragment {

    private View rootView;
    private RecyclerView destinationsRecyclerView;
    private Toolbar toolbar;
    private DestinationAdapter mAdapter;
    private ArrayList<DestinationsChild> mDestinationsList;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private List<Destinations> listOfDestinations;
    private View loadingProgressView;
    private View noConnectionView;
    private Retrofit retrofit;
    private RetrofitInterfaces retrofitInterfaces;
    private Call<List<Destinations>> call;
    private Fragment thisFragment = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.fragment_restaurants, container, false);
        mContext = getActivity();
        mSharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        initView();
        fetchDestinationsApiData();
        return rootView;
    }

    // Init UI
    private void initView() {

        // toolbar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackground(null);
        toolbar.setTitle(getString(R.string.destinations));
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        loadingProgressView = rootView.findViewById(R.id.restaurantsLoadingView);
        noConnectionView = rootView.findViewById(R.id.no_connection_layout);

        // recycler view
        destinationsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.restaurants_list_recycler_view);
        destinationsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mDestinationsList = new ArrayList<>();
        mAdapter = new DestinationAdapter(mContext, mDestinationsList);
        destinationsRecyclerView.setAdapter(mAdapter);
        destinationsRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        destinationsRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        Bundle bundle = new Bundle();
                        bundle.putString(AppUtils.ITEM_ID,
                                listOfDestinations.get(0).getChildren().get(position).getId());

                        bundle.putString(AppUtils.ITEM_NAME,
                                listOfDestinations.get(0).getChildren().
                                        get(position).getName());

                        DestinationDetailsFragment1 destinationFragment = new
                                DestinationDetailsFragment1();
                        destinationFragment.setArguments(bundle);

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
    }

    private void fetchDestinationsApiData() {
        if (AppUtils.isNetworkAvailable(getActivity())) {
            retrofit = RetrofitClient.getClient();
            retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
            call = retrofitInterfaces.fetchAllDestinationsInfo(AppUtils.WP_PARAM_LANGUAGE);
            call.enqueue(new Callback<List<Destinations>>() {
            @Override
            public void onResponse(Call<List<Destinations>> call,
                    Response<List<Destinations>> response) {
                try {

                    listOfDestinations = response.body();

                    loadingProgressView.setVisibility(View.GONE);
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
            public void onFailure(Call<List<Destinations>> call, Throwable t) {
                AppUtils.showNoConnectionView(getActivity(), thisFragment, loadingProgressView,
                        noConnectionView, toolbar);
            }
        });
        } else {
            AppUtils.showNoConnectionView(getActivity(), thisFragment, loadingProgressView,
                    noConnectionView, toolbar);
        }
    }
}
