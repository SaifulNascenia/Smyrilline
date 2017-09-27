package com.mcp.smyrilline.util;

import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.GridNavItem;
import java.util.ArrayList;

/**
 * Created by raqib on 5/4/17.
 */

public class StaticData {



    public static ArrayList<GridNavItem> gridNavList = new ArrayList<>();
    public static void initGridNavList(){
        gridNavList.clear();
        gridNavList.add(new GridNavItem("Home", R.drawable.ic_grid_home));
    }
}
