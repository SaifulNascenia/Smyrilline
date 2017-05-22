package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.activity.MainGridActivity;
import com.mcp.smyrilline.signalr.SignalRService;
import com.mcp.smyrilline.util.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by raqib on 5/11/17.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    // message filter parameters, must be matching with server side
    // DO NOT edit value, unless changed in server side
    public static final String MESSAGE_FILTER_AGE_ALL = "all";
    public static final String MESSAGE_FILTER_GENDER_BOTH = "both";
    private static final String MESSAGE_FILTER_AGE_ADULT = "adult";
    private static final String MESSAGE_FILTER_AGE_CHILD15 = "child15";
    private static final String MESSAGE_FILTER_AGE_CHILD11 = "child11";
    private static final String MESSAGE_FILTER_GENDER_MALE = "male";
    private static final String MESSAGE_FILTER_GENDER_FEMALE = "female";
    private Context mContext;
    private StringBuilder mSelectedAges;
    private StringBuilder mSelectedGenders;
    private RelativeLayout rlShadowAgeAdult;
    private RelativeLayout rlShadowAgeChild15;
    private RelativeLayout rlShadowAgeChild11;
    private RelativeLayout rlShadowAgeAll;
    private RelativeLayout rlShadowGenderMale;
    private RelativeLayout rlShadowGenderFemale;
    private RelativeLayout rlShadowGenderBoth;
    private SharedPreferences mSharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().invalidateOptionsMenu();
        mContext = getActivity();

        // set the toolbar as actionbar
        ((DrawerActivity)getActivity()).setToolbarAndToggle((Toolbar)rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(R.string.settings);

        // Has a 'done' option in toolbar
        setHasOptionsMenu(true);

        // Age settings
        LinearLayout llAgeAdult = (LinearLayout) rootView.findViewById(R.id.llAgeAdult);
        llAgeAdult.setOnClickListener(this);
        LinearLayout llAgeChild15 = (LinearLayout) rootView.findViewById(R.id.llAgeChild15);
        llAgeChild15.setOnClickListener(this);
        LinearLayout llAgeChild11 = (LinearLayout) rootView.findViewById(R.id.llAgeChild11);
        llAgeChild11.setOnClickListener(this);
        LinearLayout llAgeAll = (LinearLayout) rootView.findViewById(R.id.llAgeAll);
        llAgeAll.setOnClickListener(this);

        // Gender settings
        LinearLayout llGenderMale = (LinearLayout) rootView.findViewById(R.id.llGenderMale);
        llGenderMale.setOnClickListener(this);
        LinearLayout llGenderFemale = (LinearLayout) rootView.findViewById(R.id.llGenderFemale);
        llGenderFemale.setOnClickListener(this);
        LinearLayout llGenderBoth = (LinearLayout) rootView.findViewById(R.id.llGenderBoth);
        llGenderBoth.setOnClickListener(this);

        // Shadows views for highlighting (visible/invisible)
        rlShadowAgeAdult = (RelativeLayout) rootView.findViewById(R.id.rlShadowAgeAdult);
        rlShadowAgeChild15 = (RelativeLayout) rootView.findViewById(R.id.rlShadowAgeChild15);
        rlShadowAgeChild11 = (RelativeLayout) rootView.findViewById(R.id.rlShadowAgeChild11);
        rlShadowAgeAll = (RelativeLayout) rootView.findViewById(R.id.rlShadowAgeAll);
        rlShadowGenderMale = (RelativeLayout) rootView.findViewById(R.id.rlShadowGenderMale);
        rlShadowGenderFemale = (RelativeLayout) rootView.findViewById(R.id.rlShadowGenderFemale);
        rlShadowGenderBoth = (RelativeLayout) rootView.findViewById(R.id.rlShadowGenderBoth);

        // Get saved values, default is 'all' & 'both'
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mSelectedAges = new StringBuilder(mSharedPref.getString(Utils.PREF_MESSAGE_FILTER_AGE,
                MESSAGE_FILTER_AGE_ALL));
        mSelectedGenders = new StringBuilder(mSharedPref.getString(Utils.PREF_MESSAGE_FILTER_GENDER,
                MESSAGE_FILTER_GENDER_BOTH));

        // Initiate state of filters
        initMessageFilters();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.settings_done):
                // if neither age or gender is selected
                if (rlShadowAgeAdult.getVisibility() == View.INVISIBLE
                        && rlShadowAgeChild15.getVisibility() == View.INVISIBLE
                        && rlShadowAgeChild11.getVisibility() == View.INVISIBLE
                        && rlShadowGenderMale.getVisibility() == View.INVISIBLE
                        && rlShadowGenderFemale.getVisibility() == View.INVISIBLE) {
                    Utils.showAlertDialog(mContext, getString(R.string.please_select_age_gender));
                    break;
                } else if (rlShadowAgeAdult.getVisibility() == View.INVISIBLE  // if no age is selected
                        && rlShadowAgeChild15.getVisibility() == View.INVISIBLE
                        && rlShadowAgeChild11.getVisibility() == View.INVISIBLE) {
                    Utils.showAlertDialog(mContext, getString(R.string.please_select_age));
                    break;
                } else if (rlShadowGenderMale.getVisibility() == View.INVISIBLE    // if no gender is selected
                        && rlShadowGenderFemale.getVisibility() == View.INVISIBLE) {
                    Utils.showAlertDialog(mContext, getString(R.string.please_select_gender));
                    break;
                }

                // clearing the strings, before assigning new value
                mSelectedAges.setLength(0);
                mSelectedGenders.setLength(0);

                if (rlShadowAgeAll.getVisibility() == View.VISIBLE)
                    mSelectedAges.append(MESSAGE_FILTER_AGE_ALL);
                else {
                    if (rlShadowAgeAdult.getVisibility() == View.VISIBLE)
                        mSelectedAges.append(MESSAGE_FILTER_AGE_ADULT + ",");
                    if (rlShadowAgeChild15.getVisibility() == View.VISIBLE)
                        mSelectedAges.append(MESSAGE_FILTER_AGE_CHILD15 + ",");
                    if (rlShadowAgeChild11.getVisibility() == View.VISIBLE)
                        mSelectedAges.append(MESSAGE_FILTER_AGE_CHILD11);
                }

                if (rlShadowGenderBoth.getVisibility() == View.VISIBLE)
                    mSelectedGenders.append(MESSAGE_FILTER_GENDER_BOTH);
                else {
                    if (rlShadowGenderMale.getVisibility() == View.VISIBLE)
                        mSelectedGenders.append(MESSAGE_FILTER_GENDER_MALE + ",");
                    if (rlShadowGenderFemale.getVisibility() == View.VISIBLE)
                        mSelectedGenders.append(MESSAGE_FILTER_GENDER_FEMALE);
                }

                // Save
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString(Utils.PREF_MESSAGE_FILTER_AGE, mSelectedAges.toString());
                editor.putString(Utils.PREF_MESSAGE_FILTER_GENDER, mSelectedGenders.toString());
                editor.apply();

                // Send settings changed broadcast
                mContext.sendBroadcast(new Intent(SignalRService.ACTION_APP_SETTINGS_CHANGED));

                // Show 'saved' toast
                if (getActivity() != null)
                    Utils.showToastInMainUI(getActivity(), getString(R.string.settings_saved));
        }
        return false;
    }

    /**
     * Highlight the message filters according to saved value
     */
    private void initMessageFilters() {
        // Split string with ',' and check for tags
        String[] selectedAges = mSelectedAges.toString().split(",");
        List ageList = Arrays.asList(selectedAges);
        if (ageList.contains(MESSAGE_FILTER_AGE_ALL)) {
            rlShadowAgeAll.setVisibility(View.VISIBLE);
            rlShadowAgeAdult.setVisibility(View.VISIBLE);
            rlShadowAgeChild15.setVisibility(View.VISIBLE);
            rlShadowAgeChild11.setVisibility(View.VISIBLE);
        } else {
            if (ageList.contains(MESSAGE_FILTER_AGE_ADULT))
                rlShadowAgeAdult.setVisibility(View.VISIBLE);
            if (ageList.contains(MESSAGE_FILTER_AGE_CHILD15))
                rlShadowAgeChild15.setVisibility(View.VISIBLE);
            if (ageList.contains(MESSAGE_FILTER_AGE_CHILD11))
                rlShadowAgeChild11.setVisibility(View.VISIBLE);
        }

        String[] selectedGenders = mSelectedGenders.toString().split(",");
        List genderlist = Arrays.asList(selectedGenders);
        if (genderlist.contains(MESSAGE_FILTER_GENDER_BOTH)) {
            rlShadowGenderBoth.setVisibility(View.VISIBLE);
            rlShadowGenderMale.setVisibility(View.VISIBLE);
            rlShadowGenderFemale.setVisibility(View.VISIBLE);
        } else {
            if (genderlist.contains(MESSAGE_FILTER_GENDER_MALE))
                rlShadowGenderMale.setVisibility(View.VISIBLE);
            if (genderlist.contains(MESSAGE_FILTER_GENDER_FEMALE))
                rlShadowGenderFemale.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        // toggle shadow layouts
        switch (view.getId()) {
            case (R.id.llAgeAdult):     // adult
                if (rlShadowAgeAdult.getVisibility() == View.VISIBLE)
                    rlShadowAgeAdult.setVisibility(View.INVISIBLE);
                else
                    rlShadowAgeAdult.setVisibility(View.VISIBLE);
                break;
            case (R.id.llAgeChild15):   // child 18
                if (rlShadowAgeChild15.getVisibility() == View.VISIBLE)
                    rlShadowAgeChild15.setVisibility(View.INVISIBLE);
                else
                    rlShadowAgeChild15.setVisibility(View.VISIBLE);
                break;
            case (R.id.llAgeChild11):   // child 15
                if (rlShadowAgeChild11.getVisibility() == View.VISIBLE)
                    rlShadowAgeChild11.setVisibility(View.INVISIBLE);
                else
                    rlShadowAgeChild11.setVisibility(View.VISIBLE);
                break;
            case (R.id.llAgeAll):       // 'All' selected, toggle entire group
                if (rlShadowAgeAll.getVisibility() == View.VISIBLE) {
                    rlShadowAgeAll.setVisibility(View.INVISIBLE);
                    rlShadowAgeAdult.setVisibility(View.INVISIBLE);
                    rlShadowAgeChild15.setVisibility(View.INVISIBLE);
                    rlShadowAgeChild11.setVisibility(View.INVISIBLE);
                } else {
                    rlShadowAgeAll.setVisibility(View.VISIBLE);
                    rlShadowAgeAdult.setVisibility(View.VISIBLE);
                    rlShadowAgeChild15.setVisibility(View.VISIBLE);
                    rlShadowAgeChild11.setVisibility(View.VISIBLE);
                }
                break;
            case (R.id.llGenderMale):   // male
                if (rlShadowGenderMale.getVisibility() == View.VISIBLE)
                    rlShadowGenderMale.setVisibility(View.INVISIBLE);
                else
                    rlShadowGenderMale.setVisibility(View.VISIBLE);
                break;
            case (R.id.llGenderFemale): // female
                if (rlShadowGenderFemale.getVisibility() == View.VISIBLE)
                    rlShadowGenderFemale.setVisibility(View.INVISIBLE);
                else
                    rlShadowGenderFemale.setVisibility(View.VISIBLE);
                break;
            case (R.id.llGenderBoth):   // 'Both' selected, toggle group
                if (rlShadowGenderBoth.getVisibility() == View.VISIBLE) {
                    rlShadowGenderBoth.setVisibility(View.INVISIBLE);
                    rlShadowGenderMale.setVisibility(View.INVISIBLE);
                    rlShadowGenderFemale.setVisibility(View.INVISIBLE);
                } else {
                    rlShadowGenderBoth.setVisibility(View.VISIBLE);
                    rlShadowGenderMale.setVisibility(View.VISIBLE);
                    rlShadowGenderFemale.setVisibility(View.VISIBLE);
                }
                break;
        }

        // if all ages selected, select 'all'
        // otherwise deselect 'all'
        if (rlShadowAgeAdult.getVisibility() == View.VISIBLE
                && rlShadowAgeChild15.getVisibility() == View.VISIBLE
                && rlShadowAgeChild11.getVisibility() == View.VISIBLE)
            rlShadowAgeAll.setVisibility(View.VISIBLE);
        else
            rlShadowAgeAll.setVisibility(View.INVISIBLE);

        // if both genders selected, select 'both'
        // otherwise deselect 'both'
        if (rlShadowGenderMale.getVisibility() == View.VISIBLE
                && rlShadowGenderFemale.getVisibility() == View.VISIBLE)
            rlShadowGenderBoth.setVisibility(View.VISIBLE);
        else
            rlShadowGenderBoth.setVisibility(View.INVISIBLE);
    }
}
