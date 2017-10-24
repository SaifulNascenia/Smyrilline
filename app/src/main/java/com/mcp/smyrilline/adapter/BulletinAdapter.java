package com.mcp.smyrilline.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.messaging.Bulletin;
import com.mcp.smyrilline.util.AppUtils;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by raqib on 12/29/15.
 */
public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.ViewHolder> {

    private ArrayList<Bulletin> mBulletinList;
    private TextView tvNothingText;

    public BulletinAdapter(ArrayList<Bulletin> mBulletinList, TextView tvNothingText) {
        this.mBulletinList = mBulletinList;
        this.tvNothingText = tvNothingText;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bulletin, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Bulletin bulletin = mBulletinList.get(position);

        if (bulletin.isSeen())
            holder.imgBulletinNewIndicator.setVisibility(View.INVISIBLE);
        else
            holder.imgBulletinNewIndicator.setVisibility(View.VISIBLE);

        holder.tvBulletinTitle.setText(bulletin.getTitle());
        /**
         * Use JSoup to parse only the text from html
         * eg. if there is image at the begining of content
         * Using webview will show truncated image in list item ~ odd!
         */
        Document doc = Jsoup.parse(bulletin.getContent());
        String text = doc.body().text();
        holder.tvBulletinContent.setText(text);

        // We're getting eg. "date": "Sep 02, 2016 04:15 PM"
        String date = bulletin.getDate();
        String convertedDate = AppUtils
            .convertDateFormat(date, AppUtils.DATE_FORMAT_BULLETIN_DETAIL,
                AppUtils.DATE_FORMAT_BULLETIN);
        holder.tvBulletinDate.setText(convertedDate);
    }

    @Override
    public int getItemCount() {
        return mBulletinList.size();
    }

    /**
     * Remove bulletin from list & save in shared pref
     *
     * @param position
     */
    public void remove(int position) {
        mBulletinList.remove(position);
        notifyItemRemoved(position);
        AppUtils.saveListInSharedPref(mBulletinList, AppUtils.PREF_BULLETIN_LIST);

        if (mBulletinList.isEmpty())
            tvNothingText.setVisibility(View.VISIBLE);
    }

    /**
     * Add new bulletin to list and save in shared pref
     *
     * @param position
     * @param newBulletin
     */
    public void add(int position, Bulletin newBulletin) {
        tvNothingText.setVisibility(View.GONE);
        mBulletinList.add(position, newBulletin);
        notifyItemInserted(position);
    }

    public void addAll(int position, ArrayList<Bulletin> newBulletinList) {
        tvNothingText.setVisibility(View.GONE);
        mBulletinList.addAll(position, newBulletinList);
        notifyItemRangeInserted(0, newBulletinList.size());
    }

    public void setBulletinList(ArrayList<Bulletin> bulletinList) {
        this.mBulletinList = bulletinList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBulletinNewIndicator;
        TextView tvBulletinTitle;
        TextView tvBulletinContent;
        TextView tvBulletinDate;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBulletinNewIndicator = (ImageView) itemView.findViewById(R.id.imgBulletinNewIndicator);
            tvBulletinTitle = (TextView) itemView.findViewById(R.id.tvBulletinTitle);
            tvBulletinContent = (TextView) itemView.findViewById(R.id.tvBulletinContent);
            tvBulletinDate = (TextView) itemView.findViewById(R.id.tvBulletinDate);
        }
    }
}
