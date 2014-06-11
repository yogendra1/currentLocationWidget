package com.yogee.widgets.CurrentLocationWidget.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import com.yogee.widgets.CurrentLocationWidget.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by yogendra on 17/04/14.
 */
public class AddressFinder extends AsyncTask<Location, Void, String> {

    private static final String TAG = AddressFinder.class.getSimpleName();

    /* context */
    private Context mContext;

    /* geo coder */
    private Geocoder geocoder;

    /* final message to return */
    private String message;

    /* provided location */
    private Location mLocation;

    /* listener interface */
    private AddressTextListener mAddressTextListener;

    /* shared preferences */
    private SharedPreferences sharedPreferences;

    /* lat lon pair distance threshold in meters */
    private int LAT_LON_DISTANCE_THRESHOLD = 50; //default is 50meters

    /* holds current address resolution attempt count */
    private static int currentAddressAttempt = 0;

    public AddressFinder(Context context, AddressTextListener addressTextListener) {
        super();
        mContext = context;
        mAddressTextListener = addressTextListener;
        sharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCE_LAST_ADDRESS, mContext.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Location... params) {

        /* getting location from input params */
        mLocation = params[0];
        message = mContext.getString(R.string.msg_could_not_resolve_address)+",\n Lat: " + mLocation.getLatitude() + "\n Lon: " + mLocation.getLongitude();

        /* checking last resolved address and checking the distance between current location and previous if distance is less or equal than threshold using previous one */
        String lastAddressTxt = sharedPreferences.getString(Constants.KEY_LAST_ADDRESS, "");
        if (lastAddressTxt != "") {
            String[] addressFields = lastAddressTxt.split("#@#");
            double latitude = Double.parseDouble(addressFields[0]);
            double longitude = Double.parseDouble(addressFields[1]);

            /* calculating distance in meters */
            Utilities.AppLog.d(TAG, "lat1=" + latitude + "::lon1=" + longitude + "::" + "lat2=" + mLocation.getLatitude() + "::lon2=" + mLocation.getLongitude());
            double distance = GeoUtils.distance(latitude, longitude, mLocation.getLatitude(), mLocation.getLongitude(), "MET");
            if (addressFields.length == 3 && distance <= LAT_LON_DISTANCE_THRESHOLD) {
                Utilities.AppLog.d(TAG, ">>>> old location distance is less or equal than threshold using previous address, distance - " + distance);
                message = addressFields[2];
                return message;
            }
        }

        /* reset the previous address text */
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String addressTxtToSave = mLocation.getLatitude() + "#@#" + mLocation.getLongitude() + "#@#" + "";
        Utilities.AppLog.d(TAG, ">>>> saving address - " + addressTxtToSave);
        editor.putString(Constants.KEY_LAST_ADDRESS, addressTxtToSave);
        editor.commit();

        String addressTxt = getAddressText();
        if (addressTxt != null) {
            message = addressTxt;
        }

        return message;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mAddressTextListener != null) {
            mAddressTextListener.onResult(message);
        }
    }

    /* returns address after trying multiple times */
    private synchronized String getAddressText() {

        String addressTxt = null;
        while (currentAddressAttempt <= Constants.MAX_ADDRESS_RESOLUTION_ATTEMPT) {

            Utilities.AppLog.d(TAG, ">>>> Address resolution attempt no.  - " + currentAddressAttempt);

            /* getting geo coder instance */
            if (Geocoder.isPresent()) {

                geocoder = new Geocoder(mContext, Locale.getDefault());

                 /* list of addresses */
                List<Address> addresses = null;

                 /* try to geocode */
                try {
                    addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {
                        Utilities.AppLog.d(TAG, ">>>> Address found - " + addresses.get(0).toString());

                        /* get address text for first address */
                        addressTxt = getFormattedAddress(addresses.get(0));
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentAddressAttempt++;
        }

        /* resetting attempt counter */
        currentAddressAttempt = 0;
        return addressTxt;
    }

    /* returns formatted address */
    private String getFormattedAddress(Address address) {

        String addressText = String.format(
                "%s%s%s%s%s.",
                // If there's a street address, add it
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                // postal code
                (address.getPostalCode() != null) ? ", " + address.getPostalCode() : "",
                // Locality is usually a city
                (address.getLocality() != null) ? ", " + address.getLocality() : "",
                // state
                (address.getAdminArea() != null) ? ", " + address.getAdminArea() : "",
                // The country of the address
                (address.getCountryName() != null) ? ", " + address.getCountryName() : ""
        );

        /* saving current address for future calls */
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String addressTxtToSave = mLocation.getLatitude() + "#@#" + mLocation.getLongitude() + "#@#" + addressText;
        Utilities.AppLog.d(TAG, ">>>> saving address - " + addressTxtToSave);
        editor.putString(Constants.KEY_LAST_ADDRESS, addressTxtToSave);
        editor.commit();

        // Return the text
        return addressText;
    }

    /* address listener interface */
    public interface AddressTextListener {
        public abstract void onResult(String addressTextOrError);
    }
}
