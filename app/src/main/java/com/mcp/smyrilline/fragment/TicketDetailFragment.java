package com.mcp.smyrilline.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.adapter.PassengerPagerAdapter;
import com.mcp.smyrilline.adapter.RoutePagerAdapter;
import com.mcp.smyrilline.model.Passenger;
import com.mcp.smyrilline.model.RouteItem;
import com.mcp.smyrilline.util.Utils;
import com.mcp.smyrilline.util.InnerViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;

/**
 * Fragment for My Ticket
 */
public class TicketDetailFragment extends Fragment {

    private static TicketDetailFragment mInstance;
    private ImageView imgBarcode;
    private TextView tvBookingName;
    private TextView tvCabinNumber;
    private TextView tvBookingNumber;
    private TextView tvTicketAdult;
    private TextView tvTicketChild;
    private TextView tvTicketInfant;
    private TextView tvBookingCar;
    private LinearLayout llTravellers;
    private TextView tvTravellersElipsis;
    private LinearLayout llChildrenDetail;
    private TextView tvChildren12;
    private TextView tvChildren15;
    private Button btnLogOut;
    private Context mContext;
    private ArrayList<RouteItem> mRouteList;
    private InnerViewPager mPassengerViewPager;
    private PassengerPagerAdapter mPassengerPagerAdapter;
    private ArrayList<Passenger> mPassengerList;
    private InnerViewPager mRouteViewPager;
    private RoutePagerAdapter mRoutePagerAdapter;

