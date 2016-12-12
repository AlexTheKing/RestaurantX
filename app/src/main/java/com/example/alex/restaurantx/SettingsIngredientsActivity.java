package com.example.alex.restaurantx;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.alex.restaurantx.adapter.CursorDataAdapter;
import com.example.alex.restaurantx.adapter.ListDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.IngredientViewHolder;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsIngredientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_ingredients);
        TextView textView = (TextView) findViewById(R.id.settings_ingredients_header);
        textView.setText(getString(R.string.manage_allergic_ingredients));
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.settings_ingredients_recyclerview);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DatabaseHelper.getInstance(this, DatabaseHelper.CURRENT_VERSION).query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor pCursor) {
                final List<String> allIngredients = new ArrayList<>();
                while(pCursor.moveToNext()) {
                    int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                    final String[] ingredientsAsArray = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(", ");
                    for (String ingredient : ingredientsAsArray) {
                        boolean equals = false;
                        for (String finalIngredient : allIngredients) {
                            if(finalIngredient.equalsIgnoreCase(ingredient)){
                                equals = true;
                                break;
                            }
                        }
                        if(!equals){
                            String capitalized = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
                            allIngredients.add(capitalized);
                        }
                    }
                }
                recyclerView.setAdapter(new ListDataAdapter<>(allIngredients, IngredientViewHolder.getListHelper(SettingsIngredientsActivity.this), R.layout.item_settings_ingredient));
            }

            @Override
            public void onError(Exception e) { }
        }, DataManager.UNIVERSAL_QUANTIFICATOR, DishModel.class, null);
    }
}
