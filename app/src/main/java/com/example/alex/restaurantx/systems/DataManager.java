package com.example.alex.restaurantx.systems;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.database.models.IngredientIndexModel;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.model.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {

    public void upgradeDishesWithType(List<Dish> pDishes, String pType) {
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.delete(DishModel.class, null, DishModel.TYPE + " = ?", DatabaseHelper.getSqlStringInterpret(pType));
        saveDishes(pDishes);
    }

    public void loadDishes(final IResultCallback<List<Dish>> pCallback, String pWhereClause, String... pArgs){
        final List<Dish> dishes = new ArrayList<>();
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor pCursor) {
                int indexNameColumn = pCursor.getColumnIndex(DishModel.NAME);
                int indexTypeColumn = pCursor.getColumnIndex(DishModel.TYPE);
                int indexWeightColumn = pCursor.getColumnIndex(DishModel.WEIGHT);
                int indexCostColumn = pCursor.getColumnIndex(DishModel.COST);
                int indexDescriptionColumn = pCursor.getColumnIndex(DishModel.DESCRIPTION);
                int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                int indexUserEstimationColumn = pCursor.getColumnIndex(DishModel.USER_ESTIMATION);
                int indexAverageEstimationColumn = pCursor.getColumnIndex(DishModel.AVERAGE_ESTIMATION);
                int indexBitmapUrlColumn = pCursor.getColumnIndex(DishModel.BITMAP_URL);
                while(pCursor.moveToNext()){
                    String name = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexNameColumn));
                    String type = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexTypeColumn));
                    String weight = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexWeightColumn));
                    int cost = pCursor.getInt(indexCostColumn);
                    String description = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexDescriptionColumn));
                    String ingredients[] = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(", ");
                    int userEstimation = pCursor.getInt(indexUserEstimationColumn);
                    float averageEstimation = pCursor.getFloat(indexAverageEstimationColumn);
                    String bitmapUrl = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexBitmapUrlColumn));
                    Dish dish = new Dish(name, cost, weight, ingredients);
                    dish.setType(type);
                    dish.setDescription(description);
                    dish.setBitmapUrl(bitmapUrl);
                    dish.getVote().setUserEstimation(userEstimation);
                    dish.getVote().setAverageEstimation(averageEstimation);
                    dishes.add(dish);
                }
                pCallback.onSuccess(dishes);
            }

            @Override
            public void onError(Exception e) {

            }
        }, "*", DishModel.class, pWhereClause, pArgs);
    }

    public void saveDishes(List<Dish> pDishes){
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        List<ContentValues> contentValuesList = getContentValuesList(pDishes);
        helper.bulkInsert(DishModel.class, contentValuesList, null);
    }

    private List<ContentValues> getContentValuesList(List<Dish> pDishes){
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (Dish dish : pDishes) {
            contentValuesList.add(dish.convert());
        }
        return contentValuesList;
    }

    public void saveIngredientsIndexes(){

    }

    public void loadIngredientsIndexes(final IResultCallback<HashMap<String, Integer>> pCallback){
        //TODO CHECK LOADING INDEXES FROM DB
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        Cursor pResult = null;
        helper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor pResult) {
                int indexIngredientColumn = pResult.getColumnIndex(IngredientIndexModel.INGREDIENT);
                int indexWeightColumn = pResult.getColumnIndex(IngredientIndexModel.WEIGHT);
                HashMap<String, Integer> indexes = new HashMap<>();
                try {
                    while (pResult.moveToNext()) {
                        indexes.put(pResult.getString(indexIngredientColumn), pResult.getInt(indexWeightColumn));
                    }
                } finally {
                    pResult.close();
                }
                pCallback.onSuccess(indexes);
            }
            @Override
            public void onError(Exception e) {
                pCallback.onError(e);
            }
        }, "*", IngredientIndexModel.class, "");
    }
}
