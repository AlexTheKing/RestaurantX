package com.example.alex.restaurantx.systems;

import com.example.alex.restaurantx.model.Dish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecommenderSystem {

    private static RecommenderSystem sRecommenderSystem;
    private HashMap<String, Integer> mIndexes;

    private RecommenderSystem() {
        mIndexes = new HashMap<>();
    }

    public static RecommenderSystem getInstance() {
        if (sRecommenderSystem == null) {
            sRecommenderSystem = new RecommenderSystem();
            sRecommenderSystem.loadCachedIndexing();
        }
        return sRecommenderSystem;
    }

    private void loadCachedIndexing() {
        //TODO load indexes from DB or file
    }

    public void saveIndexing() {
        //TODO save indexes in DB or file
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

    public HashMap<String, Integer> getTopIngredients(int pNumOfModels) {
        if (pNumOfModels >= mIndexes.size() || pNumOfModels == 0) {
            throw new IllegalArgumentException("pNumOfModels >= mIndexes.size()");
        }
        HashMap<String, Integer> topIndexes = new HashMap<>();
        int maxWeight = 0;
        String maxIngredient = "";
        for (int i = 0; i < pNumOfModels; i++) {
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

    private int getEstimation(Dish pDish) {
        int count = 0;
        for (String ingredient : pDish.getIngredients()) {
            count += mIndexes.get(ingredient);
        }
        return count;
    }

    private List<Dish> getSortedMenuByTop(List<Dish> pDishesOfOneType) {
        List<Dish> sortedDishes = new ArrayList<>();
        HashMap<Dish, Integer> estimations = new HashMap<>();
        for (Dish dish : pDishesOfOneType) {
            estimations.put(dish, getEstimation(dish));
        }
        int maxEstimation = 0;
        Dish maxDish = null;
        for (int i = 0; i < pDishesOfOneType.size(); i++) {
            for (Dish dish : estimations.keySet()) {
                if (estimations.get(dish) > maxEstimation && !sortedDishes.contains(dish)) {
                    maxEstimation = estimations.get(dish);
                    maxDish = dish;
                }
            }
            sortedDishes.add(maxDish);
            maxDish = null;
            maxEstimation = 0;
        }
        return sortedDishes;
    }

    public List<Dish> analyzeMenu(HashMap<String, List<Dish>> pDishesWithTypes) {
        List<Dish> newMenu = new ArrayList<>();
        for (String typeOfDish : pDishesWithTypes.keySet()) {
            newMenu.addAll(getSortedMenuByTop(pDishesWithTypes.get(typeOfDish)));
        }
        return newMenu;
    }
}
