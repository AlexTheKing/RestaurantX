package com.example.alex.restaurantx.systems;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public static final String UNIVERSAL_QUANTIFICATOR = "*";
    private static final String CLAUSE = "%s = ?";
    private static final String INGREDIENTS_SPLITTER = StringUtils.COMMA + StringUtils.SPACE;
    private final DatabaseHelper mHelper;

    public DataManager(final DatabaseHelper pHelper) {
        mHelper = pHelper;
    }

    public void loadDishes(final IResultCallback<List<Dish>> pCallback, final String pWhereClause, final String... pArgs) {
        final List<Dish> dishes = new ArrayList<>();
        final IResultCallback<Cursor> dishLoaderResultCallback = getDishLoaderResultCallback(dishes, pCallback);
        mHelper.query(dishLoaderResultCallback, UNIVERSAL_QUANTIFICATOR, DishModel.class, pWhereClause, pArgs);
    }

    public void saveDishes(final List<Dish> pDishes) {
        saveDishes(pDishes, null);
    }

    public void saveDishes(final List<Dish> pDishes, @Nullable final IResultCallback<Integer> pCallback) {
        final List<ContentValues> contentValuesList = getContentValuesListDishes(pDishes);
        mHelper.bulkInsert(DishModel.class, contentValuesList, pCallback);
    }

    public void resaveDishes(final List<Dish> pDishes, @Nullable final IResultCallback<Long> pCallback) {
        final List<ContentValues> insertValuesList = getContentValuesListDishes(pDishes);
        final List<ContentValues> updateValuesList = getContentValuesListDishes(pDishes);

        for (final ContentValues updateValues : updateValuesList) {
            updateValues.remove(DishModel.USER_ESTIMATION);
        }

        final String whereClause = String.format(CLAUSE, DishModel.NAME);
        final int length = insertValuesList.size();

        for (int i = 0; i < length; i++) {
            mHelper.insertOrUpdate(DishModel.class, insertValuesList.get(i), updateValuesList.get(i), whereClause, new String[]{updateValuesList.get(i).getAsString(DishModel.NAME)}, pCallback);
        }
    }

    public void updateDish(final Dish pDish, final IResultCallback<Integer> pCallback) {
        mHelper.update(DishModel.class, pDish.convert(), String.format(CLAUSE, DishModel.NAME), new String[]{DatabaseHelper.getSqlStringInterpret(pDish.getName())}, pCallback);
    }

    private IResultCallback<Cursor> getDishLoaderResultCallback(final List<Dish> pDishes, final IResultCallback<List<Dish>> pCallback) {
        return new IResultCallback<Cursor>() {

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

                while (pCursor.moveToNext()) {
                    final String name = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexNameColumn));
                    final String type = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexTypeColumn));
                    final String weight = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexWeightColumn));
                    final float cost = pCursor.getFloat(indexCostColumn);
                    final String currency = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexCurrencyColumn));
                    final String description = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexDescriptionColumn));
                    final String[] ingredients = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(INGREDIENTS_SPLITTER);
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
                    pDishes.add(dish);
                }

                pCallback.onSuccess(pDishes);
            }

            @Override
            public void onError(final Exception e) {
                pCallback.onError(e);
            }
        };
    }

    private List<ContentValues> getContentValuesListDishes(final List<Dish> pDishes) {
        final List<ContentValues> contentValuesList = new ArrayList<>();

        for (final Dish dish : pDishes) {
            contentValuesList.add(dish.convert());
        }

        return contentValuesList;
    }
}
