package com.mcp.smyrilline.fragment;

import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.interfaces.ApiInterfaces;
import com.mcp.smyrilline.model.restaurentsmodel.RestaurentDetails;
import com.mcp.smyrilline.service.ApiClient;
import com.mcp.smyrilline.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/14/17.
 */

//https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
public class IndividualResturentDetailsFragment extends Fragment implements View.OnClickListener {

    private View _rootView;

    private RecyclerView breakfastItemsRecylerView, lunchItemsRecylerView, dinnerItemsRecylerView;

    private DutyFreeAdapter dutyFreeAdapter, breakFastItemsRecylerViewAdapter,
            lunchItemsRecylerViewAdapter, dinnerItemsRecylerViewAdapter;

    private ImageView restaurentImage, breakfastItemsExpandImageview, openCloseExpandImageview,
            lunchItemExpandImageView, dinnerItemsExpandImageView;

    private Unbinder unbinder;

    private LinearLayout openCloseBottomIndicatorView;
    private RelativeLayout openCloseTimeLayout;

    private CardView restaurentTimeInfoCardView, restaurentInfoDetailsCardView, adultChildrenCardInfoView;
    private ExpandableTextView restaurentDetailsInfoTextView;

    private TextView breakFastTimeTextView, lunchTimeTextView, dinnerTimeTextView,
            expandTextView, adultsTitleTextView, childrenTitleTextView,
            topBreakfastTitleTextview, topBreakfastItemInfoTextview, topPrebookTitleTextview,
            topPreBookPriceTextView, topOnBoardPriceTextView, topSavingsAmountTextView,
            topTimeTitleTextView, topTimesTextView,
            bottomBreakfastTitleTextview, bottomBreakfastItemInfoTextview, bottomPrebookTitleTextview,
            bottomPreBookPriceTextView, bottomOnBoardPriceTextView, bottomSavingsAmountTextView,
            bottomTimeTitleTextView, bottomTimesTextView;

    private RestaurentDetails restaurentDetails;

    private Toolbar toolbar, noInternetViewToolbar;
    private CoordinatorLayout rootViewCoordinatorLayout;

    private View mLoadingView;
    private View noInternetConnetionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private static String restaurentId;
    private static String restaurentName;

