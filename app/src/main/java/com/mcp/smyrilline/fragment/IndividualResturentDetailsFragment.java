package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcp.smyrilline.R;

/**
 * Created by saiful on 6/14/17.
 */

//https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
public class IndividualResturentDetailsFragment extends Fragment {

    private View _rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_restaurent_details, container, false);


        return _rootView;
    }
}
