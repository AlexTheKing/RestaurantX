package com.example.alex.restaurantx.systems;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.database.models.IngredientIndexModel;
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.model.IngredientIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {

    public static final String UNIVERSAL_QUANTIFICATOR = "*";

    public void upgradeDishesWithType(final List<Dish> pDishes, final String pType) {
        final DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.delete(DishModel.class, null, DishModel.TYPE + " = ?", DatabaseHelper.getSqlStringInterpret(pType));
        saveDishes(pDishes);
    }

    public void loadDishes(final IResultCallback<List<Dish>> pCallback, final String pWhereClause, final String... pArgs) {
        final List<Dish> dishes = new ArrayList<>();
        final DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pCursor) {
                final int indexNameColumn = pCursor.getColumnIndex(DishModel.NAME);
                final int indexTypeColumn = pCursor.getColumnIndex(DishModel.TYPE);
                final int indexWeightColumn = pCursor.getColumnIndex(DishModel.WEIGHT);
                final int indexCostColumn = pCursor.getColumnIndex(DishModel.COST);
                final int indexCurrencyColumn = pCursor.getColumnIndex(DishModel.CURRENCY);
                final int indexDescriptionColumn = pCursor.getColumnIndex(DishModel.DESCRIPTION);
                final int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                final int indexUserEstimationColumn = pCursor.getColumnIndex(DishModel.USER_ESTIMATION);
                final int indexAverageEstimationColumn = pCursor.getColumnIndex(DishModel.AVERAGE_ESTIMATION);
                final int indexBitmapUrlColumn = pCursor.getColumnIndex(DishModel.BITMAP_URL);
                final String splitString = ", ";
                while (pCursor.moveToNext()) {
                    final String name = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexNameColumn));
                    final String type = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexTypeColumn));
                    final String weight = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexWeightColumn));
                    final float cost = pCursor.getFloat(indexCostColumn);
                    final String currency = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexCurrencyColumn));
                    final String description = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexDescriptionColumn));
                    final String ingredients[] = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(splitString);
                    final int userEstimation = pCursor.getInt(indexUserEstimationColumn);
                    final float averageEstimation = pCursor.getFloat(indexAverageEstimationColumn);
                    final String bitmapUrl = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexBitmapUrlColumn));
                    final Dish dish = new Dish(name, cost, weight, ingredients);
                    dish.setType(type);
                    dish.setCurrency(currency);
                    dish.setDescription(description);
                    dish.setBitmapUrl(bitmapUrl);
                    dish.getVote().setUserEstimation(userEstimation);
                    dish.getVote().setAverageEstimation(averageEstimation);
                    dishes.add(dish);
                }
                pCallback.onSuccess(dishes);
            }

            @Override
            public void onError(final Exception e) {

            }
        }, UNIVERSAL_QUANTIFICATOR, DishModel.class, pWhereClause, pArgs);
    }

    public void saveDishes(final List<Dish> pDishes) {
        saveDishes(pDishes, null);
    }

    public void saveDishes(final List<Dish> pDishes, @Nullable final IResultCallback<Integer> pCallback) {
        final DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        final List<ContentValues> contentValuesList = getContentValuesListDishes(pDishes);
        helper.bulkInsert(DishModel.class, contentValuesList, pCallback);
    }

    private List<ContentValues> getContentValuesListDishes(final List<Dish> pDishes) {
        final List<ContentValues> contentValuesList = new ArrayList<>();
        for (final Dish dish : pDishes) {
            contentValuesList.add(dish.convert());
        }
        return contentValuesList;
    }

    public void saveIngredientsIndexes(final IngredientIndex pIngredientIndex) {
        //TODO : CHECK SAVING INDEXES IN DB
        final DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.bulkInsert(IngredientIndexModel.class, pIngredientIndex.convertToContentValues(), null);
    }

    public void loadIngredientsIndexes(final IResultCallback<HashMap<String, Integer>> pCallback) {
        //TODO : CHECK LOADING INDEXES FROM DB
        final DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pResult) {
                final int indexIngredientColumn = pResult.getColumnIndex(IngredientIndexModel.INGREDIENT);
                final int indexWeightColumn = pResult.getColumnIndex(IngredientIndexModel.WEIGHT);
                final HashMap<String, Integer> indexes = new HashMap<>();
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
            public void onError(final Exception e) {
                pCallback.onError(e);
            }
        }, UNIVERSAL_QUANTIFICATOR, IngredientIndexModel.class, "");
    }
}
