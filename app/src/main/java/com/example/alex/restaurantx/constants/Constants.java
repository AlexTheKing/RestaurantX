package com.example.alex.restaurantx.constants;

import com.example.alex.restaurantx.holders.ContextHolder;

public interface Constants {

    String INTENT_EXTRA_TYPE = "type";
    String INTENT_EXTRA_DISH_NAME = "dish_name";
    String INTENT_EXTRA_HAVE_PROHIBITED = "have_prohibited";
    String SETTINGS_FILENAME = "SettingsIngredients";
    String BROADCAST_UPDATE_MESSAGE = "UPDATE";
    int DATABASE_CURRENT_VERSION = 1;
    String INTENT_SERVICE_UPDATE_ACTION = ContextHolder.getInstance().getContext().getPackageName() + ".UPDATE_MENU";

    interface ApiSettings {
        //restaurant/api/request?method=
        String BASE_URL = "http://192.168.100.11:8080/";
        String PART_URL = "api/";
        String TYPES_METHOD = "types";
        String MENU_METHOD = "dishes";
        String RATE_METHOD = "rate?name=%s&id=%s&rate=%s";
        String ADD_COMMENT_METHOD = "comment?name=%s&id=%s&comment=%s";
        String COMMENTS_METHOD = "comments?name=%s";
        String GET_METHOD = "GET";
    }

    interface ImageLoaderSettings {
        int DEFAULT_MEMORY_CACHE_SIZE = 12 * 1024 * 1024;
    }

    interface ThreadManagerSettings {
        int DEFAULT_NUMBER_OF_THREADS = 3;
    }

    interface JsonHandlerSettigns {
        String RESPONSE = "response";
        String COMMENTS = "comments";
        String TYPES = "types";
    }
}
