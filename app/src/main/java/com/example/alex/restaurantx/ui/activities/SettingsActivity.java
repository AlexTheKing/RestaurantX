package com.example.alex.restaurantx.ui.activities;

import android.database.Cursor;
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
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.ListDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.IngredientViewHolder;
import com.example.alex.restaurantx.systems.DataManager;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;
import com.example.alex.restaurantx.util.SearchUtils;
import com.example.alex.restaurantx.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private final String INGREDIENTS_SPLITTER = StringUtils.COMMA + StringUtils.SPACE;
    private RecyclerView mRecyclerView;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);
        setupSearchButton(menu);

        return true;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = setupToolbar();
        setupNavigationDrawer(toolbar);
        setupHeader();
        setupRecyclerView();
        upgradeRecyclerView(StringUtils.EMPTY);
    }

    private void setupSearchButton(final Menu menu) {
        SearchUtils.setupSearch(menu, new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                upgradeRecyclerView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String input) {
                upgradeRecyclerView(input);
                return true;
            }
        });
    }

    private Toolbar setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        return toolbar;
    }

    private void setupNavigationDrawer(final Toolbar pToolbar) {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_settings_ingredients);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                pToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawer));
    }

    private void setupHeader() {
        final TextView textView = (TextView) findViewById(R.id.settings_header);
        textView.setText(getString(R.string.manage_allergic_ingredients));
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.settings_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void upgradeRecyclerView(final String pQuery) {
        final CoreApplication application = ((CoreApplication) getApplication());
        final DatabaseHelper databaseHelper = application.getDatabaseHelper();
        databaseHelper.query(new IResultCallback<Cursor>() {

            @Override
            public void onSuccess(final Cursor pCursor) {
                final List<String> allIngredients = new ArrayList<>();

                while (pCursor.moveToNext()) {
                    final int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                    final String[] ingredientsAsArray = DatabaseHelper.getUsualStringInterpret(pCursor.getString(indexIngredientsColumn)).split(INGREDIENTS_SPLITTER);

                    for (final String ingredient : ingredientsAsArray) {
                        boolean equals = false;

                        for (final String finalIngredient : allIngredients) {
                            if (finalIngredient.equalsIgnoreCase(ingredient)) {
                                equals = true;

                                break;
                            }
                        }

                        if (!equals) {
                            final String capitalized = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);

                            if (TextUtils.isEmpty(pQuery) || capitalized.toUpperCase().startsWith(pQuery.toUpperCase())) {
                                allIngredients.add(capitalized);
                            }
                        }
                    }
                }

                mRecyclerView.setAdapter(new ListDataAdapter<>(allIngredients, IngredientViewHolder.getListBinder(SettingsActivity.this), R.layout.item_settings_ingredient));
            }

            @Override
            public void onError(final Exception e) {
            }
        }, DataManager.UNIVERSAL_QUANTIFICATOR, DishModel.class, null);
    }
}
