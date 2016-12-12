package com.example.alex.restaurantx.holders;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alex.restaurantx.DishInfoActivity;
import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterHelper;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;

public class DishViewHolder extends RecyclerView.ViewHolder {

    private final TextView mName;
    private final TextView mCost;
    private final TextView mProhibited;
    private final RatingBar mAverageRating;

    private DishViewHolder(final View pView) {
        super(pView);
        mName = ((TextView) pView.findViewById(R.id.dishlist_item_name));
        mProhibited = ((TextView) pView.findViewById(R.id.dishlist_item_prohibited));
        mCost = ((TextView) pView.findViewById(R.id.dishlist_item_cost));
        mAverageRating = ((RatingBar) pView.findViewById(R.id.dishlist_item_average_rating));
    }

    public static IAdapterHelper<DishViewHolder, Cursor> getCursorHelper(final Context pContext){
        return new IAdapterHelper<DishViewHolder, Cursor>() {
            @Override
            public void OnBindViewHolder(final Cursor pCursor, final DishViewHolder pHolder, final int pPosition) {
                pCursor.moveToPosition(pPosition);
                final int indexNameColumn = pCursor.getColumnIndex(DishModel.NAME);
                final int indexCostColumn = pCursor.getColumnIndex(DishModel.COST);
                final int indexCurrencyColumn = pCursor.getColumnIndex(DishModel.CURRENCY);
                final int indexAverageEstimationColumn = pCursor.getColumnIndex(DishModel.AVERAGE_ESTIMATION);
                final int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                final String dishName = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexNameColumn));
                final String[] ingredientsAsStr = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(", ");
                SharedPreferences sharedPreferences = pContext.getSharedPreferences(Constants.SETTINGS_FILENAME, Context.MODE_PRIVATE);
                boolean haveProhibited = false;
                for (String ingredient : ingredientsAsStr) {
                    String capitalized = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
                    if (sharedPreferences.getBoolean(capitalized, false)) {
                        pHolder.mName.setTextColor(Color.RED);
                        pHolder.mProhibited.setText(String.format(pContext.getString(R.string.contains), ingredient));
                        haveProhibited = true;
                        break;
                    }
                }
                if(!haveProhibited){
                    pHolder.mProhibited.setVisibility(View.GONE);
                }
                pHolder.mName.setText(dishName);
                final String currency = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexCurrencyColumn));
                pHolder.mCost.setText(String.format("%s"+currency, String.valueOf(pCursor.getFloat(indexCostColumn))));
                pHolder.mAverageRating.setRating(pCursor.getFloat(indexAverageEstimationColumn));
                final boolean finalHaveProhibited = haveProhibited;
                pHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View pView) {
                        Animation animation = AnimationUtils.loadAnimation(ContextHolder.getInstance().getContext(), R.anim.slider);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        pHolder.itemView.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation pAnimation) { }

                            @Override
                            public void onAnimationEnd(Animation pAnimation) {
                                final Intent intent = new Intent(pContext, DishInfoActivity.class);
                                intent.putExtra(Constants.INTENT_EXTRA_DISHNAME, dishName);
                                intent.putExtra(Constants.INTENT_EXTRA_HAVE_PROHIBITED, finalHaveProhibited);
                                pContext.startActivity(intent);
                            }

                            @Override
                            public void onAnimationRepeat(Animation pAnimation) { }
                        });
                    }
                });
            }

            @Override
            public DishViewHolder build(View view) {
                return new DishViewHolder(view);
            }
        };
    }
}
