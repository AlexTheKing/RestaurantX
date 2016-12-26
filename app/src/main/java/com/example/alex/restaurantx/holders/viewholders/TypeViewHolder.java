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
import com.example.alex.restaurantx.adapter.IAdapterHelper;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.ui.activities.DishListActivity;

public class TypeViewHolder extends RecyclerView.ViewHolder {

    private final TextView mName;

    private TypeViewHolder(final View pView) {
        super(pView);
        mName = ((TextView) pView.findViewById(R.id.type_list_item_name));
    }

    public static IAdapterHelper<TypeViewHolder, Cursor> getCursorHelper(final Context pContext) {
        return new IAdapterHelper<TypeViewHolder, Cursor>() {
            @Override
            public void OnBindViewHolder(final Cursor pCursor, final TypeViewHolder pHolder, final int pPosition) {
                pCursor.moveToPosition(pPosition);
                final int indexTypeColumn = pCursor.getColumnIndex(DishModel.TYPE);
                final String type = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexTypeColumn));
                pHolder.mName.setText(type);
                pHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View pView) {
                        Animation animation = AnimationUtils.loadAnimation(pContext, R.anim.slider);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        pHolder.itemView.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation pAnimation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation pAnimation) {
                                final Intent intent = new Intent(pContext, DishListActivity.class);
                                intent.putExtra(Constants.INTENT_EXTRA_TYPE, type);
                                pContext.startActivity(intent);
                            }

                            @Override
                            public void onAnimationRepeat(Animation pAnimation) {
                            }
                        });
                    }
                });
            }

            @Override
            public TypeViewHolder build(View pView) {
                return new TypeViewHolder(pView);
            }
        };
    }
}
