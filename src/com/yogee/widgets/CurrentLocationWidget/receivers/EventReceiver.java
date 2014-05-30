package com.yogee.widgets.CurrentLocationWidget.receivers;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.yogee.widgets.CurrentLocationWidget.services.LocationService;
import com.yogee.widgets.CurrentLocationWidget.utils.Utilities;
import com.yogee.widgets.CurrentLocationWidget.widget.LocationWidgetProvider;

/**
 * Created by yogendra on 17/04/14.
 */
public class EventReceiver extends BroadcastReceiver {
    private static final String TAG = EventReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        /* getting widget counts */
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LocationWidgetProvider.class);
        int widgetCount = appWidgetManager.getAppWidgetIds(thisWidget).length;
        Utilities.AppLog.d(TAG, ">>>>>>>>>> total widget count found  - " + widgetCount);


        boolean isScreenOn = Utilities.isScreenOn(context);
        Utilities.AppLog.d(TAG, ">>>>>>>>>> event received with action - " + intent.getAction() + " screen on - " + isScreenOn);

         /* starting service only in case if at least one widget instance is added to home screen of screen is on i.e assuming that this is the screen unlock event by user not by any other notification related app*/
        if (widgetCount > 0 && isScreenOn) {
            context.startService(new Intent(context, LocationService.class));
            Utilities.AppLog.d(TAG, ">>>>>>>>>> Starting the service");
        }
    }
}
