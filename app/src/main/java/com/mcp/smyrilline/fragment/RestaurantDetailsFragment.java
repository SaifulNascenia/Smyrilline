package com.mcp.smyrilline.fragment;

import static com.mcp.smyrilline.util.AppUtils.dpToPx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.blogc.android.views.ExpandableTextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.listener.RecylerViewItemClickListener;
import com.mcp.smyrilline.listener.RecylerViewTouchEventListener;
import com.mcp.smyrilline.model.restaurant.BreakfastItem;
import com.mcp.smyrilline.model.restaurant.DinnerItem;
import com.mcp.smyrilline.model.restaurant.LunchItem;
import com.mcp.smyrilline.model.restaurant.RestaurantDetails;
import com.mcp.smyrilline.rest.RetrofitClient;
import com.mcp.smyrilline.rest.RetrofitInterfaces;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.view.GridSpacingItemDecoration;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by saiful on 6/14/17.
 */

//https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
public class RestaurantDetailsFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView breakfastItemsRecyclerView, lunchItemsRecyclerView, dinnerItemsRecyclerView;
    private DutyFreeAdapter breakFastItemsRecyclerViewAdapter,
            lunchItemsRecyclerViewAdapter, dinnerItemsRecyclerViewAdapter;
    private ImageView restaurantImage, breakfastItemsExpandImageview, openCloseExpandImageview,
            lunchItemExpandImageView, dinnerItemsExpandImageView;
    private LinearLayout openCloseBottomIndicatorView;
    private RelativeLayout openCloseTimeLayout;
    private ExpandableTextView restaurantDetailsExpandableTextView;
    private TextView breakfastOpeningAndClosingTimeTextView, lunchOpeningAndClosingTimeTextView,
            dinnerOpeningAndClosingTimeTextView, restaurantDetailsMoreView, adultsTitleTextView,
            childrenTitleTextView, breakfastTitleTextview, breakfastAvailableStatusTextview,
            breakfastPrebookPriceTextview, breakfastOnBoardPriceTextView, breakfastSaveAmountTextView,
            breakfastTimeTitleTextView, breakfastTimeTextView,
            dinnerTitleTextview, dinnerAvailableStatusTextview, dinnerPrebookPriceTextview,
            dinnerOnBoardPriceTextView, dinnerSaveAmountTextView,
            dinnerTimeTitleTextView, dinnerTimeTextView;
    private RestaurantDetails restaurantDetails;
    private Toolbar toolbar, noConnectionToolbar;
    private CoordinatorLayout coordinatorLayout;
    private View loadingProgressView;
    private View noConnectionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RestaurantDetailsFragment thisFragment = this;
    private RecyclerView.LayoutManager mBreakfastRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager mLunchRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager mDinnerRecyclerViewLayoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        initView();
        fetchRestaurantDetails();
        return rootView;
    }

    private void fetchRestaurantDetails() {
        if (AppUtils.isNetworkAvailable(getActivity())) {
            Retrofit retrofit = RetrofitClient.getClient();
            RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
            Call<RestaurantDetails> call = retrofitInterfaces
                .fetchRestaurantDetails(getString(R.string.wp_language_param),
                            getArguments().getString(RestaurantFragment.RESTAURANT_ID));

            call.enqueue(new Callback<RestaurantDetails>() {
                @Override
                public void onResponse(Call<RestaurantDetails> call,
                        Response<RestaurantDetails> response) {

                    restaurantDetails = response.body();
                    loadingProgressView.setVisibility(View.GONE);
                    toolbar.setBackground(null);
                    collapsingToolbarLayout.setTitle(null);
                    collapsingToolbarLayout.setTitleEnabled(false);

                    setApiDataOnView();
                    setupMenuRecyclerViews();
                }

                @Override
                public void onFailure(Call<RestaurantDetails> call, Throwable t) {
                    AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                            loadingProgressView, coordinatorLayout, noConnectionView,
                            noConnectionToolbar,
                            getArguments().getString(RestaurantFragment.RESTAURANT_NAME));
                }
            });
        } else
            AppUtils.showNoConnectionViewWithExtraToolbar(getActivity(), thisFragment,
                    loadingProgressView,
                    coordinatorLayout, noConnectionView,
                    noConnectionToolbar,
                    getArguments().getString(RestaurantFragment.RESTAURANT_NAME));
    }

    private void initView() {

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().getString(RestaurantFragment.RESTAURANT_NAME));
        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);

        noConnectionToolbar = (Toolbar) rootView.findViewById(R.id.extra_toolbar);
        loadingProgressView = rootView.findViewById(R.id.restaurantsLoadingView);
        noConnectionView = rootView.findViewById(R.id.no_connection_layout);
        retryInternetBtn = (Button) rootView.findViewById(R.id.retry_connect);
        retryInternetBtn.setOnClickListener(this);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.root_coordinator_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView
                .findViewById(R.id.collapsing_toolbar);

        restaurantImage = (ImageView) rootView.findViewById(R.id.restaurant_image);

        openCloseExpandImageview = (ImageView) rootView
                .findViewById(R.id.open_close_expand_imageview);
        openCloseExpandImageview.setOnClickListener(this);
        openCloseBottomIndicatorView = (LinearLayout) rootView
                .findViewById(R.id.open_close_bottom_indicator_view);
        openCloseTimeLayout = (RelativeLayout) rootView.findViewById(R.id.open_close_time_layout);

        breakfastOpeningAndClosingTimeTextView = (TextView) rootView
                .findViewById(R.id.breakfast_opening_and_closing_time_textview);
        lunchOpeningAndClosingTimeTextView = (TextView) rootView
                .findViewById(R.id.lunch_opening_and_closing_time_textview);
        dinnerOpeningAndClosingTimeTextView = (TextView) rootView
                .findViewById(R.id.dinner_opening_and_closing_time_textview);

        restaurantDetailsExpandableTextView = (ExpandableTextView) rootView
                .findViewById(R.id.restaurantDetailsExpandableTextView);
        restaurantDetailsMoreView = (TextView) rootView
                .findViewById(R.id.restaurantDetailsMoreView);

        adultsTitleTextView = (TextView) rootView.findViewById(R.id.adults_title_textview);
        adultsTitleTextView.setOnClickListener(this);
        childrenTitleTextView = (TextView) rootView.findViewById(R.id.children_title_textview);
        childrenTitleTextView.setOnClickListener(this);

        breakfastTitleTextview = (TextView) rootView.findViewById(R.id.breakfast_title_textview);
        breakfastAvailableStatusTextview = (TextView) rootView
                .findViewById(R.id.breakfast_available_status_textview);
        breakfastPrebookPriceTextview = (TextView) rootView
                .findViewById(R.id.breakfast_prebook_price_textview);
        //  topPreBookPriceTextView = (TextView) rootView.findViewById(R.id.prebook_price_textview);
        breakfastOnBoardPriceTextView = (TextView) rootView
                .findViewById(R.id.breakfast_onboard_textview);
        breakfastSaveAmountTextView = (TextView) rootView
                .findViewById(R.id.breakfast_save_textview);
        breakfastTimeTitleTextView = (TextView) rootView
                .findViewById(R.id.breakfast_time_title_textview);
        breakfastTimeTextView = (TextView) rootView.findViewById(R.id.breakfast_time_textview);

        dinnerTitleTextview = (TextView) rootView.findViewById(R.id.dinner_title_textview);
        dinnerAvailableStatusTextview = (TextView) rootView
                .findViewById(R.id.dinner_available_tatus_textview);
        dinnerPrebookPriceTextview = (TextView) rootView
                .findViewById(R.id.dinner_prebook_price_textview);
        //bottomPreBookPriceTextView = (TextView) rootView.findViewById(R.id.bottom_prebook_price_textview);
        dinnerOnBoardPriceTextView = (TextView) rootView
                .findViewById(R.id.dinner_onboard_price_textview);
        dinnerSaveAmountTextView = (TextView) rootView
                .findViewById(R.id.dinner_save_amount_textview);
        dinnerTimeTitleTextView = (TextView) rootView
                .findViewById(R.id.dinner_seatings_times_textview);
        dinnerTimeTextView = (TextView) rootView.findViewById(R.id.dinner_time_textview);

        breakfastItemsExpandImageview = (ImageView) rootView
                .findViewById(R.id.breakfast_list_expand_imageview);
        breakfastItemsExpandImageview.setOnClickListener(this);
        breakfastItemsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.breakfast_list_item_recylerView);
        breakfastItemsRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        breakfastItemsRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        BreakfastItem breakfastItem = restaurantDetails.getBreakfastItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, breakfastItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, breakfastItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, breakfastItem.getSubheader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, breakfastItem.getHeader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, breakfastItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME,
                                RestaurantFragment.class.getSimpleName());

                        DutyFreeDetailsFragment dutyFreeDetailsFragment = new DutyFreeDetailsFragment();
                        dutyFreeDetailsFragment.setArguments(AppUtils.getBundleObj());

                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, dutyFreeDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        lunchItemExpandImageView = (ImageView) rootView
                .findViewById(R.id.lunch_list_expand_imageview);
        lunchItemExpandImageView.setOnClickListener(this);
        lunchItemsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.lunch_list_item_recylerView);
        lunchItemsRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        lunchItemsRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        LunchItem lunchItem = restaurantDetails.getLunchItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, lunchItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, lunchItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, lunchItem.getSubheader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, lunchItem.getHeader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, lunchItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME,
                                RestaurantFragment.class.getSimpleName());

                        DutyFreeDetailsFragment dutyFreeDetailsFragment = new DutyFreeDetailsFragment();
                        dutyFreeDetailsFragment.setArguments(AppUtils.getBundleObj());

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, dutyFreeDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        dinnerItemsExpandImageView = (ImageView) rootView
                .findViewById(R.id.dinner_list_expand_imageview);
        dinnerItemsExpandImageView.setOnClickListener(this);
        dinnerItemsRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.dinner_list_item_recylerView);
        dinnerItemsRecyclerView
                .addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                        dinnerItemsRecyclerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        DinnerItem dinnerItem = restaurantDetails.getDinnerItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, dinnerItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, dinnerItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, dinnerItem.getSubheader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, dinnerItem.getHeader());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, dinnerItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME,
                                RestaurantFragment.class.getSimpleName());

                        DutyFreeDetailsFragment dutyFreeDetailsFragment = new DutyFreeDetailsFragment();
                        dutyFreeDetailsFragment.setArguments(AppUtils.getBundleObj());


                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, dutyFreeDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    private void setApiDataOnView() {
        coordinatorLayout.setVisibility(View.VISIBLE);

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        restaurantDetails.getImageUrl())
                .placeholder(R.drawable.img_placeholder_thumb)
                .into(restaurantImage);

        breakfastOpeningAndClosingTimeTextView.setText(restaurantDetails.getBreakfastTime());
        lunchOpeningAndClosingTimeTextView.setText(restaurantDetails.getLunchTime());
        dinnerOpeningAndClosingTimeTextView.setText(restaurantDetails.getDinnerTime());

        restaurantDetailsExpandableTextView.setText(restaurantDetails.getOpenCloseTimeText());
        AppUtils.handleExpandableTextView(restaurantDetailsExpandableTextView,
                restaurantDetailsMoreView);

        setApiDataOnAdultMealsInfoView();
        setApiDataOnChildrenMealsInfoView();
    }

    public void setBreakfastListExpandImageviewAction() {
        if (breakfastItemsRecyclerView.getVisibility() == View.GONE) {
            breakfastItemsExpandImageview.setImageResource(R.drawable.ic_collapse_list);
            breakfastItemsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            breakfastItemsExpandImageview.setImageResource(R.drawable.ic_expand_list);
            breakfastItemsRecyclerView.setVisibility(View.GONE);

        }
    }

    private void setLunchExpandImageViewAction() {
        if (lunchItemsRecyclerView.getVisibility() == View.GONE) {
            lunchItemExpandImageView.setImageResource(R.drawable.ic_collapse_list);
            lunchItemsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            lunchItemExpandImageView.setImageResource(R.drawable.ic_expand_list);
            lunchItemsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setDinnerExpandViewImageViewAction() {
        if (dinnerItemsRecyclerView.getVisibility() == View.GONE) {
            dinnerItemsExpandImageView.setImageResource(R.drawable.ic_collapse_list);
            dinnerItemsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            dinnerItemsExpandImageView.setImageResource(R.drawable.ic_expand_list);
            dinnerItemsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupMenuRecyclerViews() {
        mBreakfastRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);
        mLunchRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDinnerRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);

        breakFastItemsRecyclerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getBreakfastItems(),
                BreakfastItem.class.getSimpleName()
        );

        breakfastItemsRecyclerView.setLayoutManager(mBreakfastRecyclerViewLayoutManager);
        breakfastItemsRecyclerView
                .addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        breakfastItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        breakfastItemsRecyclerView.setAdapter(breakFastItemsRecyclerViewAdapter);

        lunchItemsRecyclerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getLunchItems(),
                LunchItem.class.getSimpleName());

        lunchItemsRecyclerView.setLayoutManager(mLunchRecyclerViewLayoutManager);
        lunchItemsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        lunchItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        lunchItemsRecyclerView.setAdapter(lunchItemsRecyclerViewAdapter);

        dinnerItemsRecyclerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getDinnerItems(),
                DinnerItem.class.getSimpleName());

        dinnerItemsRecyclerView.setLayoutManager(mDinnerRecyclerViewLayoutManager);
        dinnerItemsRecyclerView
                .addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        dinnerItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dinnerItemsRecyclerView.setAdapter(dinnerItemsRecyclerViewAdapter);
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
            case R.id.retry_connect:
                AppUtils.restartFragment(getActivity(), this);
                break;
        }

    }

    private void setAdultsTitleTextViewAction() {
        adultsTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.colorPrimary));
        adultsTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.windowBackground));

        childrenTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.bkg_light_grey));
        childrenTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.grey_black_textcolor));
        setApiDataOnAdultMealsInfoView();
    }

    private void setApiDataOnAdultMealsInfoView() {
        breakfastTitleTextview.setText(restaurantDetails.getAdultMeals().get(0)
                .getName());
        breakfastAvailableStatusTextview.setText(restaurantDetails.getAdultMeals().get(0)
                .getTag());

        breakfastPrebookPriceTextview.setText(Html.fromHtml(getString(R.string.pre_book_price_text)
                + "<font color=#0a95dc>" +
                restaurantDetails.getAdultMeals().get(0).getPrebookPrice() +
                "</font>"));
        breakfastOnBoardPriceTextView.setText(getString(R.string.onboard_price_text) +
                restaurantDetails.getAdultMeals().get(0).getOnboardPrice());
        breakfastSaveAmountTextView.setText(getString(R.string.save_text) +
                restaurantDetails.getAdultMeals().get(0)
                        .getSave());

        if (restaurantDetails.getAdultMeals().get(0).getTime() != null) {

            breakfastTimeTitleTextView.setText(getString(R.string.times_text));
            breakfastTimeTextView.setText(restaurantDetails.getAdultMeals().get(0).getTime());
        } else {
            breakfastTimeTitleTextView.setText(getString(R.string.seatings_text) + restaurantDetails.getAdultMeals().get(0)
                    .getSeatingTime());
            breakfastTimeTextView.setText(restaurantDetails.getAdultMeals().get(0).getSeatingText());
        }

        dinnerTitleTextview.setText(restaurantDetails.getAdultMeals().get(1).getName());
        dinnerAvailableStatusTextview.setText(restaurantDetails.getAdultMeals().get(1).getTag());
        dinnerPrebookPriceTextview.setText(Html.fromHtml(getString(R.string.pre_book_price_text) +
                "<font color=#0a95dc>" +
                restaurantDetails.getAdultMeals().get(1)
                        .getPrebookPrice() + "</font>"
        ));

        dinnerOnBoardPriceTextView.setText(getString(R.string.onboard_price_text) + restaurantDetails.getAdultMeals().get(1)
                .getOnboardPrice());
        dinnerSaveAmountTextView.setText(getString(R.string.save_text) + restaurantDetails.getAdultMeals().get(1)
                .getSave());

        if (restaurantDetails.getAdultMeals().get(1).getTime() != null) {

            dinnerTimeTitleTextView.setText(getString(R.string.times_text));
            dinnerTimeTextView.setText(restaurantDetails.getAdultMeals().get(1).getTime());
        } else {
            dinnerTimeTitleTextView.setText(getString(R.string.seatings_text) + restaurantDetails.getAdultMeals().get(1)
                    .getSeatingTime());
            dinnerTimeTextView.setText(restaurantDetails.getAdultMeals().get(1).getSeatingText());
        }
    }

    private void setChildrenTitleTextviewAction() {
        adultsTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.bkg_light_grey));
        adultsTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.grey_black_textcolor));
        childrenTitleTextView.setBackgroundColor(
                getActivity().getResources().getColor(R.color.colorPrimary));
        childrenTitleTextView.setTextColor(
                getActivity().getResources().getColor(R.color.windowBackground));

        setApiDataOnChildrenMealsInfoView();
    }

    private void setApiDataOnChildrenMealsInfoView() {
        breakfastTitleTextview.setText(restaurantDetails.getChildrenMeals().get(0)
                .getName());
        breakfastAvailableStatusTextview.setText(restaurantDetails.getChildrenMeals().get(0)
                .getTag());

        breakfastPrebookPriceTextview.setText(Html.fromHtml(getString(R.string.pre_book_price_text)
                + "<font color=#0a95dc>" +
                restaurantDetails.getChildrenMeals().get(0)
                        .getPrebookPrice() + "</font>"
        ));

        breakfastOnBoardPriceTextView.setText(getString(R.string.onboard_price_text) +
                restaurantDetails.getChildrenMeals().get(0)
                        .getOnboardPrice());
        breakfastSaveAmountTextView.setText(getString(R.string.save_text) +
                restaurantDetails.getChildrenMeals().get(0)
                        .getSave());

        if (restaurantDetails.getChildrenMeals().get(0).getTime() != null) {

            breakfastTimeTitleTextView.setText(getString(R.string.times_text));
            breakfastTimeTextView.setText(restaurantDetails.getChildrenMeals().get(0).getTime());
        } else {
            breakfastTimeTitleTextView.setText(getString(R.string.seatings_text) +
                    restaurantDetails.getChildrenMeals().get(0)
                            .getSeatingTime());
            breakfastTimeTextView.setText(restaurantDetails.getChildrenMeals().get(0).getSeatingText());
        }

        dinnerTitleTextview.setText(restaurantDetails.getChildrenMeals().get(1)
                .getName());
        dinnerAvailableStatusTextview.setText(restaurantDetails.getChildrenMeals().get(1)
                .getTag());
        dinnerPrebookPriceTextview.setText(Html.fromHtml(getString(R.string.pre_book_price_text) +
                "<font color=#0a95dc>" +
                restaurantDetails.getChildrenMeals().get(1)
                        .getPrebookPrice() + "</font>"
        ));

        dinnerOnBoardPriceTextView.setText(getString(R.string.onboard_price_text) +
                restaurantDetails.getChildrenMeals().get(1)
                        .getOnboardPrice());
        dinnerSaveAmountTextView.setText(getString(R.string.save_text) +
                restaurantDetails.getChildrenMeals().get(1).getSave());

        if (restaurantDetails.getChildrenMeals().get(1).getTime() != null) {

            dinnerTimeTitleTextView.setText(getString(R.string.times_text));
            dinnerTimeTextView.setText(restaurantDetails.getChildrenMeals().get(1).getTime());
        } else {
            dinnerTimeTitleTextView.setText(getString(R.string.seatings_text) +
                    restaurantDetails.getChildrenMeals().get(1).getSeatingTime());
            dinnerTimeTextView.setText(restaurantDetails.getChildrenMeals().get(1).getSeatingText());
        }
    }

    private void setOpenCloseExpandImageviewAction() {
        openCloseExpandImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openCloseBottomIndicatorView.getVisibility() == View.GONE) {
                    openCloseBottomIndicatorView.setVisibility(View.VISIBLE);
                    openCloseTimeLayout.setVisibility(View.VISIBLE);
                    openCloseExpandImageview.setImageResource(R.drawable.ic_collapse_list);

                } else {
                    openCloseBottomIndicatorView.setVisibility(View.GONE);
                    openCloseTimeLayout.setVisibility(View.GONE);
                    openCloseExpandImageview.setImageResource(R.drawable.ic_expand_list);
                }
            }
        });
    }
}
