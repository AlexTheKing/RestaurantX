package com.example.alex.restaurantx.model;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.systems.RecommenderSystem;

import java.util.List;

public class Dish {

    private String mName;
    private String mWeight;
    private String mType;
    private int mCost;
    private String mDescription;
    private List<String> mIngredients;
    private final Vote mVote = new Vote();
    private String mBitmapUrl;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String pDescription) {
        mDescription = pDescription;
    }

    public String getType() {
        return mType;
    }

    public void setType(String pType) {
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

    public int getCost() {
        return mCost;
    }

    public String getBitmapUrl() {
        return mBitmapUrl;
    }

    public void setBitmapUrl(String pBitmapUrl) {
        mBitmapUrl = pBitmapUrl;
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
        return base + getIngredientsAsString();
    }

    public String getIngredientsAsString() {
        StringBuilder builder = new StringBuilder();
        for (String ingredient : mIngredients) {
            builder.append(ingredient + ((mIngredients.indexOf(ingredient) != mIngredients.size() - 1) ? ", " : ""));
        }
        return builder.toString();
    }

    public ContentValues convert() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DishModel.ID, "NULL");
        contentValues.put(DishModel.NAME, mName);
        contentValues.put(DishModel.TYPE, mType);
        contentValues.put(DishModel.WEIGHT, mWeight);
        contentValues.put(DishModel.COST, mCost);
        contentValues.put(DishModel.DESCRIPTION, mDescription);
        contentValues.put(DishModel.INGREDIENTS, getIngredientsAsString());
        contentValues.put(DishModel.USER_ESTIMATION, mVote.getUserEstimation());
        contentValues.put(DishModel.AVERAGE_ESTIMATION, mVote.getAverageEstimation());
        contentValues.put(DishModel.BITMAP_URL, mBitmapUrl);
        return contentValues;
    }

    public class Vote {

        private final int MAX_ESTIMATION = 5;
        private int mUserEstimation;
        private float mAverageEstimation;

        public void setUserEstimation(int pEstimation) throws IllegalArgumentException {
            if (!(pEstimation >= 1 && pEstimation <= MAX_ESTIMATION)) {
                throw new IllegalArgumentException("pEstimation must be >= 1 and <= " + MAX_ESTIMATION);
            }
            mUserEstimation = pEstimation;
            RecommenderSystem.getInstance().updateVoteForDish(Dish.this);
        }

        public int getUserEstimation() {
            return mUserEstimation;
        }

        public float getAverageEstimation() {
            return mAverageEstimation;
        }

        public void setAverageEstimation(float pAverageEstimation) {
            mAverageEstimation = pAverageEstimation;
        }
    }
}
