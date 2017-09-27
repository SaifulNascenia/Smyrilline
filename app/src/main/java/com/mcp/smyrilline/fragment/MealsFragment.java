package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.MealExpandableListAdapter;
import com.mcp.smyrilline.model.InternalStorage;
import com.mcp.smyrilline.model.MealDate;
import com.mcp.smyrilline.util.AppUtils;
import java.util.ArrayList;

/**
 * Created by raqib on 4/26/16.
 */
public class MealsFragment extends Fragment {

    private static MealsFragment mInstance;
    private Context mContext;
    private SharedPreferences mSharedPref;
    private Gson gson = new Gson();
    private ArrayList<MealDate> mMealList;
    private MealExpandableListAdapter mMealAdapter;
    private ExpandableListView mExpandableListView;
    private TextView tvNothingText;
    private Button btnLogOut;

    public static MealsFragment newInstance() {
        if (mInstance == null)
            mInstance = new MealsFragment();
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        // load meal list
        Object savedObject = InternalStorage
                .readObject(mContext, AppUtils.PREF_MEAL_LIST);
        if (savedObject != null)
            mMealList = (ArrayList<MealDate>) savedObject;
        else
            mMealList = new ArrayList<>();

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.listViewMeals);
        tvNothingText = (TextView) rootView.findViewById(R.id.tvMealsNothingText);

        // Change booking button, add it as list footer
        View footerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_meal_list, null);
        btnLogOut = (Button) footerView.findViewById(R.id.btnMealsLogOut);
        btnLogOut.setAllCaps(false);

        // If there's no meal, no need to show 'change booking'
        if (!mMealList.isEmpty())
            mExpandableListView.addFooterView(footerView);

        mMealAdapter = new MealExpandableListAdapter(mContext, mMealList, tvNothingText);
        mExpandableListView.setAdapter(mMealAdapter);
        mMealAdapter.checkEmpty();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User logged out
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putBoolean(AppUtils.PREF_LOGGED_IN, false);
                editor.apply();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new LoginFragment())
                        .commit();
            }
        });

        return rootView;
    }
}
