package com.mcp.smyrilline.interfaces;

import com.mcp.smyrilline.model.parentmodel.ParentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by saiful on 5/24/17.
 */

public interface ApiInterfaces {

    /*
        @GET()
        Call<ParentModel> getTotalData(@Query("urlQueryData") String urlQueryData);

    */
    @GET("pages?filter[post_parent]=7&filter[orderby]=menu_order&filter[order]=asc&per_page=100")
    Call<ParentModel> getTotalData();

}
