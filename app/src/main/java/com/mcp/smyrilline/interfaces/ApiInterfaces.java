package com.mcp.smyrilline.interfaces;

import com.mcp.smyrilline.model.DestinationAndShipInfoModel.Info;
import com.mcp.smyrilline.model.dutyfreemodels.DutyFree;
import com.mcp.smyrilline.model.parentmodel.ParentModel;
import com.mcp.smyrilline.model.restaurentsmodel.ListOfRestaurent;
import com.mcp.smyrilline.model.restaurentsmodel.RestaurentDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by saiful on 5/24/17.
 */

public interface ApiInterfaces {


    /*@GET("pages?filter[post_parent]=7&filter[orderby]=menu_order&filter[order]=asc&per_page=100")
    Call<List<ParentModel>> fetchAllRestaurentsAndBarsInfo();
    */

    @GET("Restaurants/{language}")
    Call<List<ListOfRestaurent>> fetchAllRestaurentsAndBarsInfo(@Path("language") String language);

    @GET("Restaurants/{language}/{id}")
    Call<RestaurentDetails> fetchRestaurentDetails(@Path("language") String language,
                                                   @Path("id") String id);

    @GET("TaxFreeShop/{language}")
    Call<DutyFree> fetchDutyFreeProductsList(@Path("language") String language);

    @GET("Destinations/{language}")
    Call<List<ListOfRestaurent>> fetchAllDestinationsInfo(@Path("language") String language);

    @GET("Destinations/{language}/{id}")
    Call<Info> fetchDestinationDetialsInfo(@Path("language") String language,
                                           @Path("id") String id);

    @GET("ShipInfo/{language}")
    Call<Info> fetchShipInfo(@Path("language") String language);


}
