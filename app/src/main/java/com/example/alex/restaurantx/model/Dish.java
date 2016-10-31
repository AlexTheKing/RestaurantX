package com.example.alex.restaurantx.model;

import java.util.List;

public class Dish {

    private String mName;
    private String mWeight;
    private int mCost;
    private List<String> mIngredients;

    public String getName() {
        return mName;
    }

    public String getWeight() {
        return mWeight;
    }

    public int getCost() {
        return mCost;
    }

    public List<String> getIngredients() {
        return mIngredients;
    }

    public Dish(String pName, int pCost, String pWeight, List<String> pIngredients) {
        mName = pName;
        mCost = pCost;
        mWeight = pWeight;
        mIngredients = pIngredients;
    }

    @Override
    public String toString() {
        String base = mName + " " + mCost + " " + mWeight + " Ingredients:\n";
        String ingredients = "";
        for (int i = 0; i < mIngredients.size(); i++) {
            ingredients += mIngredients.get(i) + " ";
        }
        return base + ingredients;
    }
}
