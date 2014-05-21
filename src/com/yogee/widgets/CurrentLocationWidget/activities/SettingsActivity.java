package com.yogee.widgets.CurrentLocationWidget.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.yogee.widgets.CurrentLocationWidget.R;
import com.yogee.widgets.CurrentLocationWidget.services.LocationService;
import com.yogee.widgets.CurrentLocationWidget.utils.*;

import java.util.List;

public class SettingsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    /**/
    private int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /* theme list */
    private ListView themeList;

    /* checked text box for on/off status bar notification */
    private CheckedTextView chkOnOffNotification;

    /* theme list items */
    private List<WidgetTheme> themes;

    /* adapter instance */
    private BaseAdapter themeListAdapter;

    /* checked text view on click listener */
    private View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleStatusBarNotification();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        /* checked text view instance */
        chkOnOffNotification = (CheckedTextView) findViewById(R.id.chk_toggle_notification);
        initStatusBarNotificationStatus();
        chkOnOffNotification.setOnClickListener(checkBoxClickListener);

        /* setting up theme list */
        themeList = (ListView) findViewById(R.id.list_widget_themes);
        themeList.setOnItemClickListener(this);
        themeList.setSmoothScrollbarEnabled(false);
    }

    /**
     * toggles the status bar notification
     */
    private void initStatusBarNotificationStatus() {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_STATUS_BAR_NOTIFICATION_STATUS, MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(Constants.KEY_STATUS_BAR_NOTIFICATION_STATUS, true);

        if (isChecked) {
            chkOnOffNotification.setChecked(true);
            chkOnOffNotification.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        } else {
            chkOnOffNotification.setChecked(false);
            chkOnOffNotification.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        }
        Utilities.AppLog.d(TAG, ">>>>>>>> notification status - " + isChecked);
    }

    /**
     * toggles the status bar notification
     */
    private void toggleStatusBarNotification() {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_STATUS_BAR_NOTIFICATION_STATUS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (chkOnOffNotification.isChecked()) {
            chkOnOffNotification.setChecked(false);
            chkOnOffNotification.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
            editor.putBoolean(Constants.KEY_STATUS_BAR_NOTIFICATION_STATUS, false);
            Utilities.cancelStatusBarNotification(this);
        } else {
            chkOnOffNotification.setChecked(true);
            chkOnOffNotification.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
            editor.putBoolean(Constants.KEY_STATUS_BAR_NOTIFICATION_STATUS, true);
            startService(new Intent(this, LocationService.class));
        }
        editor.commit();
        Utilities.AppLog.d(TAG, ">>>>>>>> notification status changed");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSettingsView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utilities.AppLog.d(TAG, ">>>> theme selected with id - " + position);

        /* setting current theme */
        WidgetThemeCollection.getInstance().setSelectedThemeId(this, position);

        /* starting the service */
        startService(new Intent(this, LocationService.class));

        /*  refreshing the adapter and list ui */
        themeListAdapter.notifyDataSetChanged();
        themeListAdapter.notifyDataSetInvalidated();
    }

    private void initSettingsView() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            Utilities.AppLog.d(TAG, ">>>> services are available connection result - " + result);

            /* showing select theme label */
            ((TextView) findViewById(R.id.lbl_select_theme)).setVisibility(View.VISIBLE);

            /* setting up theme list items */
            themes = WidgetThemeCollection.getInstance().getAvailableThemes();
            if (themeListAdapter == null) {
                themeListAdapter = new ThemeListAdapter(this, 0, themes);
            }
            themeList.setAdapter(themeListAdapter);
            themeList.setSelection(WidgetThemeCollection.getInstance().getSelectedThemeId(this));

        } else {

            /* Get the error dialog from Google Play services */
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    result, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            /* If Google Play services can provide an error dialog */
            if (errorDialog != null && !errorDialog.isShowing()) {
                /* Create a new DialogFragment for the error dialog */
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                /* Set the dialog in the DialogFragment */
                errorFragment.setDialog(errorDialog);
                /* Show the error dialog in the DialogFragment */
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
        }
    }

    /* class to handle dialog */
    public class ErrorDialogFragment extends DialogFragment {

        /* dialog */
        private Dialog mDialog;

        /* public constructor */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