    private IndividualResturentDetailsFragment thisClassContext = this;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_restaurent_details, container, false);

        //add dependency lib
        unbinder = ButterKnife.bind(this, _rootView);
        initView();


        fetchRestaurentDetails();

        return _rootView;
    }

    private void setWithoutInternetView() {
        mLoadingView.setVisibility(View.GONE);
        noInternetConnetionView.setVisibility(View.VISIBLE);
        /*AppUtils.withoutInternetConnectionView(getActivity(),
                getActivity().getIntent(),
                retryInternetBtn,
                "IndividualResturentDetailsFragment");
        */
    }

    private void fetchRestaurentDetails() {

        Retrofit retrofit = ApiClient.getClient();
        ApiInterfaces apiInterfaces = retrofit.create(ApiInterfaces.class);
        Call<RestaurentDetails> call = apiInterfaces.fetchRestaurentDetails(AppUtils.WP_PARAM_LANGUAGE,
                //        getArguments().getString("RESTAURENT_ID"));
                "1");

        call.enqueue(new Callback<RestaurentDetails>() {
            @Override
            public void onResponse(Call<RestaurentDetails> call, Response<RestaurentDetails> response) {

                restaurentDetails = response.body();
                //Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_LONG).show();
                Log.i("restaurentdetails", restaurentDetails.getName() + "/n");
                Log.i("restaurentdetails", response.body().toString());

                mLoadingView.setVisibility(View.GONE);


                toolbar.setBackground(null);

                //toolbar.setTitle(getArguments().getString("RESTAURENT_NAME"));
                toolbar.setTitle("Norona");

                ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
                collapsingToolbarLayout.setTitle(null);
                collapsingToolbarLayout.setTitleEnabled(false);


                bindViewWithApiData();

                setUprestaurentRecyclerView();


            }

            @Override
            public void onFailure(Call<RestaurentDetails> call, Throwable t) {

                rootViewCoordinatorLayout.setVisibility(View.GONE);
                noInternetViewToolbar.setVisibility(View.VISIBLE);
                //noInternetViewToolbar.setTitle(getArguments().getString("RESTAURENT_NAME"));
                noInternetViewToolbar.setTitle("Norona");
                noInternetViewToolbar.setBackgroundColor(
                        getActivity().getResources().getColor(R.color.colorPrimary));
                ((DrawerActivity) getActivity()).setToolbarAndToggle(noInternetViewToolbar);

                setWithoutInternetView();

            }
        });

    }

    private void initView() {

        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);
        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);

        mLoadingView = _rootView.findViewById(R.id.restaurantsLoadingView);
        noInternetConnetionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        retryInternetBtn.setOnClickListener(this);

        rootViewCoordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);
        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);

        restaurentImage = (ImageView) _rootView.findViewById(R.id.restaurent_image);

        restaurentTimeInfoCardView = (CardView) _rootView.findViewById(R.id.restaurent_time_info_view);

        openCloseExpandImageview = (ImageView) _rootView.findViewById(R.id.open_close_expand_imageview);
        openCloseExpandImageview.setOnClickListener(this);
        openCloseBottomIndicatorView = (LinearLayout) _rootView.findViewById(R.id.open_close_bottom_indicator_view);
        openCloseTimeLayout = (RelativeLayout) _rootView.findViewById(R.id.open_close_time_layout);

        breakFastTimeTextView = (TextView) _rootView.findViewById(R.id.restaurent_breakfast_time_textview);
        lunchTimeTextView = (TextView) _rootView.findViewById(R.id.restaurent_lunch_time_textview);
        dinnerTimeTextView = (TextView) _rootView.findViewById(R.id.restaurent_dinner_time_textview);

        restaurentInfoDetailsCardView = (CardView) _rootView.findViewById(R.id.restaurent_Details_view);
        restaurentDetailsInfoTextView = (ExpandableTextView) _rootView.findViewById(R.id.restaurent_info_expandable_TextView);
        expandTextView = (TextView) _rootView.findViewById(R.id.toggle_TextView);

        adultChildrenCardInfoView = (CardView) _rootView.findViewById(R.id.adult_children_view);
        adultsTitleTextView = (TextView) _rootView.findViewById(R.id.adults_title_textview);
        adultsTitleTextView.setOnClickListener(this);
        childrenTitleTextView = (TextView) _rootView.findViewById(R.id.children_title_textview);
        childrenTitleTextView.setOnClickListener(this);
        topBreakfastTitleTextview = (TextView) _rootView.findViewById(R.id.breakfast_title_textview);
        topBreakfastItemInfoTextview = (TextView) _rootView.findViewById(R.id.breakfast_item_info_textview);
        topPrebookTitleTextview = (TextView) _rootView.findViewById(R.id.prebook_title_textview);
        //  topPreBookPriceTextView = (TextView) _rootView.findViewById(R.id.prebook_price_textview);
        topOnBoardPriceTextView = (TextView) _rootView.findViewById(R.id.onboard_textview);
        topSavingsAmountTextView = (TextView) _rootView.findViewById(R.id.save_textview);
        topTimeTitleTextView = (TextView) _rootView.findViewById(R.id.times_title_textview);
        topTimesTextView = (TextView) _rootView.findViewById(R.id.top_time_textview);
        bottomBreakfastTitleTextview = (TextView) _rootView.findViewById(R.id.bottom_breakfast_title_textview);
        bottomBreakfastItemInfoTextview = (TextView) _rootView.findViewById(R.id.bottom_breakfast_info_textview);
        bottomPrebookTitleTextview = (TextView) _rootView.findViewById(R.id.bottom_prebook_title_textview);
        bottomPreBookPriceTextView = (TextView) _rootView.findViewById(R.id.bottom_prebook_price_textview);
        bottomOnBoardPriceTextView = (TextView) _rootView.findViewById(R.id.bottom_onboard_textview);
        bottomSavingsAmountTextView = (TextView) _rootView.findViewById(R.id.bottom_savings_textview);
        bottomTimeTitleTextView = (TextView) _rootView.findViewById(R.id.seating_times_textview);
        bottomTimesTextView = (TextView) _rootView.findViewById(R.id.bottom_times_textview);


        breakfastItemsExpandImageview = (ImageView) _rootView.findViewById(R.id.breakfast_list_expand_imageview);
        breakfastItemsExpandImageview.setOnClickListener(this);
        breakfastItemsRecylerView = (RecyclerView) _rootView.findViewById(R.id.breakfast_list_item_recylerView);

        dinnerItemsExpandImageView = (ImageView) _rootView.findViewById(R.id.dinner_list_expand_imageview);
        dinnerItemsExpandImageView.setOnClickListener(this);
        dinnerItemsRecylerView = (RecyclerView) _rootView.findViewById(R.id.dinner_list_item_recylerView);

        lunchItemExpandImageView = (ImageView) _rootView.findViewById(R.id.lunch_list_expand_imageview);
        lunchItemExpandImageView.setOnClickListener(this);
        lunchItemsRecylerView = (RecyclerView) _rootView.findViewById(R.id.lunch_list_item_recylerView);


    }

    private void bindViewWithApiData() {

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        restaurentDetails.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(restaurentImage);


        breakFastTimeTextView.setText(restaurentDetails.getBreakfastTime());
        lunchTimeTextView.setText(restaurentDetails.getLunchTime());
        dinnerTimeTextView.setText(restaurentDetails.getDinnerTime());


        restaurentDetailsInfoTextView.setText(restaurentDetails.getOpenCloseTimeText());

        restaurentDetailsInfoTextView.post(new Runnable() {
            @Override
            public void run() {
                //  int lineCount = restaurentDetailsInfoTextView.getLineCount();

                int start, end;
                String firstLine, secondLine, thirdLine;

                if (restaurentDetailsInfoTextView.getLineCount() == 3) {
                    // Use lineCount here
                    start = restaurentDetailsInfoTextView.getLayout().getLineStart(0);
                    end = restaurentDetailsInfoTextView.getLayout().getLineEnd(0);
                    firstLine = restaurentDetailsInfoTextView.getText().toString().substring(start, end);

                    start = restaurentDetailsInfoTextView.getLayout().getLineStart(1);
                    end = restaurentDetailsInfoTextView.getLayout().getLineEnd(1);
                    secondLine = restaurentDetailsInfoTextView.getText().toString().substring(start, end);

                    start = restaurentDetailsInfoTextView.getLayout().getLineStart(2);
                    end = restaurentDetailsInfoTextView.getLayout().getLineEnd(2);
                    thirdLine = restaurentDetailsInfoTextView.getText().toString().substring(start, end);

                    String totalThreeLineText = firstLine + secondLine + thirdLine;

                /*Log.i("textline", totalThreeLineText);
                Log.i("textline", totalThreeLineText.length() + " " +
                        restaurentDetails.getOpenCloseTimeText().length() + "/n" + thirdLine);
*/

                    if (!(totalThreeLineText.length() ==
                            restaurentDetails.getOpenCloseTimeText().length())
                            ) {

                        expandTextView.setVisibility(View.VISIBLE);
                    }
                }


            }
        });
        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        restaurentDetailsInfoTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurentDetailsInfoTextView.isExpanded()) {
                    restaurentDetailsInfoTextView.collapse();
                    expandTextView.setText("view more");

                } else {
                    restaurentDetailsInfoTextView.expand();
                    expandTextView.setText("view less");

                }
            }
        });

        setApiDataOnAdultMealsInfoView();
        setApiDataOnChildrenMealsInfoView();

    }

    public void setBreakfastListExpandImageviewAction() {


        if (breakfastItemsRecylerView.getVisibility() == View.GONE) {

            breakfastItemsExpandImageview.setImageResource(R.drawable.up_arrow);
            breakfastItemsRecylerView.setVisibility(View.VISIBLE);
        } else {
            breakfastItemsExpandImageview.setImageResource(R.drawable.down_arrow);
            breakfastItemsRecylerView.setVisibility(View.GONE);

        }
    }

    private void setLunchExpandImageViewAction() {

        if (lunchItemsRecylerView.getVisibility() == View.GONE) {

            lunchItemExpandImageView.setImageResource(R.drawable.up_arrow);
            lunchItemsRecylerView.setVisibility(View.VISIBLE);
        } else {
            lunchItemExpandImageView.setImageResource(R.drawable.down_arrow);
            lunchItemsRecylerView.setVisibility(View.GONE);

        }
    }

    private void setDinnerExpandViewImageViewAction() {

        if (dinnerItemsRecylerView.getVisibility() == View.GONE) {

            dinnerItemsExpandImageView.setImageResource(R.drawable.up_arrow);
            dinnerItemsRecylerView.setVisibility(View.VISIBLE);
        } else {
            dinnerItemsExpandImageView.setImageResource(R.drawable.down_arrow);
            dinnerItemsRecylerView.setVisibility(View.GONE);

        }
    }

    private void setUprestaurentRecyclerView() {

        RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(getActivity(), 3);
        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getActivity(), 3);
        RecyclerView.LayoutManager mLayoutManager3 = new GridLayoutManager(getActivity(), 3);

        breakFastItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurentDetails.getBreakfastItems(),
                "Breakfast"
        );

        breakfastItemsRecylerView.setLayoutManager(mLayoutManager1);
        breakfastItemsRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        breakfastItemsRecylerView.setItemAnimator(new DefaultItemAnimator());
        breakfastItemsRecylerView.setAdapter(breakFastItemsRecylerViewAdapter);


        lunchItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurentDetails.getLunchItems(),
                "Lunch");

        lunchItemsRecylerView.setLayoutManager(mLayoutManager2);
        lunchItemsRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        lunchItemsRecylerView.setItemAnimator(new DefaultItemAnimator());
        lunchItemsRecylerView.setAdapter(lunchItemsRecylerViewAdapter);


        dinnerItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurentDetails.getDinnerItems(),
                "Dinner");

        dinnerItemsRecylerView.setLayoutManager(mLayoutManager3);
        dinnerItemsRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        dinnerItemsRecylerView.setItemAnimator(new DefaultItemAnimator());
        dinnerItemsRecylerView.setAdapter(dinnerItemsRecylerViewAdapter);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.breakfast_list_expand_imageview:
                setBreakfastListExpandImageviewAction();
                break;
            case R.id.lunch_list_expand_imageview:
                setLunchExpandImageViewAction();
                break;
            case R.id.dinner_list_expand_imageview:
                setDinnerExpandViewImageViewAction();
                break;
            case R.id.open_close_expand_imageview:
                setOpenCloseExpandImageviewAction();
                break;
            case R.id.adults_title_textview:
                setAdultsTitleTextViewAction();
                break;
            case R.id.children_title_textview:
                setChildrenTitleTextviewAction();
                break;
            case R.id.retry_internet:
                Toast.makeText(getActivity(), "retry", Toast.LENGTH_LONG).show();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(thisClassContext).attach(thisClassContext).commit();
                break;
        }

    }

    private void setAdultsTitleTextViewAction() {

        adultsTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.colorPrimary));
        adultsTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.windowBackground));

        childrenTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.light_grey_bkg));
        childrenTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.grey_black_textcolor));

        setApiDataOnAdultMealsInfoView();

    }

    private void setApiDataOnAdultMealsInfoView() {

        // Top view
        topBreakfastTitleTextview.setText(restaurentDetails.getAdultMeals().get(0)
                .getName());
        topBreakfastItemInfoTextview.setText(restaurentDetails.getAdultMeals().get(0)
                .getTag());

        topPrebookTitleTextview.setText(Html.fromHtml("Prebook price: " + "<font color=#0a95dc>" +
                restaurentDetails.getAdultMeals().get(0)
                        .getPrebookPrice() + "</font>"
        ));

        /*topPreBookPriceTextView.setText(restaurentDetails.getAdultMeals().get(0)
                .getPrebookPrice());
        */
        topOnBoardPriceTextView.setText("Onboard price: " + restaurentDetails.getAdultMeals().get(0)
                .getOnboardPrice());
        topSavingsAmountTextView.setText("Save: " + restaurentDetails.getAdultMeals().get(0)
                .getSave());

        if (restaurentDetails.getAdultMeals().get(0).getTime() != null) {

            topTimeTitleTextView.setText("Times");
            topTimesTextView.setText(restaurentDetails.getAdultMeals().get(0).getTime());
        } else {
            topTimeTitleTextView.setText("Seatings: " + restaurentDetails.getAdultMeals().get(0)
                    .getSeatingTime());
            topTimesTextView.setText(restaurentDetails.getAdultMeals().get(0).getSeatingText());
        }

        // Bottom View

        bottomBreakfastTitleTextview.setText(restaurentDetails.getAdultMeals().get(1)
                .getName());
        bottomBreakfastItemInfoTextview.setText(restaurentDetails.getAdultMeals().get(1)
                .getTag());
        //bottomPrebookTitleTextview.setText("Prebook price: ");

        bottomPrebookTitleTextview.setText(Html.fromHtml("Prebook price: " + "<font color=#0a95dc>" +
                restaurentDetails.getAdultMeals().get(1)
                        .getPrebookPrice() + "</font>"
        ));

        /*bottomPreBookPriceTextView.setText(restaurentDetails.getAdultMeals().get(1)
                .getPrebookPrice());
        */

        bottomOnBoardPriceTextView.setText("Onboard price: " + restaurentDetails.getAdultMeals().get(1)
                .getOnboardPrice());
        bottomSavingsAmountTextView.setText("Save: " + restaurentDetails.getAdultMeals().get(1)
                .getSave());

        if (restaurentDetails.getAdultMeals().get(1).getTime() != null) {

            bottomTimeTitleTextView.setText("Times");
            bottomTimesTextView.setText(restaurentDetails.getAdultMeals().get(1).getTime());
        } else {
            bottomTimeTitleTextView.setText("Seatings: " + restaurentDetails.getAdultMeals().get(1)
                    .getSeatingTime());
            bottomTimesTextView.setText(restaurentDetails.getAdultMeals().get(1).getSeatingText());
        }


    }

    private void setChildrenTitleTextviewAction() {

        adultsTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.light_grey_bkg));
        adultsTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.grey_black_textcolor));
        childrenTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.colorPrimary));
        childrenTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.windowBackground));

        setApiDataOnChildrenMealsInfoView();

    }

    private void setApiDataOnChildrenMealsInfoView() {

        // Top view
        topBreakfastTitleTextview.setText(restaurentDetails.getChildrenMeals().get(0)
                .getName());
        topBreakfastItemInfoTextview.setText(restaurentDetails.getChildrenMeals().get(0)
                .getTag());

        topPrebookTitleTextview.setText(Html.fromHtml("Prebook price: " + "<font color=#0a95dc>" +
                restaurentDetails.getChildrenMeals().get(0)
                        .getPrebookPrice() + "</font>"
        ));

        /*topPreBookPriceTextView.setText(restaurentDetails.getChildrenMeals().get(0)
                .getPrebookPrice());
        */
        topOnBoardPriceTextView.setText("Onboard price: " + restaurentDetails.getChildrenMeals().get(0)
                .getOnboardPrice());
        topSavingsAmountTextView.setText("Save: " + restaurentDetails.getChildrenMeals().get(0)
                .getSave());

        if (restaurentDetails.getChildrenMeals().get(0).getTime() != null) {

            topTimeTitleTextView.setText("Times");
            topTimesTextView.setText(restaurentDetails.getChildrenMeals().get(0).getTime());
        } else {
            topTimeTitleTextView.setText("Seatings: " + restaurentDetails.getChildrenMeals().get(0)
                    .getSeatingTime());
            topTimesTextView.setText(restaurentDetails.getChildrenMeals().get(0).getSeatingText());
        }

        // Bottom View

        bottomBreakfastTitleTextview.setText(restaurentDetails.getChildrenMeals().get(1)
                .getName());
        bottomBreakfastItemInfoTextview.setText(restaurentDetails.getChildrenMeals().get(1)
                .getTag());
        //bottomPrebookTitleTextview.setText("Prebook price: ");

        bottomPrebookTitleTextview.setText(Html.fromHtml("Prebook price: " + "<font color=#0a95dc>" +
                restaurentDetails.getChildrenMeals().get(1)
                        .getPrebookPrice() + "</font>"
        ));

        /*bottomPreBookPriceTextView.setText(restaurentDetails.getChildrenMeals().get(1)
                .getPrebookPrice());
        */

        bottomOnBoardPriceTextView.setText("Onboard price: " + restaurentDetails.getChildrenMeals().get(1)
                .getOnboardPrice());
        bottomSavingsAmountTextView.setText("Save: " + restaurentDetails.getChildrenMeals().get(1)
                .getSave());

        if (restaurentDetails.getChildrenMeals().get(1).getTime() != null) {

            bottomTimeTitleTextView.setText("Times");
            bottomTimesTextView.setText(restaurentDetails.getChildrenMeals().get(1).getTime());
        } else {
            bottomTimeTitleTextView.setText("Seatings: " + restaurentDetails.getChildrenMeals().get(1)
                    .getSeatingTime());
            bottomTimesTextView.setText(restaurentDetails.getChildrenMeals().get(1).getSeatingText());
        }

    }


    private void setOpenCloseExpandImageviewAction() {

        openCloseExpandImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (openCloseBottomIndicatorView.getVisibility() == View.INVISIBLE) {
                    openCloseBottomIndicatorView.setVisibility(View.VISIBLE);
                    openCloseTimeLayout.setVisibility(View.VISIBLE);
                    openCloseExpandImageview.setImageResource(R.drawable.up_arrow);

                } else {
                    openCloseBottomIndicatorView.setVisibility(View.INVISIBLE);
                    openCloseTimeLayout.setVisibility(View.GONE);
                    openCloseExpandImageview.setImageResource(R.drawable.down_arrow);

                }

            }
        });


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
