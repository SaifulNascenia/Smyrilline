package com.mcp.smyrilline.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcp.smyrilline.R;

/**
 * Created by raqib on 9/17/17.
 */

public class GroupListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_list, container, false);
        return rootView;
    }
}
