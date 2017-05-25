package com.example.alex.restaurantx.constants;

public interface Constants {

    String INTENT_EXTRA_DISH_TYPE = "EXTRA_DISH_TYPE";
    String INTENT_EXTRA_RECOMMENDATIONS = "EXTRA_RECOMMENDATIONS";
    String INTENT_EXTRA_DISH_NAME = "EXTRA_DISH_NAME";
    String INTENT_EXTRA_HAVE_PROHIBITED = "EXTRA_HAVE_PROHIBITED";
    String INTENT_EXTRA_ACTION_UPDATE = "EXTRA_ACTION_UPDATE";
    String ACTION_UPDATE_BROADCAST = "com.alex.restaurantx.ACTION_UPDATE_BROADCAST";
    String SETTINGS_FILENAME = "SettingsIngredients";

    interface ApiSettings {

        //restaurant/app/api/request_type/request_name?params=
        String HOST_URL = "http://192.168.100.10:8080/";
        String RELATIVE_URL = "app/api/";
        String LIST_TYPES = "list/types";
        String LIST_DISHES = "list/dishes";
        String ADD_RATE = "add/rate?dishname=%s&appid=%s&rate=%s";
        String ADD_COMMENT = "add/comment?dishname=%s&appid=%s&comment=%s";
        String LIST_COMMENTS = "list/comments?dishname=%s";
        String LIST_RECOMMENDATIONS = "list/recommendations?appid=%s";
    }

    interface ImageLoaderSettings {

        int DEFAULT_MEMORY_CACHE_SIZE = 12 * 1024 * 1024;
        int MEMORY_FACTOR = 4;
    }

    interface ThreadManagerSettings {

        int DEFAULT_NUMBER_OF_THREADS = 3;
        int DEFAULT_PROCESSORS_THRESHOLD = 3;
    }

    interface JsonHandlerSettings {

        String RESPONSE = "response";
        String COMMENTS = "comments";
        String TYPES = "types";
        String RECOMMENDATIONS = "recommendations";
    }

    interface DatabaseSettings {

        String DATABASE_NAME = "xrestdb.sqlite";
        int DATABASE_CURRENT_VERSION = 1;
    }
}
