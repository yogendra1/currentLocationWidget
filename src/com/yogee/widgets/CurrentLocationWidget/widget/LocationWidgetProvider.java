package com.yogee.widgets.CurrentLocationWidget.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.yogee.widgets.CurrentLocationWidget.R;
import com.yogee.widgets.CurrentLocationWidget.services.LocationService;
import com.yogee.widgets.CurrentLocationWidget.utils.Constants;
import com.yogee.widgets.CurrentLocationWidget.utils.Utilities;

/**
 * Created by yogendra on 18/04/14.
 */
public class LocationWidgetProvider extends AppWidgetProvider {

    private static final String TAG = LocationWidgetProvider.class.getSimpleName();

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Utilities.AppLog.d(TAG, ">>>>> widget update event called");

        /* starting get location service */
        context.startService(new Intent(context, LocationService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utilities.AppLog.d(TAG, ">>>>> app widget event received with action - " + intent.getAction());

        /* getting address */
        SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCE_LAST_ADDRESS, Activity.MODE_PRIVATE);
        String savedAddressText = sp.getString(Constants.KEY_LAST_ADDRESS, "");
        Utilities.AppLog.d(TAG, ">>>> shared prefs data - " + savedAddressText);

        String addressToSend = "";
        String latitude = "";
        String longitude = "";
        if (savedAddressText != "") {
            String[] fields = savedAddressText.split("-");
            if (fields[0] != "") {
                latitude = fields[0];
            }
            if (fields[1] != "") {
                longitude = fields[1];
            }
            if (fields.length == 3 && fields[2] != "") {
                addressToSend = fields[2];
            }
        }

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Utilities.AppLog.d(TAG, ">>>>> widget clicked");

            /* starting get location service */
            context.startService(new Intent(context, LocationService.class));
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_DISABLED)) {
            Utilities.AppLog.d(TAG, ">>>>> last widget removed stopping the service");

            /* stopping get location service */
            context.stopService(new Intent(context, LocationService.class));
        } else if (intent.getAction().equals(Constants.INTENT_ACTION_SHARE_LOCATION)) {
            Utilities.AppLog.d(TAG, ">>>>> share location event received");

            /* starting the apps listening the share action */
            if (!addressToSend.equals("")) {
                addressToSend += "\n" + context.getString(R.string.map_link) + " " + context.getString(R.string.google_map_web_link) + latitude + "," + longitude;
                Utilities.shareText(context, context.getString(R.string.msg_i_am_are_nearby) + ", " + addressToSend);
            }
        } else if (intent.getAction().equals(Constants.INTENT_ACTION_SHOW_LOCATION_ON_MAP)) {
            Utilities.AppLog.d(TAG, ">>>>> location show on map event received");

            /* starting the apps listening the show location on map action */
            if (latitude != null && !latitude.equals("") && longitude != null && !longitude.equals("")) {
                Utilities.showOnMap(context, latitude, longitude, addressToSend);
            }
        } else if (intent.getAction().equals(Constants.INTENT_ACTION_SHOW_STATUS_BAR_NOTIFICATION)) {
            Utilities.AppLog.d(TAG, ">>>>> status bar notification event received");

              if (latitude != null && !latitude.equals("") && longitude != null && !longitude.equals("") && addressToSend != null && !addressToSend .equals("")) {
                Utilities.showStatusBarNotification(context, latitude, longitude, addressToSend);
            }
        }
    }
}
