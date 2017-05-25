package com.mcp.smyrilline.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.Meal;
import com.mcp.smyrilline.model.MealDate;
import com.mcp.smyrilline.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by raqib on 4/27/16.
 */
public class MealExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<MealDate> mGroups;
    private AlertDialog mWarningDialog;
    private TextView tvNothingText;

    public MealExpandableListAdapter(Context context, ArrayList<MealDate> dateList, TextView tvNothingText) {
        mContext = context;
        mGroups = dateList;
        this.tvNothingText = tvNothingText;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).getMealList().size();
    }

    @Override
    public MealDate getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Meal getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getMealList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        MealDate group = getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_meal_date, null);
        }

        TextView tvMealListParentDate = (TextView) convertView.findViewById(R.id.tvMealListParentDate);
        tvMealListParentDate.setText(group.getDateString());

        final ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);

        eLV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId) {
                eLV.expandGroup(itemPosition);
                return true;
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Meal meal = getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater childInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = childInflater.inflate(R.layout.list_item_meal_child, null);
        }

        LinearLayout dateDivider = (LinearLayout) convertView.findViewById(R.id.mealDateDivider);
        if (isLastChild)
            dateDivider.setVisibility(View.VISIBLE);
        else
            dateDivider.setVisibility(View.GONE);

        TextView tvMealDescription = (TextView) convertView.findViewById(R.id.tvMealDescription);
        tvMealDescription.setText(meal.getDescription());

        final Button btnMealUse = (Button) convertView.findViewById(R.id.btnMealUse);

        // Check used or expired [DISABLE EXPIRED FEATURE FOR NOW]
        if (meal.isUsed()/* || System.currentTimeMillis() > meal.getDate().getTime()*/) {
            btnMealUse.setEnabled(false);
            btnMealUse.setClickable(false);
            btnMealUse.setTextColor(ContextCompat.getColor(mContext, R.color.bkg_deep_grey));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_used, null));
            else
                btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_used));

//            if (meal.isUsed())
                btnMealUse.setText(mContext.getResources().getString(R.string.used));
//            else
//                btnMealUse.setText(mContext.getResources().getString(R.string.expired));
        } else {
            btnMealUse.setEnabled(true);
            btnMealUse.setClickable(true);
            btnMealUse.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_use, null));
            else
                btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_use));

            btnMealUse.setText(mContext.getResources().getString(R.string.use));
        }

        btnMealUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long dateMillis = meal.getDate().getTime();

                // Check again in case expired when viewing list
                if (System.currentTimeMillis() > dateMillis) {
                    btnMealUse.setText("Expired");
                    btnMealUse.setClickable(false);
                    btnMealUse.setTextColor(ContextCompat.getColor(mContext, R.color.bkg_deep_grey));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_used, null));
                    else
                        btnMealUse.setBackground(mContext.getResources().getDrawable(R.drawable.bkg_meal_used));
                } else {
                    AlertDialog.Builder warningDialogBuilder = new AlertDialog.Builder(mContext);
                    warningDialogBuilder
                            .setTitle(mContext.getResources().getString(R.string.are_you_sure))
                            .setMessage(Html.fromHtml("<font color = #00a8ff>" + mContext.getResources().getString(R.string.use_meal_warning) + "</font>"))
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
//                                    btnMealUse.setEnabled(false);
//                                    btnMealUse.setEnabled(false);
                                    // use meal and save
                                    meal.setUsed(true);
                                    AppUtils.saveListInSharedPref(mGroups, AppUtils.PREF_MEAL_LIST);
                                    notifyDataSetChanged();
//                                    useCouponAndExit(position, btnMealUse);
                                }
                            })
                            .setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    mWarningDialog = warningDialogBuilder.create();
                    mWarningDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                            Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                            positiveButton.setTextColor(ContextCompat.getColor(mContext, R.color.bkg_ok_button));
                            negativeButton.setTextColor(ContextCompat.getColor(mContext, R.color.bkg_cancel_button));

                            positiveButton.invalidate();
                            negativeButton.invalidate();
                        }
                    });
                    mWarningDialog.setCancelable(true);
                    mWarningDialog.setCanceledOnTouchOutside(false);
                    mWarningDialog.show();
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * checks empty list
     * Updates the textview if list is empty
     * We update the textview here as adapter is refreshed from multiple places
     */
    public void checkEmpty() {
        if (mGroups.isEmpty()) {
            tvNothingText.setVisibility(View.VISIBLE);
            tvNothingText.setText(mContext.getResources().getText(R.string.no_meals));
        } else
            tvNothingText.setVisibility(View.GONE);
    }

    /**
     * Calls notifyDataSetChanged and checks empty list
     * Updates the textview if list is empty
     */
    public void refreshSearchedList() {

        if (mGroups.size() == 0) {
            tvNothingText.setVisibility(View.VISIBLE);
            tvNothingText.setText(mContext.getResources().getText(R.string.no_results));
        } else
            tvNothingText.setVisibility(View.GONE);

        notifyDataSetChanged();
    }

//    /**
//     * Search filter method
//     */
//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.US).trim();
//        mGroups.clear();
//        if (TextUtils.isEmpty(charText)) {
//            mGroups.addAll(mTempList);
//        } else {
//            for (Coupon coupon : mTempList) {
//                if (coupon.name.toLowerCase(Locale.US)
//                        .contains(charText)) {
//                    mGroups.add(coupon);
//                }
//            }
//        }
//        refreshSearchedList();
//    }
}
