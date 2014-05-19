package com.yogee.widgets.CurrentLocationWidget.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yogee.widgets.CurrentLocationWidget.services.LocationService;
import com.yogee.widgets.CurrentLocationWidget.utils.Utilities;

/**
 * Created by yogendra on 17/04/14.
 */
public class EventReceiver extends BroadcastReceiver {
    private static final String TAG = EventReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Utilities.AppLog.d(TAG, ">>>>>>>>>> action received starting the service - " + intent.getAction());
        context.startService(new Intent(context, LocationService.class));
    }
}