    public static Fragment newInstance() {
        if (mInstance == null)
            mInstance = new TicketDetailFragment();
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        getActivity().setTitle(mContext.getResources().getString(R.string.my_smyrilline));

        // Init UI
        View rootView = inflater.inflate(R.layout.fragment_ticket_detail, container, false);
        imgBarcode = (ImageView) rootView.findViewById(R.id.imgBarcode);
        tvBookingName = (TextView) rootView.findViewById(R.id.tvBookingName);
        tvBookingNumber = (TextView) rootView.findViewById(R.id.tvBookingNumber);
        tvTicketAdult = (TextView) rootView.findViewById(R.id.tvTicketAdult);
        tvTicketChild = (TextView) rootView.findViewById(R.id.tvTicketChild);
        tvTicketInfant = (TextView) rootView.findViewById(R.id.tvTicketInfant);
        llTravellers = (LinearLayout) rootView.findViewById(R.id.ll_travellers);
        tvTravellersElipsis = (TextView) rootView.findViewById(R.id.tvTravellersElipsis);
        llChildrenDetail = (LinearLayout) rootView.findViewById(R.id.llChildrenDetail);
        tvChildren12 = (TextView) rootView.findViewById(R.id.tvChildren12);
        tvChildren15 = (TextView) rootView.findViewById(R.id.tvChildren15);
        tvBookingCar = (TextView) rootView.findViewById(R.id.tvBookingCar);
        btnLogOut = (Button) rootView.findViewById(R.id.btnLogOut);

        // Get saved ticket data from memory
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        tvBookingNumber.setText(sharedPreferences.getString(Utils.PREF_BOOKING_NUMBER, Utils.PREF_NO_ENTRY));
        tvBookingCar.setText(sharedPreferences.getString(Utils.PREF_CAR, Utils.PREF_NO_ENTRY));
        tvTicketAdult.setText(sharedPreferences.getString(Utils.PREF_ADULT, Utils.ZERO));
        tvTicketInfant.setText(sharedPreferences.getString(Utils.PREF_INFANT, Utils.ZERO));

        // Get child count from memory
        String childCount = sharedPreferences.getString(Utils.PREF_CHILD, Utils.ZERO);
        tvTicketChild.setText(childCount);

        // Show ellipsis only if child is more than one
        if (Integer.parseInt(childCount) > 0) {
            tvTravellersElipsis.setVisibility(View.VISIBLE);

            // Expand and collapse behaviour
            tvTravellersElipsis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llChildrenDetail.getVisibility() == View.VISIBLE)
                        llChildrenDetail.setVisibility(View.GONE);
                    else {
                        llChildrenDetail.setVisibility(View.VISIBLE);
                        tvChildren12.setText(sharedPreferences.getString(Utils.PREF_CHILD12, Utils.ZERO));
                        tvChildren15.setText(sharedPreferences.getString(Utils.PREF_CHILD15, Utils.ZERO));
                    }
                }
            });
        }

        // Load saved lists
        Gson gson = new Gson();

        String passengerListAsString = sharedPreferences.getString(Utils.PREF_PASSENGER_LIST, Utils.PREF_NO_ENTRY);
        if (!passengerListAsString.equals(Utils.PREF_NO_ENTRY) && !passengerListAsString.equals("null")) {
            mPassengerList = gson.fromJson(passengerListAsString, new TypeToken<ArrayList<Passenger>>() {
            }.getType());
        } else
            mPassengerList = new ArrayList<>();

        String routeListAsString = sharedPreferences.getString(Utils.PREF_ROUTE_LIST, Utils.PREF_NO_ENTRY);
        if (!routeListAsString.equals(Utils.PREF_NO_ENTRY) && !routeListAsString.equals("null")) {
            mRouteList = gson.fromJson(routeListAsString, new TypeToken<ArrayList<RouteItem>>() {
            }.getType());
        } else
            mRouteList = new ArrayList<>();

        CardView cardViewPassengersPager = (CardView) rootView.findViewById(R.id.cardViewPassengersPager);
        if (mPassengerList.isEmpty())
            cardViewPassengersPager.setVisibility(View.GONE);
        else {
            mPassengerPagerAdapter = new PassengerPagerAdapter(mContext, mPassengerList);
            mPassengerViewPager = (InnerViewPager) rootView.findViewById(R.id.passengerViewPager);
            mPassengerViewPager.setAdapter(mPassengerPagerAdapter);
            mPassengerViewPager.setCurrentItem(0);
            PageIndicator passengerPagerIndicator = (CirclePageIndicator) rootView.findViewById(R.id.passengerPagerIndicator);
            passengerPagerIndicator.setViewPager(mPassengerViewPager);
        }

        CardView cardViewRoutesPager = (CardView) rootView.findViewById(R.id.cardViewRoutesPager);
        if (mRouteList.isEmpty())
            cardViewRoutesPager.setVisibility(View.GONE);
        else {
            mRouteViewPager = (InnerViewPager) rootView.findViewById(R.id.routeViewPager);
            mRoutePagerAdapter = new RoutePagerAdapter(mContext, mRouteList);
            mRouteViewPager.setAdapter(mRoutePagerAdapter);
            mRouteViewPager.setCurrentItem(0);
            PageIndicator routePagerIndicator = (CirclePageIndicator) rootView.findViewById(R.id.routePagerIndicator);
            routePagerIndicator.setViewPager(mRouteViewPager);
        }

        // Logout/change booking
        btnLogOut.setAllCaps(false);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User logged out
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Utils.PREF_LOGGED_IN, false);
                editor.apply();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new LoginFragment())
                        .commit();
            }
        });

        // Draw barcode
        String bookingNumber = sharedPreferences.getString(Utils.PREF_BOOKING_NUMBER, Utils.PREF_NO_ENTRY);
        // Calculate control number from 12 digits BBBBBBBBNNNT
        // B - booking, N - passenger index, T - type ie. 0
        String fullCode = getFullCode(bookingNumber + "001" + 0);
        try {
            generateBarcode(fullCode, imgBarcode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    ////////////////////////////////////////////////////////////////
    // this method generates EAN 13 control number ans returns full
    // string to encode
    private String getFullCode(String codeString12) {

        int chetVal = 0, nechetVal = 0;
        String codeToParse = codeString12;

        for (int index = 0; index < 6; index++) {
            chetVal += Integer.valueOf(codeToParse.substring(
                    index * 2 + 1, index * 2 + 2)).intValue();
            nechetVal += Integer.valueOf(codeToParse.substring(
                    index * 2, index * 2 + 1)).intValue();
        }

        chetVal *= 3;
        int controlNumber = 10 - (chetVal + nechetVal) % 10;
        if (controlNumber == 10) controlNumber = 0;

        codeToParse += String.valueOf(controlNumber);

        return codeToParse;

    }

    private void generateBarcode(String data, ImageView img) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.EAN_13, 150, 150);
        Bitmap ImageBitmap = Bitmap.createBitmap(180, 40, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 180; i++) {//width
            for (int j = 0; j < 40; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            img.setImageBitmap(ImageBitmap);
        } else {
            Toast.makeText(mContext, /*getResources().getString(R.string.userInputError)*/"Barcode not generated\nInvalid input!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
