package com.example.alex.restaurantx.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.IngredientIndexModel;
import com.example.alex.restaurantx.holder.ContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientIndex {

    private HashMap<String, Integer> mIndexes;

    public IngredientIndex() {
        mIndexes = new HashMap<>();
        loadCachedIndexing();
    }

    private void loadCachedIndexing() {
        //TODO CHECK LOADING INDEXES FROM DB
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        Cursor cursor = null;
        try {
            cursor = helper.query("*", IngredientIndexModel.class, "");
            int indexIngredientColumn = cursor.getColumnIndex(IngredientIndexModel.INGREDIENT);
            int indexWeightColumn = cursor.getColumnIndex(IngredientIndexModel.WEIGHT);
            while (cursor.moveToNext()) {
                mIndexes.put(cursor.getString(indexIngredientColumn), cursor.getInt(indexWeightColumn));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveIndexing() {
        //TODO CHECK SAVING INDEXES IN DB
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.bulkInsert(IngredientIndexModel.class, convertToContentValues());
    }

    public void addToIndexing(Dish pDish) {
        for (String ingredient : pDish.getIngredients()) {
            if (!mIndexes.containsKey(ingredient)) {
                mIndexes.put(ingredient, 0);
            }
        }
    }

    public void updateIndexing(Dish pDish) {
        for (String ingredient : pDish.getIngredients()) {
            if (!mIndexes.containsKey(ingredient)) {
                throw new IllegalStateException("perform adding to indexing before updating");
            }
            mIndexes.put(ingredient, mIndexes.get(ingredient) + 2);
        }
        for (String ingredient : mIndexes.keySet()) {
            mIndexes.put(ingredient, mIndexes.get(ingredient) - 1);
        }
    }

    public HashMap<String, Integer> getTopIngredients(int pNumOfIngredients) {
        if (pNumOfIngredients >= mIndexes.size() || pNumOfIngredients == 0) {
            throw new IllegalArgumentException("pNumOfIngredients >= mIndexes.size()");
        }
        HashMap<String, Integer> topIndexes = new HashMap<>();
        int maxWeight = 0;
        String maxIngredient = "";
        for (int i = 0; i < pNumOfIngredients; i++) {
            for (String ingredient : mIndexes.keySet()) {
                if (mIndexes.get(ingredient) > maxWeight && !topIndexes.containsKey(ingredient)) {
                    maxWeight = mIndexes.get(ingredient);
                    maxIngredient = ingredient;
                }
            }
            topIndexes.put(maxIngredient, maxWeight);
            maxIngredient = "";
            maxWeight = 0;
        }
        return topIndexes;
    }

    public int getEstimation(Dish pDish) {
        int count = 0;
        for (String ingredient : pDish.getIngredients()) {
            count += mIndexes.get(ingredient);
        }
        return count;
    }

    private List<ContentValues> convertToContentValues() {
        List<ContentValues> contentValuesList = new ArrayList<>();
        for (String ingredient : mIndexes.keySet()) {
            ContentValues values = new ContentValues();
            values.put(IngredientIndexModel.INGREDIENT, ingredient);
            values.put(IngredientIndexModel.WEIGHT, mIndexes.get(ingredient));
            contentValuesList.add(values);
        }
        return contentValuesList;
    }
}
