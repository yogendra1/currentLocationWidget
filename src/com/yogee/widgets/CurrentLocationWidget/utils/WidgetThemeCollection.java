package com.yogee.widgets.CurrentLocationWidget.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import com.yogee.widgets.CurrentLocationWidget.R;
import com.yogee.widgets.CurrentLocationWidget.widget.LocationWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yogendra on 21/04/14.
 */
public class WidgetThemeCollection {

    /* private instance */
    private static WidgetThemeCollection widgetThemeCollection;

    /* preference for selected theme */
    private static final String PREFERENCE_SELECTED_THEME = "PREFERENCE_SELECTED_THEME";

    /* key selected theme id */
    private static final String KEY_SELECTED_THEME_ID = "selected_theme_id";

    /* theme collection */
    final static List<WidgetTheme> themes = new ArrayList<WidgetTheme>();

    /* auto initialize the themes */
    static {
        /* white on black */
        themes.add(new WidgetTheme(0, "White on Black", R.color.white, R.drawable.theme_white_on_black, R.layout.layout_theme_white_on_black));
        /* white on transparent black */
        themes.add(new WidgetTheme(1, "White on Black transparent", R.color.white, R.drawable.theme_transparent_black, R.layout.layout_theme_transparent_black));
        /* white text on transparent*/
        themes.add(new WidgetTheme(2, "Transparent with White text", R.color.white, R.drawable.theme_transparent_with_white_text, R.layout.layout_theme_transparent_with_white_text));
        /* black on bisque */
        themes.add(new WidgetTheme(3, "Black on White", R.color.black, R.drawable.theme_black_on_bisque, R.layout.layout_theme_black_on_bisque));
        /* black text on transparent */
        themes.add(new WidgetTheme(4, "Transparent with Black text", R.color.black, R.drawable.theme_transparent_with_black_text, R.layout.layout_theme_transparent_with_black_text));
        /* transparent lime */
        themes.add(new WidgetTheme(5, "Lime", R.color.white, R.drawable.theme_lime, R.layout.layout_theme_lime));
        /* transparent aqua */
        themes.add(new WidgetTheme(6, "Aqua", R.color.white, R.drawable.theme_aqua, R.layout.layout_theme_aqua));
    }

    /* private constructor */
    private WidgetThemeCollection() {

    }

    /**
     * @return - same instance of WidgetThemeCollection
     */
    public static WidgetThemeCollection getInstance() {
        if (widgetThemeCollection == null) {
            widgetThemeCollection = new WidgetThemeCollection();
        }
        return widgetThemeCollection;
    }


    /**
     * @return available themes, information of all available themes are must added here
     */
    public List<WidgetTheme> getAvailableThemes() {
        return themes;
    }

    /**
     * @param themeId
     * @return selected theme by id
     */
    public WidgetTheme getThemeById(int themeId) {
        for (WidgetTheme theme : themes) {
            if (theme.getThemeId() == themeId) {
                return theme;
            }
        }
        return null;
    }

    /**
     * @param context
     * @return current selected theme id default is 0th theme white text on black background
     */
    public int getSelectedThemeId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_SELECTED_THEME, context.MODE_PRIVATE);
        return preferences.getInt(KEY_SELECTED_THEME_ID, 0);
    }

    /**
     * @param context
     * @param themeId
     */
    public void setSelectedThemeId(Context context, int themeId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_SELECTED_THEME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_SELECTED_THEME_ID, themeId);
        editor.commit();
    }
}
