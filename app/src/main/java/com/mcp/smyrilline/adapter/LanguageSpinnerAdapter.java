package com.mcp.smyrilline.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mcp.smyrilline.R;

/**
 * Adapter for Language option spinner.
 */
public class LanguageSpinnerAdapter extends ArrayAdapter<Integer> {

    LayoutInflater inflater;
    private Context context;
    private int textViewResourceId;
    private Integer[] flagIcons;

    public LanguageSpinnerAdapter(Context context, int textViewResourceId, Integer[] flagIcons) {
        super(context, textViewResourceId, flagIcons);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.flagIcons = flagIcons;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(textViewResourceId, parent, false);

        // Remove right padding for spinner below LL
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            rowView.setPadding(rowView.getPaddingLeft(), rowView.getPaddingTop(), 0, rowView.getPaddingBottom());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgFlag);
        imageView.setImageResource(flagIcons[position]);
        return rowView;
    }
}
