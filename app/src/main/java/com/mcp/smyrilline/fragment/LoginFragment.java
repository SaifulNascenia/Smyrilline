package com.mcp.smyrilline.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.activity.DrawerActivity;
import com.mcp.smyrilline.model.InternalStorage;
import com.mcp.smyrilline.model.Meal;
import com.mcp.smyrilline.model.MealDate;
import com.mcp.smyrilline.model.Passenger;
import com.mcp.smyrilline.model.RouteItem;
import com.mcp.smyrilline.util.AppUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by raqib on 5/17/17.
 */

public class LoginFragment extends android.support.v4.app.Fragment {
    private static final String SOAP_ACTION = "http://novedas-sosy.de/GetBookingData";
    private static final String METHOD_NAME = "GetBookingData";
    private static final String NAMESPACE = "http://novedas-sosy.de/";
    private static final String URL_BOOKING_SERVICE = "http://booking.smyrilline.com:8080/SmyrilSVC.asmx";
    public static RelativeLayout rlWelcomeHeader;
    private SharedPreferences mSharedPref;
    private Context mContext;
    private EditText etLastName;
    private EditText etBooking;
    private Button btnLogin;
    private String mLastName;
    private String mBookingNumber;
    private ProgressDialog mLoginProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Refresh toolbar options
        getActivity().invalidateOptionsMenu();

        mContext = getActivity();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());

        // Inflate tab_layout and setup Views.
        View rootView = inflater.inflate(R.layout.fragment_login, null);
        ((DrawerActivity)getActivity()).setToolbarAndToggle((Toolbar)rootView.findViewById(R.id.toolbar));
        getActivity().setTitle(mContext.getResources().getString(R.string.log_in));

        // Init UI
        etLastName = (EditText) rootView.findViewById(R.id.etLastName);
        etBooking = (EditText) rootView.findViewById(R.id.etBooking);
        btnLogin = (Button) rootView.findViewById(R.id.btnLogIn);

        // for testing
