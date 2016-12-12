package com.example.alex.restaurantx.holders;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterHelper;
import com.example.alex.restaurantx.constants.Constants;

import java.util.List;

public class IngredientViewHolder extends RecyclerView.ViewHolder{

    private final Switch mSwitch;

    private IngredientViewHolder(View view){
        super(view);
        mSwitch = (Switch) view.findViewById(R.id.item_settings_ingredient_switch);
    }

    public static IAdapterHelper<IngredientViewHolder, List<String>> getListHelper(final Context pContext){
        return new IAdapterHelper<IngredientViewHolder, List<String>>() {
            @Override
            public void OnBindViewHolder(List<String> pStrings, IngredientViewHolder pHolder, int pPosition) {
                final String ingredient = pStrings.get(pPosition);
                pHolder.mSwitch.setText(ingredient);
                final SharedPreferences sharedPreferences = pContext.getSharedPreferences(Constants.SETTINGS_FILENAME, Context.MODE_PRIVATE);
                if(sharedPreferences.getBoolean(ingredient, false)){
                    pHolder.mSwitch.setChecked(true);
                }
                pHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton pCompoundButton, boolean isChecked) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(ingredient, isChecked);
                        editor.apply();
                    }
                });
            }

            @Override
            public IngredientViewHolder build(View view) {
                return new IngredientViewHolder(view);
            }
        };
    }

}
