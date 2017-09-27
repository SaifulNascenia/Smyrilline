package com.mcp.smyrilline.rest;

import com.mcp.smyrilline.model.destination.DestinationDetails;
import com.mcp.smyrilline.model.destination.Destinations;
import com.mcp.smyrilline.model.dutyfree.DutyFree;
import com.mcp.smyrilline.model.restaurant.ListOfRestaurants;
import com.mcp.smyrilline.model.restaurant.RestaurantDetails;
import com.mcp.smyrilline.model.shipinfo.ShipInfo;
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

    @GET("restaurants/{language}")
    Call<List<ListOfRestaurants>> fetchAllRestaurantsAndBarsInfo(@Path("language") String language);

    @GET("restaurants/{language}/{id}")
    Call<RestaurantDetails> fetchRestaurantDetails(@Path("language") String language,
                                                   @Path("id") String id);

    @GET("taxfreeshop/{language}")
    Call<DutyFree> fetchDutyFreeProductsList(@Path("language") String language);

    @GET("destinations/{language}")
    Call<List<Destinations>> fetchAllDestinationsInfo(@Path("language") String language);

    @GET("destinations/{language}/{id}")
    Call<DestinationDetails> fetchDestinationDetialsInfo(@Path("language") String language,
            @Path("id") String id);

    @GET("shipinfo/{language}")
    Call<ShipInfo> fetchShipInfo(@Path("language") String language);


}
