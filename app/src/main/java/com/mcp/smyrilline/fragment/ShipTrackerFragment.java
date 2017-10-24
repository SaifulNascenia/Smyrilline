package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonArrayRequest;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.rest.VolleySingleton;
import com.mcp.smyrilline.util.AppUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by raqib on 5/11/17.
 */

public class ShipTrackerFragment extends Fragment{

    // Noronna ship ID
    public static final String SHIP_ID = "22";
    // URLs
    public static final String DOMAIN_CONSOLE_MCP = "http://console.mcp.com";
    public static final String URL_TRACKING =
            DOMAIN_CONSOLE_MCP + "/mtracking.php?ship=" + SHIP_ID;        // URL to get ship info
    public static final String URL_TRAJECTORY = DOMAIN_CONSOLE_MCP + "/mtrajectory.php?ship="
            + SHIP_ID;    // URL to get ship trajectory
    private static final int SHIPTRACKER_TIMEOUT_MS = 7000;
    //JSON Node Names
    private final String SHIP_SPEED = "Speed";
    private final String SHIP_LAT = "Lat";
    private final String SHIP_LONG = "Long";
    private final String SHIP_PREV = "Last port";
    private final String SHIP_NEXT = "Next port";

    // UI elements
    private MapView mMapView;
    private ImageView fab;
    private RelativeLayout rlShipTrackerBottomRow;
    private RelativeLayout rlShipTrackerBar;
    private View mLoadingView;
    private Context mContext;
    private ArrayList<Marker> mMarkers;
    private Marker mShipMarker;
    private LatLng mShipLocation;
    private int bottomRowHeight;

    // Ship information fields
    private TextView tvPrev;
    private TextView tvNext;
    private TextView tvSpeed;
    private MenuItem refreshMenuItem;

    private RetryPolicy shiptrackerRetryPolicy = new DefaultRetryPolicy(
            SHIPTRACKER_TIMEOUT_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu(true);//Adding Option Menu
        View rootView = inflater.inflate(R.layout.fragment_ship_tracker, container, false);
        mContext = getActivity();

        // set the messaging_toolbar as actionbar
        ((DrawerActivity) getActivity())
                .setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(R.string.ship_tracker);  // Set title
        getActivity().invalidateOptionsMenu();          // Refresh messaging_toolbar options

        // Init UI
        mLoadingView = rootView.findViewById(R.id.shipTrackerLoadingView);
        mMapView = (MapView) rootView.findViewById(R.id.mapview);
        tvPrev = (TextView) rootView.findViewById(R.id.tvPrevPort);
        tvNext = (TextView) rootView.findViewById(R.id.tvNextPort);
        tvSpeed = (TextView) rootView.findViewById(R.id.tvSpeed);
        rlShipTrackerBar = (RelativeLayout) rootView.findViewById(R.id.rlShipTrackerBar);
        rlShipTrackerBottomRow = (RelativeLayout) rootView
                .findViewById(R.id.rlShipTrackerBottomRow);
        fab = (ImageView) rootView.findViewById(R.id.fab);

        /**
         * If we set initially to GONE in xml, then on first instance the layout will pop-off
         * http://stackoverflow.com/questions/32336222
         * need to first make it gone programmatically.
         *
         * Since this is relative layout, it will not get the height onCreate
         * so added listener
         */
        rlShipTrackerBottomRow.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        bottomRowHeight = rlShipTrackerBottomRow.getHeight();
                        if (Build.VERSION.SDK_INT < 16)
                            rlShipTrackerBottomRow.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        else
                            rlShipTrackerBottomRow.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);

