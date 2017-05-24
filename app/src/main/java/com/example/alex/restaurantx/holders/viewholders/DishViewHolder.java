package com.example.alex.restaurantx.holders.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterBinder;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.ui.activities.DishInfoActivity;
import com.example.alex.restaurantx.util.SlidingAnimationUtils;
import com.example.alex.restaurantx.util.StringUtils;

import java.util.Locale;

public final class DishViewHolder extends RecyclerView.ViewHolder {

    private static final String INGREDIENTS_SPLITTER = StringUtils.COMMA + StringUtils.SPACE;
    private static final String COST_FORMATTER = "%.2f %s";
    private final TextView mName;
    private final TextView mCost;
    private final TextView mProhibited;
    private final RatingBar mAverageRating;

    private DishViewHolder(final View pView) {
        super(pView);

        mName = ((TextView) pView.findViewById(R.id.dish_list_item_name));
        mProhibited = ((TextView) pView.findViewById(R.id.dish_list_item_prohibited));
        mCost = ((TextView) pView.findViewById(R.id.dish_list_item_cost));
        mAverageRating = ((RatingBar) pView.findViewById(R.id.dish_list_item_average_rating));
    }

    public static IAdapterBinder<DishViewHolder, Cursor> getCursorHelper(final Context pContext) {
        return new IAdapterBinder<DishViewHolder, Cursor>() {

            @Override
            public void onBindViewHolder(final Cursor pCursor, final DishViewHolder pHolder, final int pPosition) {
                pCursor.moveToPosition(pPosition);
                final int indexNameColumn = pCursor.getColumnIndex(DishModel.NAME);
                final int indexCostColumn = pCursor.getColumnIndex(DishModel.COST);
                final int indexCurrencyColumn = pCursor.getColumnIndex(DishModel.CURRENCY);
                final int indexAverageEstimationColumn = pCursor.getColumnIndex(DishModel.AVERAGE_ESTIMATION);
                final int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                final String dishName = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexNameColumn));
                final String[] ingredientsAsStr = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(INGREDIENTS_SPLITTER);
                final SharedPreferences sharedPreferences = pContext.getSharedPreferences(Constants.SETTINGS_FILENAME, Context.MODE_PRIVATE);
                boolean haveProhibited = false;

                for (final String ingredient : ingredientsAsStr) {
                    final String capitalized = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);

                    if (sharedPreferences.getBoolean(capitalized, false)) {
                        pHolder.mName.setTextColor(Color.RED);
                        pHolder.mProhibited.setText(String.format(pContext.getString(R.string.contains), ingredient));
                        haveProhibited = true;

                        break;
                    }
                }

                if (!haveProhibited) {
                    pHolder.mProhibited.setVisibility(View.GONE);
                }

                pHolder.mName.setText(dishName);
                final String currency = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexCurrencyColumn));
                final String costFormatted = String.format(Locale.US, COST_FORMATTER, pCursor.getFloat(indexCostColumn), currency);
                pHolder.mCost.setText(costFormatted);
                pHolder.mAverageRating.setRating(pCursor.getFloat(indexAverageEstimationColumn));
                final LayerDrawable stars = (LayerDrawable) pHolder.mAverageRating.getProgressDrawable();
                stars.getDrawable(1).setColorFilter(ContextCompat.getColor(pContext, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(pContext, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
                final boolean finalHaveProhibited = haveProhibited;
                pHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View pView) {
                        final Animation animation = AnimationUtils.loadAnimation(pContext, R.anim.slider);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        final Intent intent = new Intent(pContext, DishInfoActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_DISH_NAME, dishName);
                        intent.putExtra(Constants.INTENT_EXTRA_HAVE_PROHIBITED, finalHaveProhibited);
                        final Animation.AnimationListener listener = SlidingAnimationUtils.getRedirectAnimationListener(pContext, intent);
                        animation.setAnimationListener(listener);
                        pHolder.itemView.startAnimation(animation);
                    }
                });
            }

            @Override
            public DishViewHolder onCreateViewHolder(final View view) {
                return new DishViewHolder(view);
            }
        };
    }
}
