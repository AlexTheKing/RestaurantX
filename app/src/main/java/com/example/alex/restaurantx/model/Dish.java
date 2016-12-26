package com.example.alex.restaurantx.model;

import android.content.ContentValues;

import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;

public class Dish {

    private final String mName;
    private final String mWeight;
    private String mType;
    private final float mCost;
    private String mCurrency;
    private String mDescription;
    private final String[] mIngredients;
    private final Vote mVote = new Vote();
    private String mBitmapUrl;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(final String pDescription) {
        mDescription = pDescription;
    }

    public String getType() {
        return mType;
    }

    public void setType(final String pType) {
        mType = pType;
    }

    public Vote getVote() {
        return mVote;
    }

    public String getName() {
        return mName;
    }

    public String getWeight() {
        return mWeight;
    }

    public float getCost() {
        return mCost;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String pCurrency) {
        mCurrency = pCurrency;
    }

    public String getBitmapUrl() {
        return mBitmapUrl;
    }

    public void setBitmapUrl(final String pBitmapUrl) {
        mBitmapUrl = pBitmapUrl;
    }

    public String[] getIngredients() {
        return mIngredients;
    }

    public Dish(final String pName, final float pCost, final String pWeight, final String[] pIngredients) {
        mName = pName;
        mCost = pCost;
        mWeight = pWeight;
        mIngredients = pIngredients;
    }

    @Override
    public String toString() {
        final String base = mName + " " + mCost + " " + mWeight + " Ingredients:\n";
        return base + getIngredientsAsString();
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

    public ContentValues convert() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DishModel.NAME, DatabaseHelper.getSqlStringInterpret(mName));
        contentValues.put(DishModel.TYPE, DatabaseHelper.getSqlStringInterpret(mType));
        contentValues.put(DishModel.WEIGHT, DatabaseHelper.getSqlStringInterpret(mWeight));
        contentValues.put(DishModel.COST, mCost);
        contentValues.put(DishModel.CURRENCY, DatabaseHelper.getSqlStringInterpret(mCurrency));
        contentValues.put(DishModel.DESCRIPTION, DatabaseHelper.getSqlStringInterpret(mDescription));
        contentValues.put(DishModel.INGREDIENTS, DatabaseHelper.getSqlStringInterpret(getIngredientsAsString()));
        contentValues.put(DishModel.USER_ESTIMATION, mVote.getUserEstimation());
        contentValues.put(DishModel.AVERAGE_ESTIMATION, mVote.getAverageEstimation());
        contentValues.put(DishModel.BITMAP_URL, DatabaseHelper.getSqlStringInterpret(mBitmapUrl));
        return contentValues;
    }

    public class Vote {

        private final int MAX_ESTIMATION = 5;
        private int mUserEstimation;
        private float mAverageEstimation;

        public void setUserEstimation(final int pEstimation) throws IllegalArgumentException {
            if (!(pEstimation >= 1 && pEstimation <= MAX_ESTIMATION)) {
                throw new IllegalArgumentException("pEstimation must be >= 1 and <= " + MAX_ESTIMATION);
            }
            mUserEstimation = pEstimation;
        }

        public int getUserEstimation() {
            return mUserEstimation;
        }

        public float getAverageEstimation() {
            return mAverageEstimation;
        }

        public void setAverageEstimation(final float pAverageEstimation) {
            mAverageEstimation = pAverageEstimation;
        }
    }
}
