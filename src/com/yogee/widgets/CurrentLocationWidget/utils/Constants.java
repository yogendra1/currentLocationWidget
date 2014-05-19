package com.yogee.widgets.CurrentLocationWidget.utils;

/**
 * Created by yogendra on 17/04/14.
 */
public class Constants {

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // expiration duration location time out in seconds
    private static final int REQUEST_EXPIRATION_TIME = 90;
    // status bar notification id
    public static final int STATUS_BAR_NOTIFICATION_ID = 9898;
    // expiration duration in milliseconds
    public static final long LOCATION_REQUEST_TIMEOUT = MILLISECONDS_PER_SECOND * REQUEST_EXPIRATION_TIME;
    // action to share location
    public static final String INTENT_ACTION_SHARE_LOCATION = "com.yogee.widgets.CurrentLocationWidget.INTENT_ACTION_SHARE_LOCATION";
    // action to show location on map
    public static final String INTENT_ACTION_SHOW_LOCATION_ON_MAP = "com.yogee.widgets.CurrentLocationWidget.INTENT_ACTION_SHOW_LOCATION_ON_MAP";
    //action to show status bar notification
    public static final String INTENT_ACTION_SHOW_STATUS_BAR_NOTIFICATION = "com.yogee.widgets.CurrentLocationWidget.INTENT_ACTION_SHOW_STATUS_BAR_NOTIFICATION";

    // constants extra address to send
    public static final String EXTRA_ADDRESS_TO_SEND = "EXTRA_ADDRESS_TO_SEND";
    // extra location latitude
    public static final String EXTRA_LOCATION_LAT = "EXTRA_LOCATION_LAT";
    // extra location longitude
    public static final String EXTRA_LOCATION_LON = "EXTRA_LOCATION_LON";
    // connected network types
    public static final String TYPE_NOT_CONNECTED = "NOT_CONNECTED";
    public static final String TYPE_WIFI = "WIFI";
    public static final String TYPE_MOBILE = "MOBILE";

    // logging enable/disable flag
    public static final boolean loggingEnable = true;

    // shared preferences
    /* preference for selected theme */
    public static final String PREFERENCE_LAST_ADDRESS = "PREFERENCE_LAST_ADDRESS";
    /* preference for enable/disable status bar notification */
    public static final String PREFERENCE_STATUS_BAR_NOTIFICATION_STATUS = "PREFERENCE_STATUS_BAR_NOTIFICATION_STATUS";

    /* key selected theme id */
    public static final String KEY_LAST_ADDRESS = "LAST_ADDRESS";
    /* key  status bar notification enable/disable  notification*/
    public static final String KEY_STATUS_BAR_NOTIFICATION_STATUS = "KEY_STATUS_BAR_NOTIFICATION_STATUS ";
}
