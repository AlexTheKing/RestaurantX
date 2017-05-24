package com.example.alex.restaurantx.model;

import android.content.ContentValues;

import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.util.StringUtils;

import java.util.Locale;

public class Dish {

    private final String mName;
    private final String mWeight;
    private final float mCost;
    private final String[] mIngredients;
    private final Vote mVote = new Vote();
    private String mType;
    private String mCurrency;
    private String mDescription;
    private String mBitmapUrl;

    public Dish(final String pName, final float pCost, final String pWeight, final String[] pIngredients) {
        mName = pName;
        mCost = pCost;
        mWeight = pWeight;
        mIngredients = pIngredients;
    }

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

    public void setCurrency(final String pCurrency) {
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

    @Override
    public String toString() {
        final String formatter = "%s %.2f %s Ingredients:\n%s";

        return String.format(Locale.US, formatter, mName, mCost, mWeight, getIngredientsAsString());
    }

    public String getIngredientsAsString() {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        final String splitter = StringUtils.COMMA + StringUtils.SPACE;

        for (final String ingredient : mIngredients) {
            builder.append(ingredient);
            builder.append((index != mIngredients.length - 1) ? splitter : StringUtils.EMPTY);
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

        public int getUserEstimation() {
            return mUserEstimation;
        }

        public void setUserEstimation(final int pEstimation) throws IllegalArgumentException {
            if (!(pEstimation >= 1 && pEstimation <= MAX_ESTIMATION)) {
                throw new IllegalArgumentException("pEstimation must be >= 1 and <= " + MAX_ESTIMATION);
            }

            mUserEstimation = pEstimation;
        }

        public float getAverageEstimation() {
            return mAverageEstimation;
        }

        public void setAverageEstimation(final float pAverageEstimation) {
            mAverageEstimation = pAverageEstimation;
        }
    }
}
