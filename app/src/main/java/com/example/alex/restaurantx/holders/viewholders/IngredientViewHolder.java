package com.example.alex.restaurantx.holders.viewholders;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterBinder;
import com.example.alex.restaurantx.constants.Constants;

import java.util.List;

public final class IngredientViewHolder extends RecyclerView.ViewHolder {

    private final SwitchCompat mSwitch;

    private IngredientViewHolder(final View view) {
        super(view);

        mSwitch = (SwitchCompat) view.findViewById(R.id.item_settings_ingredient_switch);
    }

    public static IAdapterBinder<IngredientViewHolder, List<String>> getListHelper(final Context pContext) {
        return new IAdapterBinder<IngredientViewHolder, List<String>>() {

            @Override
            public void onBindViewHolder(final List<String> pStrings, final IngredientViewHolder pHolder, final int pPosition) {
                final String ingredient = pStrings.get(pPosition);
                pHolder.mSwitch.setText(ingredient);
                final SharedPreferences sharedPreferences = pContext.getSharedPreferences(Constants.SETTINGS_FILENAME, Context.MODE_PRIVATE);

                if (sharedPreferences.getBoolean(ingredient, false)) {
                    pHolder.mSwitch.setChecked(true);
                }

                pHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(final CompoundButton pCompoundButton, final boolean isChecked) {

                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(ingredient, isChecked);
                        editor.apply();
                    }
                });
            }

            @Override
            public IngredientViewHolder onCreateViewHolder(final View view) {
                return new IngredientViewHolder(view);
            }
        };
    }

}
