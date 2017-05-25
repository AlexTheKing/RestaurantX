package com.example.alex.restaurantx.holders.viewholders;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterBinder;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.ui.activities.DishListActivity;
import com.example.alex.restaurantx.util.SlidingAnimationUtils;

public final class TypeViewHolder extends RecyclerView.ViewHolder {

    private final TextView mName;

    private TypeViewHolder(final View pView) {
        super(pView);

        mName = ((TextView) pView.findViewById(R.id.type_list_item_name));
    }

    public static IAdapterBinder<TypeViewHolder, Cursor> getCursorBinder(final Context pContext) {
        return new IAdapterBinder<TypeViewHolder, Cursor>() {

            @Override
            public void onBindViewHolder(final Cursor pCursor, final TypeViewHolder pHolder, final int pPosition) {
                pCursor.moveToPosition(pPosition);
                final int indexTypeColumn = pCursor.getColumnIndex(DishModel.TYPE);
                final String type = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexTypeColumn));
                pHolder.mName.setText(type);
                pHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View pView) {
                        final Animation animation = AnimationUtils.loadAnimation(pContext, R.anim.slider);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        final Intent intent = new Intent(pContext, DishListActivity.class);
                        intent.putExtra(Constants.INTENT_EXTRA_DISH_TYPE, type);
                        pContext.startActivity(intent);
                        pHolder.itemView.startAnimation(animation);
                        final Animation.AnimationListener listener = SlidingAnimationUtils.getRedirectAnimationListener(pContext, intent);
                        animation.setAnimationListener(listener);
                    }
                });
            }

            @Override
            public TypeViewHolder onCreateViewHolder(final View pView) {
                return new TypeViewHolder(pView);
            }
        };
    }
}
