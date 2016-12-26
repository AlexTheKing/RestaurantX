package com.example.alex.restaurantx.ui.activities;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockApplication;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.ListDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.IngredientViewHolder;
import com.example.alex.restaurantx.systems.DataManager;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //NAVIGATION DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_settings_ingredients);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawer));
        //MENU
        TextView textView = (TextView) findViewById(R.id.settings_header);
        textView.setText(getString(R.string.manage_allergic_ingredients));
        mRecyclerView = (RecyclerView) findViewById(R.id.settings_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView("");
    }


    private void upgradeRecyclerView(final String pQuery) {
        ((CoreApplication) getApplication()).mDatabaseHelper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor pCursor) {
                final List<String> allIngredients = new ArrayList<>();
                while (pCursor.moveToNext()) {
                    int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                    final String[] ingredientsAsArray = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(", ");
                    for (String ingredient : ingredientsAsArray) {
                        boolean equals = false;
                        for (String finalIngredient : allIngredients) {
                            if (finalIngredient.equalsIgnoreCase(ingredient)) {
                                equals = true;
                                break;
                            }
                        }
                        if (!equals) {
                            String capitalized = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
                            if (TextUtils.isEmpty(pQuery) || capitalized.toUpperCase().startsWith(pQuery.toUpperCase())) {
                                allIngredients.add(capitalized);
                            }
                        }
                    }
                }
                mRecyclerView.setAdapter(new ListDataAdapter<>(allIngredients, IngredientViewHolder.getListHelper(SettingsActivity.this), R.layout.item_settings_ingredient));
            }

            @Override
            public void onError(Exception e) {
            }
        }, DataManager.UNIVERSAL_QUANTIFICATOR, DishModel.class, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        //Uses reflection :c
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            //Set the cursor resource ID to @null and it'll change its color to text color
            mCursorDrawableRes.set(searchEditText, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                upgradeRecyclerView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                upgradeRecyclerView(input);
                return true;
            }
        });
        return true;
    }
}
