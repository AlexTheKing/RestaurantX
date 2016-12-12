package com.example.alex.restaurantx.constants;

import com.example.alex.restaurantx.holders.ContextHolder;

public class Constants {

    public static final String BASE_URL = "http://192.168.100.11:8080/";
    public static final String INTENT_EXTRA_TYPE = "type";
    public static final String INTENT_EXTRA_DISHNAME = "dish_name";
    public static final String INTENT_EXTRA_HAVE_PROHIBITED = "have_prohibited";
    public static final String GET_METHOD = "GET";
    public static final String SETTINGS_FILENAME = "SettingsIngredients";
    public static final String BROADCAST_UPDATE_MESSAGE = "UPDATE";
    public static final String INTENT_SERVICE_UPDATE_ACTION = ContextHolder.getInstance().getContext().getPackageName() + ".UPDATE_MENU";
}
