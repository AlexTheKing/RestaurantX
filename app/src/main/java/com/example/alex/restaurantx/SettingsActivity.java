package com.example.alex.restaurantx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView manageIngredients = (TextView) findViewById(R.id.settings_ingredients);
        manageIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                Intent intent = new Intent(SettingsActivity.this, SettingsIngredientsActivity.class);
                startActivity(intent);
            }
        });
        TextView defaultCurrency = (TextView) findViewById(R.id.settings_default_currency);
        defaultCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                // TODO : IMPLEMENT CURRENCY
            }
        });
    }
}
