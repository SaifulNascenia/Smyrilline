package com.mcp.smyrilline.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saiful on 5/24/17.
 */

public class RetrofitClient {

    //public static final String BASE_URL = "http://stage-smy-wp.mcp.com/wordpress/wp-json/wp/v2/";
    public static final String BASE_URL = "http://stage-smy-wp.mcp.com:82/api/SmyrilLine/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}