package com.example.alex.restaurantx.backend.database.models;

import com.example.alex.restaurantx.backend.database.annotations.Table;
import com.example.alex.restaurantx.backend.database.annotations.dbInteger;
import com.example.alex.restaurantx.backend.database.annotations.dbText;

@Table("ratings")
public class RateModel {

    @dbText
    public static final String DISH_NAME = "dish_name";

    @dbText
    public static final String INSTANCE_ID = "instance_id";

    @dbInteger
    public static final String RATING = "rating";

}
