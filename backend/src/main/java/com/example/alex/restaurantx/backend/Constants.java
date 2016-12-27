package com.example.alex.restaurantx.backend;

interface Constants {

    String DATABASE_PATH = "xrest.db";

    interface JsonSettings {
        String RESPONSE = "response";
        String TYPES = "types";
        String ERROR_REASON = "database unavailable";
        String INGREDIENTS_SPLITTER = ", ";
        String COMMENTS = "comments";
    }
}
