package com.example.alex.restaurantx.model;

import android.content.ContentValues;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.models.IngredientIndexModel;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientIndex {

    private HashMap<String, Integer> mIndexes;

    public IngredientIndex() {
        loadCachedIndexing();
    }

    private void loadCachedIndexing() {
        final DataManager manager = new DataManager();
        manager.loadIngredientsIndexes(new IResultCallback<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(final HashMap<String, Integer> pResult) {
                mIndexes = pResult;
            }

            @Override
            public void onError(final Exception e) {

            }
        });
    }

    public void addToIndexing(final Dish pDish) {
        for (final String ingredient : pDish.getIngredients()) {
            if (!mIndexes.containsKey(ingredient)) {
                mIndexes.put(ingredient, 0);
            }
        }
    }

    public void updateIndexing(final Dish pDish) {
        for (final String ingredient : pDish.getIngredients()) {
            if (!mIndexes.containsKey(ingredient)) {
                throw new IllegalStateException("perform adding to indexing before updating");
            }
            mIndexes.put(ingredient, mIndexes.get(ingredient) + 2);
        }
        for (final String ingredient : mIndexes.keySet()) {
            mIndexes.put(ingredient, mIndexes.get(ingredient) - 1);
        }
    }

    public HashMap<String, Integer> getTopIngredients(final int pNumOfIngredients) {
        if (pNumOfIngredients >= mIndexes.size() || pNumOfIngredients == 0) {
            throw new IllegalArgumentException("pNumOfIngredients >= mIndexes.size()");
        }
        final HashMap<String, Integer> topIndexes = new HashMap<>();
        int maxWeight = 0;
        String maxIngredient = "";
        for (int i = 0; i < pNumOfIngredients; i++) {
            for (final String ingredient : mIndexes.keySet()) {
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

    public int getEstimation(final Dish pDish) {
        int count = 0;
        for (final String ingredient : pDish.getIngredients()) {
            count += mIndexes.get(ingredient);
        }
        return count;
    }

    public List<ContentValues> convertToContentValues() {
        final List<ContentValues> contentValuesList = new ArrayList<>();
        for (final String ingredient : mIndexes.keySet()) {
            final ContentValues values = new ContentValues();
            values.put(IngredientIndexModel.INGREDIENT, ingredient);
            values.put(IngredientIndexModel.WEIGHT, mIndexes.get(ingredient));
            contentValuesList.add(values);
        }
        return contentValuesList;
    }
}