                        rlShipTrackerBottomRow.setVisibility(View.GONE);
                    }
                }
        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rlShipTrackerBottomRow.getVisibility() == View.GONE) {
                    // slide up animation for the information bar
                    TranslateAnimation slideUpBar = new TranslateAnimation(
                            0,
                            0,
                            bottomRowHeight,
                            0);

                    slideUpBar.setDuration(500);

                    // slide up animation for the plus button
                    TranslateAnimation slideUpFab = new TranslateAnimation(
                            0,
                            0,
                            bottomRowHeight,
                            0);
                    slideUpFab.setDuration(500);

                    rlShipTrackerBar.startAnimation(slideUpBar);
                    fab.startAnimation(slideUpFab);

                    // change the plus to minus with some delay
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.setImageResource(R.drawable.img_bkg_minus);
                        }
                    }, 400);

                    rlShipTrackerBottomRow.setVisibility(View.VISIBLE);

                } else {

                    // slide down animation for the information bar
                    TranslateAnimation slideDownBar = new TranslateAnimation(
                            0,
                            0,
                            0,
                            bottomRowHeight);
                    slideDownBar.setDuration(500);
                    slideDownBar.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rlShipTrackerBar.clearAnimation();
                            rlShipTrackerBottomRow.setVisibility(View.GONE);
                        }
                    });

                    // slide down animation for the minus button
                    TranslateAnimation slideDownFab = new TranslateAnimation(
                            0,
                            0,
                            0,
                            bottomRowHeight);
                    slideDownFab.setDuration(500);
                    slideDownFab.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fab.clearAnimation();
                        }
                    });

                    rlShipTrackerBar.startAnimation(slideDownBar);
                    fab.startAnimation(slideDownFab);

                    // change minus to plus with some delay
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.setImageResource(R.drawable.img_bkg_plus);
                        }
                    }, 400);
                }
            }
        });

        // initialize map
        initMap();

        // load tracker and data
        loadShipTracker();

        return rootView;
    }

    /**
     * Check and load tracker, and ship information
     */
    private void loadShipTracker() {
        if (AppUtils.isNetworkAvailable(getActivity())) {
            // disable refresh button
            if (refreshMenuItem != null)
                refreshMenuItem.setEnabled(false);

            // show circular progress
            mLoadingView.setVisibility(View.VISIBLE);

            // send request for tracking data
            sendTrackingRequest();
        } else {
            mLoadingView.setVisibility(View.GONE);
            AppUtils.showAlertDialog(mContext, getString(R.string.alert_no_wifi));
        }
    }

    /**
     * Method that gets ship location from Tracking api
     */
    private void sendTrackingRequest() {
        JsonArrayRequest jsonTrackingArrayRequest = new JsonArrayRequest(URL_TRACKING,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        if (jsonArray == null)
                            return;

                        // parsing json array response

                        try {
                            /// Getting JSON Array
                            JSONObject jObj = (JSONObject) jsonArray.get(0);

                            String lat = jObj.getString(SHIP_LAT);
                            String lon = jObj.getString(SHIP_LONG);

                            // To pin marker, and connect with trajectory line
                            mShipLocation = new LatLng(Double.parseDouble(lat),
                                    Double.parseDouble(lon));

                            // Set map center to ship location
                            mMapView.setCenter(mShipLocation);

                            // Check keys, and set to textviews
                            String text = AppUtils.getStringFromJsonObject(jObj, SHIP_PREV);
                            tvPrev.setText(text != null ? text.trim() : "-");
                            text = AppUtils.getStringFromJsonObject(jObj, SHIP_NEXT);
                            tvNext.setText(text != null ? text.trim() : "-");
                            text = AppUtils.getStringFromJsonObject(jObj, SHIP_SPEED);
                            tvSpeed.setText(text != null ? text.trim() : "-");

                            // Create another request to load the ship trajectory
                            sendTrajectoryRequest();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(AppUtils.TAG,
                        "ShipTrackerFragment: sendTrackingRequest()\n" + volleyError.getMessage());

                // hide progress view
                mLoadingView.setVisibility(View.GONE);

                // enable refresh
                if (refreshMenuItem != null)
                    refreshMenuItem.setEnabled(true);

                // show server error dialog
                if (volleyError.getClass().equals(TimeoutError.class))
                    AppUtils.showAlertDialog(mContext, getString(R.string.alert_server_timeout));
                else
                    AppUtils.showAlertDialog(mContext, getString(R.string.alert_server_down));

                return;
            }
        });

        // set retry policy
        jsonTrackingArrayRequest.setRetryPolicy(shiptrackerRetryPolicy);

        // Adding request to request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonTrackingArrayRequest);
    }

    /**
     * Gets last 24 hours lat, long from Trajectory api
     * Draws
     */
    private void sendTrajectoryRequest() {
        JsonArrayRequest jsonTrajectoryArrayRequest = new JsonArrayRequest(URL_TRAJECTORY,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        ArrayList<LatLng> points = new ArrayList<>();

                        if (jsonArray == null)
                            return;

                        JSONObject jObj = null;
                        double latitude = 0, longitude = 0;
                        try {
                            // Adding  LatLng points from json array to line
                            PathOverlay line = new PathOverlay(
                                    ContextCompat.getColor(getActivity(), R.color.ship_trajectory),
                                    6);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jObj = jsonArray.getJSONObject(i);
                                latitude = Double.parseDouble(jObj.getString("lat"));
                                longitude = Double.parseDouble(jObj.getString("lon"));
                                line.addPoint(new LatLng(latitude, longitude));
                            }

                            // To connect gap between last trajectory point and ship location
                            line.addPoint(mShipLocation);
                            mMapView.getOverlays().add(line);

                            // location of last point from trajectory
                            Location loc1 = new Location("");
                            loc1.setLatitude(latitude);
                            loc1.setLongitude(longitude);

                            // location of ship
                            Location loc2 = new Location("");
                            loc2.setLatitude(mShipLocation.getLatitude());
                            loc2.setLongitude(mShipLocation.getLongitude());

                            // Calculating angle between the last trajectory point and ship location
                            // to rotate the marker by that angle
                            double angle = 0.0;
                            angle = loc1.bearingTo(loc2);

                            if (loc1.getLongitude() == loc2.getLongitude()) {
                                if (loc1.getLatitude() > loc2.getLatitude())
                                    angle = 180f;
                                else
                                    angle = 0.0f;
                            }

                            // Clear existing marker before draw new one
                            clearMarkers();
                            mShipMarker = new Marker(mMapView, null, null, mShipLocation);

                            // Get rotated image
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeResource(
                                    getActivity().getResources(),
                                    R.drawable.ic_direction_arrow,
                                    bmOptions);
                            final Drawable rotatedDrawable = getRotateDrawable(bitmap, angle);

                            mShipMarker.setMarker(rotatedDrawable,
                                    true);   // set the rotated pointer icon as marker
                            mMapView.addMarker(mShipMarker);                // add marker to map
                            mMarkers.add(
                                    mShipMarker);                      // keep the marker in an array

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            /* ContextCompat throws NullPointerException
                             * when we switch to a different screen before this thread is finished
                             * So we add exception check
                             */
                            e.printStackTrace();
                        } finally {
                            /**
                             * Because map tiles are loading slowly
                             * so we add some delay before dismissing the progress dialog
                             */
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // hide progress view
                                    mLoadingView.setVisibility(View.GONE);

                                    // enable refresh
                                    if (refreshMenuItem != null)
                                        refreshMenuItem.setEnabled(true);
                                }
                            }, 3000);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(AppUtils.TAG, "ShipTrackerFragment: sendTrajectoryRequest()\n" + volleyError
                        .getMessage());

                // hide progress view
                mLoadingView.setVisibility(View.GONE);

                // enable refresh
                if (refreshMenuItem != null)
                    refreshMenuItem.setEnabled(true);

                // show server error dialog
                if (volleyError.getClass().equals(TimeoutError.class))
                    AppUtils.showAlertDialog(mContext, getString(R.string.alert_server_timeout));
                else
                    AppUtils.showAlertDialog(mContext, getString(R.string.alert_server_down));

                return;
            }
        });

        // set retry policy
        jsonTrajectoryArrayRequest.setRetryPolicy(shiptrackerRetryPolicy);

        // Adding request to request queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonTrajectoryArrayRequest);
    }

    /**
     * Set map properties, load custom tiles hosted on server
     * Setting min, max, & initial zoom level
     */
    private void initMap() {
        // OpenStreetMap tiles on wordpress server
        WebSourceTileLayer ws = new WebSourceTileLayer("openstreetmap",
                "http://smy-wp.mcp.com/osm/{z}/{x}/{y}.png");
        ws.setName("OpenStreetMap")
                .setAttribution("© OpenStreetMap Contributors")
                .setMinimumZoomLevel(1)
                .setMaximumZoomLevel(17);

        mMapView.setTileSource(ws);
        mMapView.setMinZoomLevel(mMapView.getTileProvider().getMinimumZoomLevel());
        mMapView.setMaxZoomLevel(mMapView.getTileProvider().getMaximumZoomLevel());
        mMarkers = new ArrayList<>();
    }

    /**
     * Clearing any existing markers from the map
     */
    private void clearMarkers() {
        if (mMarkers.size() != 0)
            for (Marker m : mMarkers) {
                mMapView.removeMarker(m);
                m = null;
            }
    }

    /**
     * Rotate a drawable image to the given angle
     *
     * @return rotated image
     */
    private Drawable getRotateDrawable(final Bitmap bitmap, final double angle) {
        final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate((float) angle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        return drawable;
    }

    //Adding Refresh button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ship_tracker, menu);
        refreshMenuItem = menu.findItem(R.id.ship_tracker_refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case R.id.ship_tracker_refresh:

                if (AppUtils.isNetworkAvailable(getActivity())) {
                    // disable refresh button
                    if (refreshMenuItem != null)
                        refreshMenuItem.setEnabled(false);

                    // show circular progress
                    mLoadingView.setVisibility(View.VISIBLE);

                    sendTrackingRequest();
                } else {
                    mLoadingView.setVisibility(View.GONE);
                    AppUtils.showAlertDialog(mContext, getString(R.string.alert_no_wifi));
                }
                break;
        }
        return false;
    }
}
