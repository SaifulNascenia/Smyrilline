package com.mcp.smyrilline.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.DutyFreeAdapter;
import com.mcp.smyrilline.interfaces.ViewDataBinder;
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

import at.blogc.android.views.ExpandableTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mcp.smyrilline.util.AppUtils.dpToPx;

/**
 * Created by saiful on 6/14/17.
 */

//https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
public class ResturantDetailsFragment extends Fragment implements View.OnClickListener, ViewDataBinder {

    public static int startLineCount, endLineCount;
    public static String firstLineText, secondLineText, thirdLineText;
    public static String totalThreeLineText;
    private View _rootView;
    private RecyclerView breakfastItemsRecylerView, lunchItemsRecylerView, dinnerItemsRecylerView;
    private DutyFreeAdapter breakFastItemsRecylerViewAdapter,
            lunchItemsRecylerViewAdapter, dinnerItemsRecylerViewAdapter;
    private ImageView restaurantImage, breakfastItemsExpandImageview, openCloseExpandImageview,
            lunchItemExpandImageView, dinnerItemsExpandImageView;
    private LinearLayout openCloseBottomIndicatorView;
    private RelativeLayout openCloseTimeLayout;
    private ExpandableTextView restaurantDetailsInfoTextView;
    private TextView breakfastOpeningAndClosingTimeTextView, lunchOpeningAndClosingTimeTextView,
            dinnerOpeningAndClosingTimeTextView, expandTextView, adultsTitleTextView,
            childrenTitleTextView, breakfastTitleTextview, breakfastAvailableStatusTextview,
            breakfastPrebookPriceTextview, breakfastOnBoardPriceTextView, breakfastSaveAmountTextView,
            breakfastTimeTitleTextView, breakfastTimeTextView,
            dinnerTitleTextview, dinnerAvailableStatusTextview, dinnerPrebookPriceTextview,
            dinnerOnBoardPriceTextView, dinnerSaveAmountTextView,
            dinnerTimeTitleTextView, dinnerTimeTextView;
    private RestaurantDetails restaurantDetails;
    private Toolbar toolbar, noInternetViewToolbar;
    private CoordinatorLayout rootViewCoordinatorLayout;
    private View mLoadingView;
    private View noInternetConnectionView;
    private Button retryInternetBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ResturantDetailsFragment thisClassContext = this;
    private RecyclerView.LayoutManager mBreakfastRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager mLunchRecyclerViewLayoutManager;
    private RecyclerView.LayoutManager mDinnerRecyclerViewLayoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        initView();

        breakfastItemsRecylerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                breakfastItemsRecylerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        BreakfastItem breakfastItem = restaurantDetails.getBreakfastItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, breakfastItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, breakfastItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, breakfastItem.getText2());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, breakfastItem.getText1());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, breakfastItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME, RestaurantsFragment.class.getSimpleName());

                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        productDetailsFragment.setArguments(AppUtils.getBundleObj());

                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, productDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


        lunchItemsRecylerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                lunchItemsRecylerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        LunchItem lunchItem = restaurantDetails.getLunchItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, lunchItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, lunchItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, lunchItem.getText2());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, lunchItem.getText1());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, lunchItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME, RestaurantsFragment.class.getSimpleName());

                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        productDetailsFragment.setArguments(AppUtils.getBundleObj());

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, productDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


        dinnerItemsRecylerView.addOnItemTouchListener(new RecylerViewTouchEventListener(getActivity(),
                dinnerItemsRecylerView,
                new RecylerViewItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        DinnerItem dinnerItem = restaurantDetails.getDinnerItems().get(position);

                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_ID, dinnerItem.getId());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_NAME, dinnerItem.getName());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_PRICE, dinnerItem.getText2());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_INFO, dinnerItem.getText1());
                        AppUtils.getBundleObj().putString(AppUtils.PRODUCT_IMAGE, dinnerItem.getImageUrl());
                        AppUtils.getBundleObj().putString(AppUtils.CALLED_CLASS_NAME, RestaurantsFragment.class.getSimpleName());

                        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                        productDetailsFragment.setArguments(AppUtils.getBundleObj());


                        //FragmentManager fm = getActivity().getSupportFragmentManager();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, productDetailsFragment)
                                .commit();

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


        return _rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (AppUtils.isNetworkAvailable(getActivity())) fetchRestaurantDetails();
        else setWithoutInternetView();
    }

    private void setWithoutInternetView() {

        rootViewCoordinatorLayout.setVisibility(View.GONE);
        noInternetViewToolbar.setVisibility(View.VISIBLE);
        noInternetViewToolbar.setTitle(getArguments().getString(AppUtils.RESTAUREANT_NAME));
        mLoadingView.setVisibility(View.GONE);
        noInternetConnectionView.setVisibility(View.VISIBLE);
        noInternetViewToolbar.setBackgroundColor(
                getActivity().getResources().getColor(R.color.colorPrimary));
        ((DrawerActivity) getActivity()).setToolbarAndToggle(noInternetViewToolbar);
    }

    private void fetchRestaurantDetails() {

        Retrofit retrofit = RetrofitClient.getClient();
        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<RestaurantDetails> call = retrofitInterfaces.fetchRestaurantDetails(AppUtils.WP_PARAM_LANGUAGE,
                getArguments().getString(AppUtils.RESTAUREANT_ID));

        call.enqueue(new Callback<RestaurantDetails>() {
            @Override
            public void onResponse(Call<RestaurantDetails> call, Response<RestaurantDetails> response) {

                restaurantDetails = response.body();

                mLoadingView.setVisibility(View.GONE);
                toolbar.setBackground(null);
                toolbar.setTitle(getArguments().getString(AppUtils.RESTAUREANT_NAME));
                ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
                collapsingToolbarLayout.setTitle(null);
                collapsingToolbarLayout.setTitleEnabled(false);

                setApiDataOnView();

                setupMenuRecyclerViews();

            }

            @Override
            public void onFailure(Call<RestaurantDetails> call, Throwable t) {

                setWithoutInternetView();
            }
        });

    }

    private void initView() {

        toolbar = (Toolbar) _rootView.findViewById(R.id.toolbar);
        noInternetViewToolbar = (Toolbar) _rootView.findViewById(R.id.extra_toolbar);

        mLoadingView = _rootView.findViewById(R.id.restaurantsLoadingView);
        noInternetConnectionView = _rootView.findViewById(R.id.no_internet_layout);
        retryInternetBtn = (Button) _rootView.findViewById(R.id.retry_internet);
        retryInternetBtn.setOnClickListener(this);

        rootViewCoordinatorLayout = (CoordinatorLayout) _rootView.findViewById(R.id.main_content);
        collapsingToolbarLayout = (CollapsingToolbarLayout) _rootView.findViewById(R.id.collapsing_toolbar);

        restaurantImage = (ImageView) _rootView.findViewById(R.id.restaurant_image);

        openCloseExpandImageview = (ImageView) _rootView.findViewById(R.id.open_close_expand_imageview);
        openCloseExpandImageview.setOnClickListener(this);
        openCloseBottomIndicatorView = (LinearLayout) _rootView.findViewById(R.id.open_close_bottom_indicator_view);
        openCloseTimeLayout = (RelativeLayout) _rootView.findViewById(R.id.open_close_time_layout);

        breakfastOpeningAndClosingTimeTextView = (TextView) _rootView.findViewById(R.id.breakfast_opening_and_closing_time_textview);
        lunchOpeningAndClosingTimeTextView = (TextView) _rootView.findViewById(R.id.lunch_opening_and_closing_time_textview);
        dinnerOpeningAndClosingTimeTextView = (TextView) _rootView.findViewById(R.id.dinner_opening_and_closing_time_textview);

        restaurantDetailsInfoTextView = (ExpandableTextView) _rootView.findViewById(R.id.restaurant_info_expandable_TextView);
        expandTextView = (TextView) _rootView.findViewById(R.id.toggle_TextView);

        adultsTitleTextView = (TextView) _rootView.findViewById(R.id.adults_title_textview);
        adultsTitleTextView.setOnClickListener(this);
        childrenTitleTextView = (TextView) _rootView.findViewById(R.id.children_title_textview);
        childrenTitleTextView.setOnClickListener(this);

        breakfastTitleTextview = (TextView) _rootView.findViewById(R.id.breakfast_title_textview);
        breakfastAvailableStatusTextview = (TextView) _rootView.findViewById(R.id.breakfast_available_status_textview);
        breakfastPrebookPriceTextview = (TextView) _rootView.findViewById(R.id.breakfast_prebook_price_textview);
        //  topPreBookPriceTextView = (TextView) _rootView.findViewById(R.id.prebook_price_textview);
        breakfastOnBoardPriceTextView = (TextView) _rootView.findViewById(R.id.breakfast_onboard_textview);
        breakfastSaveAmountTextView = (TextView) _rootView.findViewById(R.id.breakfast_save_textview);
        breakfastTimeTitleTextView = (TextView) _rootView.findViewById(R.id.breakfast_time_title_textview);
        breakfastTimeTextView = (TextView) _rootView.findViewById(R.id.breakfast_time_textview);

        dinnerTitleTextview = (TextView) _rootView.findViewById(R.id.dinner_title_textview);
        dinnerAvailableStatusTextview = (TextView) _rootView.findViewById(R.id.dinner_available_tatus_textview);
        dinnerPrebookPriceTextview = (TextView) _rootView.findViewById(R.id.dinner_prebook_price_textview);
        //bottomPreBookPriceTextView = (TextView) _rootView.findViewById(R.id.bottom_prebook_price_textview);
        dinnerOnBoardPriceTextView = (TextView) _rootView.findViewById(R.id.dinner_onboard_price_textview);
        dinnerSaveAmountTextView = (TextView) _rootView.findViewById(R.id.dinner_save_amount_textview);
        dinnerTimeTitleTextView = (TextView) _rootView.findViewById(R.id.dinner_seatings_times_textview);
        dinnerTimeTextView = (TextView) _rootView.findViewById(R.id.dinner_time_textview);


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

    private void setApiDataOnView() {

        Picasso.with(getActivity())
                .load(getActivity().getResources().
                        getString(R.string.image_downloaded_base_url) +
                        restaurantDetails.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(restaurantImage);

        breakfastOpeningAndClosingTimeTextView.setText(restaurantDetails.getBreakfastTime());
        lunchOpeningAndClosingTimeTextView.setText(restaurantDetails.getLunchTime());
        dinnerOpeningAndClosingTimeTextView.setText(restaurantDetails.getDinnerTime());

        restaurantDetailsInfoTextView.setText(restaurantDetails.getOpenCloseTimeText());
        handleExpandableTextViewListener();

        // set animation duration via code, but preferable in your layout files
        // by using the animation_duration attribute
        restaurantDetailsInfoTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantDetailsInfoTextView.isExpanded()) {
                    restaurantDetailsInfoTextView.collapse();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_expanded_time));

                } else {
                    restaurantDetailsInfoTextView.expand();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_collapse_time));

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

    private void setupMenuRecyclerViews() {

        mBreakfastRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);
        mLunchRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);
        mDinnerRecyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);

        breakFastItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getBreakfastItems(),
                BreakfastItem.class.getSimpleName()
        );

        breakfastItemsRecylerView.setLayoutManager(mBreakfastRecyclerViewLayoutManager);
        breakfastItemsRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        breakfastItemsRecylerView.setItemAnimator(new DefaultItemAnimator());
        breakfastItemsRecylerView.setAdapter(breakFastItemsRecylerViewAdapter);


        lunchItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getLunchItems(),
                LunchItem.class.getSimpleName());

        lunchItemsRecylerView.setLayoutManager(mLunchRecyclerViewLayoutManager);
        lunchItemsRecylerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3), true));
        lunchItemsRecylerView.setItemAnimator(new DefaultItemAnimator());
        lunchItemsRecylerView.setAdapter(lunchItemsRecylerViewAdapter);


        dinnerItemsRecylerViewAdapter = new DutyFreeAdapter(getActivity(),
                restaurantDetails.getDinnerItems(),
                DinnerItem.class.getSimpleName());

        dinnerItemsRecylerView.setLayoutManager(mDinnerRecyclerViewLayoutManager);
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
                //Toast.makeText(getActivity(), "retry", Toast.LENGTH_LONG).show();
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

    @Override
    public void handleExpandableTextViewListener() {


        ViewTreeObserver vto = restaurantDetailsInfoTextView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    restaurantDetailsInfoTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    restaurantDetailsInfoTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                AppUtils.setVisibilityOfExpandTextview(restaurantDetailsInfoTextView, expandTextView);

            }

        });

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        restaurantDetailsInfoTextView.setAnimationDuration(300L);
        expandTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantDetailsInfoTextView.isExpanded()) {
                    restaurantDetailsInfoTextView.collapse();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_expanded_time));
                    Log.i("textline", "onexpand " + restaurantDetailsInfoTextView.getLineCount() + "");
                } else {
                    restaurantDetailsInfoTextView.expand();
                    expandTextView.setText(getActivity().getResources().getString(R.string.textview_text_at_collapse_time));
                    Log.i("textline", "oncollapse " + restaurantDetailsInfoTextView.getLineCount() + "");
                }
            }
        });

    }


}
