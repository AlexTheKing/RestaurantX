package com.example.alex.restaurantx.database.models;

import com.example.alex.restaurantx.database.annotations.Table;
import com.example.alex.restaurantx.database.annotations.dbInteger;
import com.example.alex.restaurantx.database.annotations.dbText;

@Table(value = "ingredients")
public class IngredientIndexModel {

    @dbText
    public static final String INGREDIENT = "ingredient";

    @dbInteger
    public static final String WEIGHT = "weight";

}
