package com.example.alex.restaurantx.backend.models;

class Dish {

    private final String mName;
    private final String mWeight;
    private String mType;
    private final float mCost;
    private String mCurrency;
    private String mDescription;
    private float mAverageEstimation;
    private final String[] mIngredients;
    private final String mBitmapUrl;

    public void setType(String type) {
        mType = type;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setAverageEstimation(float averageEstimation) {
        mAverageEstimation = averageEstimation;
    }

    public Dish(final String name, final float cost, final String weight, final String[] ingredients, final String bitmapUrl) {
        mName = name;
        mCost = cost;
        mWeight = weight;
        mIngredients = ingredients;
        mBitmapUrl = bitmapUrl;
    }

    public String getIngredientsAsString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (final String ingredient : mIngredients) {
            builder.append(ingredient);
            builder.append((index != mIngredients.length - 1) ? ", " : "");
            index++;
        }
        return builder.toString();
    }

    public String getName() {
        return mName;
    }

    public String getWeight() {
        return mWeight;
    }

    public String getType() {
        return mType;
    }

    public float getCost() {
        return mCost;
    }

    public String getDescription() {
        return mDescription;
    }

    public float getAverageEstimation() {
        return mAverageEstimation;
    }

    public String[] getIngredients() {
        return mIngredients;
    }

    public String getBitmapUrl() {
        return mBitmapUrl;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String currency) {
        mCurrency = currency;
    }
}
