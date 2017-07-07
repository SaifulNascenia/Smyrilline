package com.mcp.smyrilline.listener;

import android.view.View;

/**
 * Created by saiful on 6/5/17.
 */

public interface RecylerViewItemClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);

}
