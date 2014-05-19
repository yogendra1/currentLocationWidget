package com.yogee.widgets.CurrentLocationWidget.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.yogee.widgets.CurrentLocationWidget.R;
import com.yogee.widgets.CurrentLocationWidget.activities.SettingsActivity;
import com.yogee.widgets.CurrentLocationWidget.receivers.EventReceiver;
import com.yogee.widgets.CurrentLocationWidget.utils.*;
import com.yogee.widgets.CurrentLocationWidget.widget.LocationWidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yogendra on 17/04/14.
 */
public class LocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, AddressFinder.AddressTextListener {
    private static final String TAG = LocationService.class.getSimpleName();
    /* time out handler runnable */
    private Runnable timeOutHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            Utilities.AppLog.d(TAG, ">>>>> timeout occurs");

            /* reset the previous address text */
            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCE_LAST_ADDRESS, Activity.MODE_PRIVATE).edit();
            editor.putString(Constants.KEY_LAST_ADDRESS, "");
            editor.commit();

            updateWidgetViews(getString(R.string.msg_unable_to_get_location));
            LocationService.this.stopSelf();
        }
    };
    /* location client instance */
    private LocationClient locationClient;
    /* location request instance */
    private LocationRequest locationRequest;
    /* time out handler */
    private Handler timeOutHandler;
    /* status indicates that google play services available on device or not */
    private int serviceConnectionStatus = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* checking service availability */
        if (!areServicesConnected()) {
             /* stopping service */
            LocationService.this.stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /* setting up location client if needed */
        if (locationClient == null) {
            locationClient = new LocationClient(this, this, this);
        }

        /* setting up location request if needed */
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(Constants.UPDATE_INTERVAL);
            locationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        }

        /* try to connect client */
        if (areServicesConnected() && !locationClient.isConnected() && !locationClient.isConnecting()) {

            /* start refresh progress bar */
            startProgress();

            Utilities.AppLog.d(TAG, ">>>>> trying to connect with location service");
            locationClient.connect();

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        /* free the resources */
        freeResources();

        super.onDestroy();

        Utilities.AppLog.d(TAG, ">>>>>> service destroyed");
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (Utilities.isLocationServiceEnabled(this)) {
            Utilities.AppLog.d(TAG, ">>>>> service connection successful initiating location request");
            locationClient.requestLocationUpdates(locationRequest, this);

            /* setting up custom time out handler */
            timeOutHandler = new Handler();
            timeOutHandler.postDelayed(timeOutHandlerRunnable, Constants.LOCATION_REQUEST_TIMEOUT);

        } else {
            Utilities.AppLog.d(TAG, ">>>>> Location services are disabled it seems");
            updateWidgetViews(getString(R.string.msg_services_disabled));

            /* stopping service */
            LocationService.this.stopSelf();
        }
    }

    @Override
    public void onDisconnected() {
        Utilities.AppLog.d(TAG, ">>>>> service connection disconnected");

        /* updating views */
        updateWidgetViews(getString(R.string.msg_connection_with_play_services_unsuccessful));

        /* stopping service */
        LocationService.this.stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Utilities.AppLog.d(TAG, ">>>> in onConnectionFailed with error code - " + connectionResult.getErrorCode());

        /* updating views */
        updateWidgetViews(getString(R.string.msg_connection_with_play_services_unsuccessful));

        /* stopping service */
        LocationService.this.stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        Utilities.AppLog.d(TAG, ">>>> location found - " + location.toString());

        /* remove time out handler */
        timeOutHandler.removeCallbacks(timeOutHandlerRunnable);

        /* requesting address */
        AddressFinder addressFinder = new AddressFinder(this, this);
        addressFinder.execute(location);
    }

    @Override
    public void onResult(String addressTextOrError) {

        Utilities.AppLog.d(TAG, ">>>>> address text - " + addressTextOrError);

        /* updating widgets */
        updateWidgetViews(addressTextOrError);

        /* stopping service */
        LocationService.this.stopSelf();
    }


    /* free the acquired resources */
    private void freeResources() {
        Utilities.AppLog.d(TAG, ">>>>>> freeing the resources");

        /* disconnecting from service */
        if (locationClient != null && locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
            locationClient.disconnect();
        }

        /* free the resources */
        locationClient = null;
        locationRequest = null;
    }

    /* utility methods */

    private boolean areServicesConnected() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            Utilities.AppLog.d(TAG, ">>>> services are available connection result - " + result);
            return true;
        } else {
            Utilities.AppLog.d(TAG, ">>>> services are not available connection result - " + result);

            /* starting activity if not started yet */
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            updateWidgetViews(getString(R.string.play_services_error));
            return false;
        }
    }

    /* update the data on widget */
    private void updateWidgetViews(String addressText) {

        /* getting widget manager instance */
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        ComponentName thisWidget = new ComponentName(this, LocationWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        WidgetTheme selected = WidgetThemeCollection.getInstance().getThemeById(WidgetThemeCollection.getInstance().getSelectedThemeId(this));

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), selected.getThemeLayoutId());

            /* setting the widget fields */
            remoteViews.setTextViewText(R.id.txt_address, addressText);

            /* applying date formatter */
            SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM hh:mm aa");
            String lastUpdateMsg = getString(R.string.msg_updated_at) + ", " + sdf.format(new Date(System.currentTimeMillis()));
            remoteViews.setTextViewText(R.id.txt_last_update_time, lastUpdateMsg);

            /* stopping progress view */
            remoteViews.setViewVisibility(R.id.location_progress, View.GONE);

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), LocationWidgetProvider.class);
            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.refresh_time_container, pendingIntent);

            // Register share button action only if we have actual location
            Intent shareIntent = new Intent(this.getApplicationContext(), LocationWidgetProvider.class);
            shareIntent.setAction(Constants.INTENT_ACTION_SHARE_LOCATION);
            shareIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            PendingIntent pendingShareIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_share_address, pendingShareIntent);

            // Register show on map action only if we have latitude and longitude
            Intent showOnMapIntent = new Intent(this.getApplicationContext(), LocationWidgetProvider.class);
            showOnMapIntent.setAction(Constants.INTENT_ACTION_SHOW_LOCATION_ON_MAP);
            showOnMapIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            PendingIntent pendingShowOnMapIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, showOnMapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btn_show_on_map, pendingShowOnMapIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

         /* sending broadcast to display status bar notification if it is enabled from settings */
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_STATUS_BAR_NOTIFICATION_STATUS, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(Constants.KEY_STATUS_BAR_NOTIFICATION_STATUS, true);
        if (isChecked) {
            Intent notificationIntent = new Intent(this, LocationWidgetProvider.class);
            notificationIntent.setAction(Constants.INTENT_ACTION_SHOW_STATUS_BAR_NOTIFICATION);
            sendBroadcast(notificationIntent);
        }
    }

    /* starting progress */
    private void startProgress() {

        /* getting widget manager instance */
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        ComponentName thisWidget = new ComponentName(this, LocationWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        WidgetTheme selected = WidgetThemeCollection.getInstance().getThemeById(WidgetThemeCollection.getInstance().getSelectedThemeId(this));

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), selected.getThemeLayoutId());
            remoteViews.setTextViewText(R.id.txt_last_update_time, getString(R.string.refreshing));

            /* stopping progress view */
            remoteViews.setViewVisibility(R.id.location_progress, View.VISIBLE);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
