package com.team2052.frckrawler.themes;

import android.content.Context;
import android.support.annotation.StyleRes;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.helpers.ScoutHelper;

public enum Themes {
    DEFAULT("Default", R.style.AppTheme, false),
    BLUE("Blue", R.style.AppTheme_Blue, false),
    RED("Red", R.style.AppTheme_Red, false),
    DARK("Dark", R.style.AppTheme_Dark, false);

    private String name;
    @StyleRes
    private int theme_res_id;
    private boolean isLight;

    Themes(String name, @StyleRes int theme_res_id, boolean isLight) {
        this.name = name;
        this.theme_res_id = theme_res_id;
        this.isLight = isLight;
    }

    public static Themes getCurrentTheme(Context context) {
        return Themes.values()[ScoutHelper.getScoutTheme(context)];
    }

    public static String[] getNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].getName();
        }
        return names;
    }

    @StyleRes
    public int getTheme() {
        return theme_res_id;
    }

    public String getName() {
        return name;
    }

    public boolean isLight() {
        return isLight;
    }
}