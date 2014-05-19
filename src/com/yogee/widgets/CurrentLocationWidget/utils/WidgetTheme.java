package com.yogee.widgets.CurrentLocationWidget.utils;

/**
 * Created by yogendra on 21/04/14.
 */
public class WidgetTheme {

    /* theme id */
    private int mThemeId = -1;

    /* holds widget theme name */
    private String mThemeName;

    /* theme text color */
    private int mTextColorResourceId;

    /* holds widget theme resource id */
    private int mThemeBackgroundResourceId;

    /* holds widget theme layout id */
    private int mThemeLayoutId;

    /**
     * @param themeId
     * @param themeName
     * @param txtColorResourceId
     * @param backgroundResourceID
     * @param themeLayoutID
     */
    public WidgetTheme(int themeId, String themeName, int txtColorResourceId, int backgroundResourceID, int themeLayoutID) {
        mThemeId = themeId;
        mThemeName = themeName;
        mTextColorResourceId = txtColorResourceId;
        mThemeBackgroundResourceId = backgroundResourceID;
        mThemeLayoutId = themeLayoutID;
    }

    /**
     * @return theme name
     */
    public int getThemeId() {
        return mThemeId;
    }

    /**
     * @return theme name
     */
    public String getThemeName() {
        return mThemeName;
    }

    /**
     * @return theme color resource id
     */
    public int getTextColorResourceId() {
        return mTextColorResourceId;
    }

    /**
     * @return theme resource id
     */
    public int getThemeResourceId() {
        return mThemeBackgroundResourceId;
    }

    /**
     * @return theme layout id
     */
    public int getThemeLayoutId() {
        return mThemeLayoutId;
    }
}
