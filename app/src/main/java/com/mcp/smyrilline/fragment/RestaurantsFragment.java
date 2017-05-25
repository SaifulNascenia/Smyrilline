package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.RestaurantAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.model.InternalStorage;
import com.mcp.smyrilline.model.Restaurant;
import com.mcp.smyrilline.model.parentmodel.ParentModel;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.Utils;

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
    private View mLoadingView;
    private TextView tvNothingText;

    private List<ParentModel> parentModelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity();

        rootView = inflater.inflate(R.layout.fragment_restaurents, container, false);
        ((DrawerActivity) getActivity()).setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));

        setUprestaurentRecyclerView();


        return rootView;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("RestaurantFragment");

        // Init UI
        tvNothingText = (TextView) rootView.findViewById(R.id.tvRestaurantsNothingText);
        tvNothingText.setVisibility(View.GONE);
        mLoadingView = rootView.findViewById(R.id.restaurantsLoadingView);
        restaurenstListRecyclerView = (RecyclerView) rootView.findViewById(R.id.restaurents_list_recycler_view);
        restaurenstListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

       /* // List of items, will be populated in AsyncTask below
        mRestaurantList = new ArrayList<>();
        mAdapter = new RestaurantAdapter(mContext, mRestaurantList, tvNothingText);
        restaurenstListRecyclerView.setAdapter(mAdapter);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        materialRefreshLayout = (MaterialRefreshLayout) rootView.findViewById(R.id.refreshRestaurants);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onfinish() {
                super.onfinish();
            }

            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                // refreshing...
                if (Utils.isNetworkAvailable(mContext))
                    new CheckCMSandLoadRestaurantItemsTask().execute();
                else {
                    mLoadingView.setVisibility(View.GONE);
                    tvNothingText.setVisibility(View.VISIBLE);
                    materialRefreshLayout.finishRefresh();
                    Utils.showAlertDialog(mContext, Utils.ALERT_NO_WIFI);
                }
            }
        });

        // We check entrance, to avoid loading again
        // Only load again if entered from menu
        boolean enteredFromMenu = mSharedPref.getBoolean(DrawerActivity.ENTERED_FROM_MENU, false);
        if (enteredFromMenu) {
            Log.i("progresslog", "initial if");
            if (Utils.isNetworkAvailable(mContext)) {
                Log.i("progresslog", "initial nest if");
                new CheckCMSandLoadRestaurantItemsTask().execute();
            } else {
                Log.i("progresslog", "initial nest else");
                mLoadingView.setVisibility(View.GONE);
                tvNothingText.setVisibility(View.VISIBLE);
                Utils.showAlertDialog(mContext, Utils.ALERT_NO_WIFI);
            }
        } else {
            Log.i("progresslog", "initial else");

            try {
                *//*mRestaurantList = (ArrayList<Restaurant>) InternalStorage.readObject(mContext, RESTAURANT_LIST);
                // fix issue: had to make separate method, otherwise list was not showing
                mAdapter.setRestaurantList(mRestaurantList);
                mAdapter.refreshList();*//*
                mLoadingView.setVisibility(View.GONE);
                initRestaurantList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        initRestaurantList();

    }


    private class CheckCMSandLoadRestaurantItemsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // network work on background thread
            String response = Utils.isDomainAvailable(mContext, mContext.getResources().getString(R.string.url_wordpress));
            if (response.equals(Utils.CONNECTION_OK))
                initRestaurantList();

            return response;
        }

        @Override
        protected void onPostExecute(String serverResponse) {
            super.onPostExecute(serverResponse);

            mLoadingView.setVisibility(View.GONE);
            materialRefreshLayout.finishRefresh();

            if (serverResponse.equals(Utils.CONNECTION_OK)) {
                if (mAdapter != null)
                    mAdapter.refreshList();

                // Storing restaurant list, to restore when return from detail
                // instead of loading from server again
                try {
                    InternalStorage.writeObject(mContext, RESTAURANT_LIST, mRestaurantList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.showAlertDialog(mContext, serverResponse);
                tvNothingText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initRestaurantList() {

        Retrofit retrofit = ApiClient.getClient();
        ApiInterfaces apiInterfaces = retrofit.create(ApiInterfaces.class);
        Call<List<ParentModel>> call = apiInterfaces.fetchAllRestaurentsAndBarsInfo();

        call.enqueue(new Callback<List<ParentModel>>() {
            @Override
            public void onResponse(Call<List<ParentModel>> call, Response<List<ParentModel>> response) {
                try {

                    //Log.d("onResponse", response.body() + "");
                    parentModelList = response.body();
                    //Log.d("onResponse", parentModelList.size() + "");


                    for (int i = 0; i < parentModelList.size(); i++) {
                        ParentModel parentModel = parentModelList.get(i);
                        Log.d("onResponse", parentModel.getId() + " " + parentModel.getTitle().getRendered());
                    }


                } catch (Exception e) {
                    Log.d("onResponse", "error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ParentModel>> call, Throwable t) {
                Log.d("onResponse", "onFailure " + t.toString());
            }
        });


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
