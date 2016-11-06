package com.example.alex.restaurantx.systems;

import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.model.Dish;

import java.util.List;

public class DataManager {

    public void upgradeDatabase(List<Dish> pDishes){
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        for (Dish dish : pDishes) {
            //TODO UPGRADE DISHES IN DATABASE AND ADD UPGRADE METHOD IN DATABASEHELPER
        }
    }

}
