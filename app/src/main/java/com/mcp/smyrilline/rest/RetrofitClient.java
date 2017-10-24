package com.mcp.smyrilline.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.util.McpApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saiful on 5/24/17.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                .baseUrl(McpApplication.instance().context().getString(R.string.api_base_url))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
