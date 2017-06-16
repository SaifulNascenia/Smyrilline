package com.mcp.smyrilline.fragment;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.model.dutyfreemodels.Child;
import com.mcp.smyrilline.model.dutyfreemodels.DutyFree;
import com.mcp.smyrilline.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by saiful on 6/14/17.
 */

//https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
public class IndividualResturentDetailsFragment extends Fragment implements View.OnClickListener {

    private View _rootView;

    private RecyclerView breakfastListItemRecylerView;
    private RecyclerView dinnerListItemRecylerView;

    private DutyFreeAdapter dutyFreeAdapter;
    private DutyFree dutyFree;

    List<Child> breakFastChildrenList;

    ImageView breakfastListExpandImageview;

    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_restaurent_details, container, false);


        //add dependency lib
        unbinder = ButterKnife.bind(this, _rootView);
        initView();

        breakFastChildrenList = new ArrayList<>();
        Child c1 = new Child("1", "Cake", null, "hellow", "hello 2", null);
        Child c2 = new Child("1", "Cake", null, "hellow", "hello 2", null);
        Child c3 = new Child("1", "Cake", null, "hellow", "hello 2", null);
        Child c4 = new Child("1", "Cake", null, "hellow", "hello 2", null);
        Child c5 = new Child("1", "Cake", null, "hellow", "hello 2", null);
        breakFastChildrenList.add(c1);
        breakFastChildrenList.add(c2);
        breakFastChildrenList.add(c3);
        breakFastChildrenList.add(c4);
        breakFastChildrenList.add(c5);

        dutyFree = new DutyFree();
        dutyFree.setChildren(breakFastChildrenList);
        setUprestaurentRecyclerView();

        return _rootView;
    }

    private void initView() {

        breakfastListExpandImageview = (ImageView) _rootView.findViewById(R.id.breakfast_list_expand_imageview);
        breakfastListExpandImageview.setOnClickListener(this);
        breakfastListItemRecylerView = (RecyclerView) _rootView.findViewById(R.id.breakfast_list_item_recylerView);
        breakfastListItemRecylerView.setVisibility(View.GONE);
        dinnerListItemRecylerView = (RecyclerView) _rootView.findViewById(R.id.dinner_list_item_recylerView);

     /*   breakfastListExpandImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breakfastListItemRecylerView.getVisibility() == View.GONE) {

                    breakfastListExpandImageview.setImageResource(R.drawable.up_arrow);
                    breakfastListItemRecylerView.setVisibility(View.VISIBLE);
                } else {
                    breakfastListExpandImageview.setImageResource(R.drawable.down_arrow);
                    breakfastListItemRecylerView.setVisibility(View.GONE);

                }
            }
        });*/

    }

    public void setBreakfastListExpandImageviewAction() {


        if (breakfastListItemRecylerView.getVisibility() == View.GONE) {

            breakfastListExpandImageview.setImageResource(R.drawable.up_arrow);
            breakfastListItemRecylerView.setVisibility(View.VISIBLE);
        } else {
            breakfastListExpandImageview.setImageResource(R.drawable.down_arrow);
            breakfastListItemRecylerView.setVisibility(View.GONE);

        }
    }

    private void setUprestaurentRecyclerView() {

        dutyFreeAdapter = new DutyFreeAdapter(getActivity(), dutyFree.getChildren(), AppUtils.fragmentList[4]);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),3);
        breakfastListItemRecylerView.setLayoutManager(mLayoutManager);
        breakfastListItemRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        breakfastListItemRecylerView.setItemAnimator(new DefaultItemAnimator());
        breakfastListItemRecylerView.setAdapter(dutyFreeAdapter);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.breakfast_list_expand_imageview:
                setBreakfastListExpandImageviewAction();
                break;
        }

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
