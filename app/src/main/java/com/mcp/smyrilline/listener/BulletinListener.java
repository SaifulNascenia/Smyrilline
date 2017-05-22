package com.mcp.smyrilline.listener;

import com.mcp.smyrilline.model.Bulletin;

import java.util.ArrayList;

/**
 * Created by raqib on 12/31/15.
 */
public interface BulletinListener {
    void onBulletinReceived(Bulletin newBulletin);
    void onBulletinListReceived(ArrayList<Bulletin> newBulletinList);
}
