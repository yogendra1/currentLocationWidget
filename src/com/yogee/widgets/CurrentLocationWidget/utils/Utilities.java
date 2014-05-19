package com.yogee.widgets.CurrentLocationWidget.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.yogee.widgets.CurrentLocationWidget.R;

/**
 * Created by yogendra on 18/04/14.
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    public static boolean isLocationServiceEnabled(Context context) {

        boolean result = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        // checking gps provider
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            result = true;
        }

        // checking network provider
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            result = true;
        }

        // checking passive provider
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            // avoid checking passive provider it returns always true
        }
        return result;
    }

    /**
     * @param context
     * @param shareText
     */
    public static void shareText(Context context, String shareText) {
        Intent sendIntent = new Intent();
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    /**
     * @param context
     * @param latitude
     * @param longitude
     */
    public static void showOnMap(Context context, String latitude, String longitude, String addressText) {
        String geoUri = "geo:0,0?q=" + latitude + "," + longitude;

        /* adding address if available */
        if (addressText != null && addressText != "") {
            geoUri += "(" + addressText + ")";
        }

        Uri uri = Uri.parse(geoUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * unused
     *
     * @param context
     * @return connected network type
     */
    public static String isConnectedToNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return Constants.TYPE_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return Constants.TYPE_MOBILE;
        }
        return Constants.TYPE_NOT_CONNECTED;
    }

    /**
     * @param context
     * @param latitude
     * @param longitude
     * @param addressText
     */
    public static void showStatusBarNotification(Context context, String latitude, String longitude, String addressText) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_mylocation));
        builder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
        builder.setContentTitle(context.getString(R.string.msg_you_are_nearby));
        builder.setContentText(addressText);
        builder.setAutoCancel(true);

        /* intent to launch map */
        String geoUri = "geo:0,0?q=" + latitude + "," + longitude;

         /* adding address if available */
        if (addressText != null && addressText != "") {
            geoUri += "(" + addressText + ")";
            builder.setTicker(context.getString(R.string.msg_you_are_nearby) + " - " + addressText);
        }

        Uri uri = Uri.parse(geoUri);
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        /* notify with arbitrary id */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* cancel if last notification is still exist */
        notificationManager.cancel(Constants.STATUS_BAR_NOTIFICATION_ID);

        /* notify */
        notificationManager.notify(Constants.STATUS_BAR_NOTIFICATION_ID, builder.build());
        AppLog.d(TAG, ">>> notification displayed");
    }

    /**
     * @param context
     */
    public static void cancelStatusBarNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.STATUS_BAR_NOTIFICATION_ID);
    }

    /* class for controlled logging */
    public static class AppLog {

        public static void d(String tag, String message) {
            if (Constants.loggingEnable) {
                Log.d(tag, message);
            }
        }
    }
}
