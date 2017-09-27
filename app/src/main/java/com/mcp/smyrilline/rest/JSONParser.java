package com.mcp.smyrilline.rest;

import android.content.Context;
import android.util.Log;
import com.mcp.smyrilline.util.AppUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by raqib on 8/19/15.
 */
public class JSONParser {

    private static final String TAG = "JSON Parser: ";
    private Context mContext;
    private InputStream is = null;
    private URL mURL;
    private JSONArray jArray;
    private String jsonStr;

    public JSONParser() {
    }

    // constructor
    public JSONParser(Context context) {
        mContext = context;
    }

    public JSONArray getJSONArrayFromUrl(String url) {

        // Making HTTP request
        try {
            mURL = new URL(url);
            // defaultHttpClient

            HttpURLConnection urlConnection = (HttpURLConnection) mURL.openConnection();

            is = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }

            is.close();
            jsonStr = sb.toString();

            // try parse the string to a JSON array
            // Log.w("JSONParser", "Json string is -> " + jsonStr);
            jArray = new JSONArray(jsonStr);

        } catch (MalformedURLException e) {
            Log.e(AppUtils.TAG, TAG + e.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(AppUtils.TAG, TAG + "Error parsing data " + e.toString());
        } catch (Exception e) {
            Log.e(AppUtils.TAG, TAG + "Error converting result " + e.toString());
        }
        // return JSON array
        return jArray;
    }
}