//        etLastName.setText("í Skorini");
//        etBooking.setText("34807457");

        // Login/view booking
        btnLogin.setAllCaps(false);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                AppUtils.hideKeyboard(getActivity());

                mLoginProgressDialog = ProgressDialog.show(mContext, getResources().getString(R.string.please_wait), getResources().getString(R.string.connecting));
                mLastName = etLastName.getText().toString().trim();
                mBookingNumber = etBooking.getText().toString().trim();
                startLoginThread();
            }
        });

        // When pressed done in this editbox, submit will be processed automatically
        etBooking.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide keyboard
                    AppUtils.hideKeyboard(getActivity());

                    // Submit
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void startLoginThread() {
        if (AppUtils.isNetworkAvailable(getActivity()))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String response = AppUtils.isDomainAvailable(mContext, URL_BOOKING_SERVICE);
                    if (response.equals(AppUtils.CONNECTION_OK))
                        new GetResponseFromSOAPTask().execute(mLoginProgressDialog);
                    else {
                        if (getActivity() != null) { // fragment is still showing
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    mLoginProgressDialog.dismiss();
                                    AppUtils.showAlertDialog(getActivity(), response);
                                }
                            });
                        }
                    }
                }
            }).start();
        else {
            mLoginProgressDialog.dismiss();
            AppUtils.showAlertDialog(mContext, getString(R.string.alert_no_wifi));
        }
    }

    /**
     * Get passenger list from json array
     *
     * @param passengersArray
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private ArrayList<Passenger> getPassengerList(JSONArray passengersArray) throws JSONException, ParseException {
        ArrayList<Passenger> passengerList = new ArrayList<>();
        for (int i = 0; i < passengersArray.length(); i++) {
            JSONObject passengerObject = passengersArray.getJSONObject(i);
            // name
            String name = passengerObject.getString("Name");
            // sex
            String sex = passengerObject.getString("Sex");
            sex = sex.toLowerCase();
            sex = sex.equals("m") ? "Male" : "Female";
            // nationality
            String nationality = passengerObject.getString("Nationality");
            Locale locale = new Locale("", nationality);

            // convert dob, getting yyyy-MM-dd, need dd/MM/yyyy
            String dob = passengerObject.getString("DateOfBirth");
            SimpleDateFormat givenDobDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat requiredDobDateFormat = new SimpleDateFormat(AppUtils.DATE_FORMAT_DOB);
            Date dobDate = givenDobDateFormat.parse(dob);
            dob = requiredDobDateFormat.format(dobDate);

            passengerList.add(new Passenger(name, sex, dob, locale.getDisplayCountry()));
        }

        return passengerList;
    }

    /**
     * Get route list from json array
     *
     * @param routeArray
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private ArrayList<RouteItem> getRouteList(JSONArray routeArray) throws JSONException, ParseException {
        ArrayList<RouteItem> routeList = new ArrayList<>();
        for (int i = 0; i < routeArray.length(); i++) {
            JSONObject routeObject = routeArray.getJSONObject(i);
            String departureHarbor = routeObject.getString("DepHarbor");
            String departureDate = routeObject.getString("DepDate");
            String arrivalHarbor = routeObject.getString("ArrHarbor");
            String arrivalDate = routeObject.getString("ArrDate");

            /**
             * We're getting time string from server as
             * DepDate: dd-mm-yyyy - HH:mm
             * ArrDate: dd-mm-yyyy HH:mm:ss
             *
             * Required time format like 'Apr 27 (Sat), 13:00'
             */
            SimpleDateFormat givenDepartureDateFormat = new SimpleDateFormat("dd-MM-yyyy - HH:mm");
            SimpleDateFormat givenArrivalDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
            SimpleDateFormat ourDateFormat = new SimpleDateFormat(AppUtils.DATE_FORMAT_ROUTE);
            SimpleDateFormat ourTimeFormat = new SimpleDateFormat(AppUtils.TIME_FORMAT_ROUTE);

            // departure
            Date date = givenDepartureDateFormat.parse(departureDate);  // taking date only
            departureDate = ourDateFormat.format(date) + "\n" + ourTimeFormat.format(date);
            // arrival
            date = givenArrivalDateFormat.parse(arrivalDate/*.split(" ")[0]*/);         // taking date only
            arrivalDate = ourDateFormat.format(date) + "\n" + ourTimeFormat.format(date);

            routeList.add(new RouteItem(departureHarbor, departureDate, arrivalHarbor, arrivalDate));
        }

        return routeList;
    }

    /* We are getting unsorted meal list from booking system
     * and have to group meals by date
     * so we first keep a sorted set of unique dates, and a list of all meals
     * later we'll add each meal to respective child list in each date
     */
    private ArrayList<MealDate> getMealList(JSONArray mealTypesArray) throws JSONException {
        // list of date objects
        ArrayList<MealDate> mealDateList = new ArrayList<>();
        // list of all meals
        ArrayList<Meal> allMeals = new ArrayList<>();
        for (int i = 0; i < mealTypesArray.length(); i++) {
            JSONObject mealObject = mealTypesArray.getJSONObject(i);
            String dateString = mealObject.getString("date");
            String count = mealObject.getString("count");
            String desc = mealObject.getString("desc");

            MealDate mealDate = new MealDate(dateString);

            // populate date object list, only add unique dates
            if (!mealDateList.contains(mealDate))
                mealDateList.add(mealDate);

            // populate meals list
            allMeals.add(new Meal(count, desc, dateString));
        }

        // sort date list in ascending date
        Collections.sort(mealDateList);
        // sort this to group meals with similar dates together
        Collections.sort(allMeals);


        // for each date, add meals to meal list
        for (int i = 0; i < mealDateList.size(); i++) {
            MealDate mealDate = mealDateList.get(i);
            ArrayList<Meal> mealGroup = new ArrayList<>();
            // compare each date of meals list with meal date object
            for (int j = 0; j < allMeals.size(); j++) {
                Meal meal = allMeals.get(j);
                if (mealDate.getDate().getTime() == meal.getDate().getTime())
                    mealGroup.add(meal);
            }
            mealDate.setMealList(mealGroup);
        }

        return mealDateList;
    }

    /**
     * Asynk task to request and get SOAP response from web service
     */
    private class GetResponseFromSOAPTask extends AsyncTask<ProgressDialog, Void, Object[]> {

        private String TAG = "GetResponseFromSoapTask";
        /**
         * Runs in thread
         *
         * @param progressDialogs The progress dialog view which will be dismissed
         * @return Object array contains 'response', & progress dialog
         */
        @Override
        protected Object[] doInBackground(ProgressDialog... progressDialogs) {
            //for linear parameter
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Name", mLastName);
            request.addProperty("BookNo", mBookingNumber);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE httpTransport = new HttpTransportSE(URL_BOOKING_SERVICE);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
            } catch (HttpResponseException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } //send request

            String result = null;
            try {
                result = envelope.getResponse().toString();
//                Log.i("RESPONSE", result.toString()); // see output in the console
            } catch (SoapFault e) {
                // TODO Auto-generated catch block
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {   // server has garbage
                Log.i(TAG, e.getMessage());
            }

            Object[] returnValues = {result, progressDialogs[0]};
            return returnValues;
        }

        @Override
        protected void onPostExecute(Object[] returnValues) {
            super.onPostExecute(returnValues);

            try {
                // First index is the result from SOAP request
                String result = returnValues[0].toString();

                JSONObject jsonObject = new JSONObject(result);
                // Get booking name
                String bookingName = jsonObject.getString("BookingName");
                // Get booking number
                String bookingNumber = jsonObject.getString("Bookno");
                // Get adult
                String adult = jsonObject.getString("NoOfAdults");
                // Get infant
                String infant = jsonObject.getString("NoOfInfants");
                // Get passenger list
                ArrayList<Passenger> passengerList = jsonObject.isNull("Passengers") ? null : getPassengerList(jsonObject.getJSONArray("Passengers"));
                // Get meals, if not null
                ArrayList<MealDate> mealDateList = jsonObject.isNull("MealTypes") ? null : getMealList(jsonObject.getJSONArray("MealTypes"));
                // Get route list
                ArrayList<RouteItem> routeList = jsonObject.isNull("ListOfRoutes") ? null : getRouteList(jsonObject.getJSONArray("ListOfRoutes"));
                // Get childs, check if they are integer first
                String child12 = jsonObject.getString("NoOfChild12");
                child12 =
                        child12 != null && !child12.isEmpty() && child12.matches("[\\d+]") ? child12
                                : "0";
                String child15 = jsonObject.getString("NoOfChild15");
                child15 =
                        child15 != null && !child15.isEmpty() && child15.matches("[\\d+]") ? child15
                                : "0";
                String child = String.valueOf(Integer.parseInt(child12) + Integer.parseInt(child15));
                // Get car
                String car = jsonObject.getString("TypeOfCar");
                car = car == "null" ? "-" : car;

                // Save ticket data to memory
                SharedPreferences.Editor editor = mSharedPref.edit();
                Gson gson = new Gson();
                editor.putString(AppUtils.PREF_BOOKING_NAME, bookingName);
                editor.putString(AppUtils.PREF_BOOKING_NUMBER, bookingNumber);
                editor.putString(AppUtils.PREF_ADULT, adult);
                editor.putString(AppUtils.PREF_CHILD, child);
                editor.putString(AppUtils.PREF_CHILD12, child12);
                editor.putString(AppUtils.PREF_CHILD15, child15);
                editor.putString(AppUtils.PREF_INFANT, infant);
                editor.putString(AppUtils.PREF_CAR, car);
                editor.putString(AppUtils.PREF_ROUTE_LIST, gson.toJson(routeList));
                editor.putString(AppUtils.PREF_PASSENGER_LIST, gson.toJson(passengerList));
                // Save meal list in file, to fix json exception in gson deserialize
                InternalStorage.writeObject(mContext, AppUtils.PREF_MEAL_LIST, mealDateList);

                // User is now logged in
                editor.putBoolean(AppUtils.PREF_LOGGED_IN, true);
                editor.apply();

                TicketFragment ticketFragment = new TicketFragment();
                ((DrawerActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, ticketFragment, DrawerActivity.FRAGMENT_PARENT_TAG)
                        .commit();

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.showAlertDialog(mContext, mContext.getResources().getString(R.string.invalid_input));
            } catch (NullPointerException e) {   // eg. result is null
                e.printStackTrace();
                AppUtils.showAlertDialog(mContext, mContext.getResources().getString(R.string.alert_server_error));
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                // dismiss the progress dialog (2nd param of returnValues)
                ((ProgressDialog) returnValues[1]).dismiss();
            }
        }
    }
}
