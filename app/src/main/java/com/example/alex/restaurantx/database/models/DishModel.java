package com.example.alex.restaurantx.database.models;

import com.example.alex.restaurantx.database.annotations.Table;
import com.example.alex.restaurantx.database.annotations.dbInteger;
import com.example.alex.restaurantx.database.annotations.dbPrimaryKey;
import com.example.alex.restaurantx.database.annotations.dbReal;
import com.example.alex.restaurantx.database.annotations.dbText;

@Table(value = "dishes")
public class DishModel {

    @dbInteger
    @dbPrimaryKey
    public static final String ID = "id";

    @dbText
    public static final String TYPE = "type";

    @dbText
    public static final String NAME = "name";

    @dbInteger
    public static final String WEIGHT = "weight";

    @dbReal
    public static final String COST = "cost";

    @dbText
    public static final String DESCRIPTION = "description";

    @dbText
    public static final String INGREDIENTS = "ingredients";

    @dbInteger
    public static final String USER_ESTIMATION = "estimation";

    @dbReal
    public static final String AVERAGE_ESTIMATION = "average_estimation";

    @dbText
    public static final String BITMAP_URL = "bitmap_url";
}
