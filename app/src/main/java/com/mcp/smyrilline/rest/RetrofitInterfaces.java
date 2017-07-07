package com.mcp.smyrilline.rest;

import com.mcp.smyrilline.model.destination.DestinationDetailsInfo;
import com.mcp.smyrilline.model.destination.ListOfDestinations;
import com.mcp.smyrilline.model.dutyfree.DutyFree;
import com.mcp.smyrilline.model.restaurant.ListOfRestaurants;
import com.mcp.smyrilline.model.restaurant.RestaurantDetails;
import com.mcp.smyrilline.model.shipinfo.Info;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by saiful on 5/24/17.
 */

public interface RetrofitInterfaces {


    /*@GET("pages?filter[post_parent]=7&filter[orderby]=menu_order&filter[order]=asc&per_page=100")
    Call<List<ParentModel>> fetchAllRestaurantsAndBarsInfo();
    */

    @GET("Restaurants/{language}")
    Call<List<ListOfRestaurants>> fetchAllRestaurantsAndBarsInfo(@Path("language") String language);

    @GET("Restaurants/{language}/{id}")
    Call<RestaurantDetails> fetchRestaurantDetails(@Path("language") String language,
                                                   @Path("id") String id);

    @GET("TaxFreeShop/{language}")
    Call<DutyFree> fetchDutyFreeProductsList(@Path("language") String language);

    @GET("Destinations/{language}")
    Call<List<ListOfDestinations>> fetchAllDestinationsInfo(@Path("language") String language);

    @GET("Destinations/{language}/{id}")
    Call<DestinationDetailsInfo> fetchDestinationDetialsInfo(@Path("language") String language,
                                                             @Path("id") String id);

    @GET("ShipInfo/{language}")
    Call<Info> fetchShipInfo(@Path("language") String language);


}
