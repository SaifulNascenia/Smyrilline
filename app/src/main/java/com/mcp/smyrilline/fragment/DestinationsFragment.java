package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.RestaurantMenuInfoGroup;
import com.mcp.smyrilline.util.AppUtils;

import java.util.ArrayList;

import static android.R.attr.button;

/**
 * Created by raqib on 5/11/17.
 */

public class DestinationsFragment extends Fragment {

    private View rootView;

    private Toolbar toolbar;
    private MaterialRefreshLayout materialRefreshLayout;
    private TextView tvRestaurantsNothingText;
    private RelativeLayout.LayoutParams params;
    private RestaurantAdapter mAdapter;
    private RecyclerView DestinationRecylerView;

    private ArrayList<Restaurant> mDestinationList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurents, container, false);

        initView();


        toolbar.setBackground(null);
        toolbar.setTitle("DestinationFragment");
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        mDestinationList = new ArrayList<>();

        Restaurant restaurant1 = new Restaurant("1", "IceLand", "", null, null, false);
        mDestinationList.add(restaurant1);
        Restaurant restaurant2 = new Restaurant("1", "Faroe IceLand", "", null, null, false);
        mDestinationList.add(restaurant2);
        Restaurant restaurant3 = new Restaurant("1", "North IceLand", "", null, null, false);
        mDestinationList.add(restaurant3);
        Restaurant restaurant4 = new Restaurant("1", "IceLand", "", null, null, false);
        mDestinationList.add(restaurant4);
        Restaurant restaurant5 = new Restaurant("1", "IceLand", "", null, null, false);
        mDestinationList.add(restaurant5);


        mAdapter = new RestaurantAdapter(getActivity(), mDestinationList, tvRestaurantsNothingText, AppUtils.fragmentList[5]);
        DestinationRecylerView.setAdapter(mAdapter);

      /*  params = (RelativeLayout.LayoutParams) materialRefreshLayout.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.toolbar);
        materialRefreshLayout.setLayoutParams(params); //causes layout update
*/
        return rootView;
    }

    private void initView() {

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        tvRestaurantsNothingText = (TextView) rootView.findViewById(R.id.tvRestaurantsNothingText);
        materialRefreshLayout = (MaterialRefreshLayout) rootView.findViewById(R.id.refreshRestaurants);//

        DestinationRecylerView = (RecyclerView) rootView.findViewById(R.id.restaurents_list_recycler_view);
        DestinationRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}
