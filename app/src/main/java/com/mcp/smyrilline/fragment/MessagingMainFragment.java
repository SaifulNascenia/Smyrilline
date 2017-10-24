package com.mcp.smyrilline.fragment;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.adapter.ChatViewPagerAdapter;
import com.mcp.smyrilline.model.messaging.ClientStatus;
import com.mcp.smyrilline.model.messaging.SearchQuery;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.McpApplication;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by raqib on 9/16/17.
 */

public class MessagingMainFragment extends Fragment {

    private static final int SEARCH_TEXT_MAX_LENGTH = 25;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
        R.drawable.ic_tab_users,
        R.drawable.ic_tab_groups,
        R.drawable.ic_tab_admin,
        R.drawable.ic_tab_inbox
    };
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messaging_main, container, false);

        // messaging_toolbar
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        ((DrawerActivity) getActivity()).setToolbarAndToggle(toolbar);
        ((DrawerActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(R.string.messaging);

        // viewpager
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        // tab layout
        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons(tabLayout);

        // show profile page if not saved previously
        boolean isProfileVerified = McpApplication.instance().sharedPref()
            .getBoolean(AppUtils.PREF_PROFILE_VERIFIED, false);
        if (!isProfileVerified) {
            AppUtils.showAlertForVerfiyProfile(getContext());

        }
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        ChatViewPagerAdapter viewPagerAdapter = new ChatViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new UserListFragment(), getString(R.string.users));
        viewPagerAdapter.addFragment(new GroupListFragment(), getString(R.string.groups));
        viewPagerAdapter.addFragment(new AdminListFragment(), getString(R.string.admin));
        viewPagerAdapter.addFragment(new InboxFragment(), getString(R.string.inbox));
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
                AppUtils.hideKeyboard(getActivity());
            }
        });
    }

    public void setupTabIcons(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("MessagingMainFragment", "inflating menu");
        inflater.inflate(R.menu.menu_chat_list, menu);
        initSearchViewDesignAndFunction(menu.findItem(R.id.action_search));
    }

    private void initSearchViewDesignAndFunction(MenuItem searchItem) {

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();

            // change hint text (gray italic)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                searchView.setQueryHint(Html.fromHtml("<font color=#ffffff>"
                        + "<i>" + getString(R.string.search_user_hint) + "</i>" + "</font>",
                    Html.FROM_HTML_MODE_LEGACY));
            else
                searchView.setQueryHint(Html.fromHtml("<font color=#cccccc>"
                    + "<i>" + getString(R.string.search_user_hint) + "</i>" + "</font>"));

            // change search text color
            EditText searchText = ((EditText) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text));
            searchText.setTextColor(Color.BLACK);
            searchText.setFilters(
                new InputFilter[]{new InputFilter.LengthFilter(SEARCH_TEXT_MAX_LENGTH)});

            // change search clear icon color
            ImageView searchClose = (ImageView) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            searchClose.setImageResource(R.drawable.ic_clear_black_24dp);

            // make search background circular white
            View searchPlate = searchView
                .findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlate.setBackgroundResource(R.drawable.bg_white_rounded);

            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    EventBus.getDefault().post(new SearchQuery(query));
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // not implemented here
                return false;
            case R.id.action_my_profile:
                // show the profile dialog
                AppUtils.showProfileDialog(getContext());
                return true;
            case R.id.status_visible_to_booking:
                showVisibilityConfirmDialoag(getActivity(),
                    getString(R.string.you_will_be_visible_to_your_booking_partners_only),
                    AppUtils.VISIBLE_TO_BOOKING);
                return true;
            case R.id.status_public:
                showVisibilityConfirmDialoag(getActivity(),
                    getString(R.string.you_will_be_visible_to_everyone),
                    AppUtils.VISIBLE_TO_ALL);
                return true;
            case R.id.status_invisible:
                showVisibilityConfirmDialoag(getActivity(),
                    getString(R.string.you_will_be_not_be_visible),
                    AppUtils.VISIBLE_TO_NONE);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showVisibilityConfirmDialoag(final Context context, String message,
        final int profileVisibility) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(AppUtils.getSpannedText(message))
            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // save profile visibility
                    McpApplication.instance().sharedPref().edit()
                        .putInt(AppUtils.PREF_MY_VISIBILITY, profileVisibility).apply();

                    // post new status to call register chat
                    EventBus.getDefault().post(new ClientStatus(AppUtils.USER_ONLINE));

                    // close alert
                    dialog.dismiss();

                    // show toast
                    Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT)
                        .show();

                }
            })
            .setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
