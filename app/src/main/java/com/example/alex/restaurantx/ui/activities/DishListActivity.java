package com.example.alex.restaurantx.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
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
import com.example.alex.restaurantx.adapter.CursorDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.DishViewHolder;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;
import com.example.alex.restaurantx.util.DialogHelper;
import com.example.alex.restaurantx.util.SearchUtils;
import com.example.alex.restaurantx.util.StringUtils;

public class DishListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BroadcastReceiver mReceiver;
    private String mDishType;
    private DialogHelper mDialogHelper;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);
        setupSearchButton(menu);

        return true;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);
        mDialogHelper = new DialogHelper(this);
        mDishType = getIntent().getStringExtra(Constants.INTENT_EXTRA_TYPE);
        final TextView textView = (TextView) findViewById(R.id.type_dishes_header);
        textView.setText(mDishType);
        final Toolbar toolbar = setupToolbar();
        setupNavigationDrawer(toolbar);
        setupRecyclerView();
        //UPDATE LIST BROADCAST RECEIVER
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context pContext, final Intent pIntent) {
                if (pIntent.getBooleanExtra(Constants.ACTION_UPDATE_BROADCAST, false)) {
                    upgradeRecyclerView(null);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.INTENT_EXTRA_ACTION_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_dish_list);
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

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_dish_list_recycleview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView(null);
    }

    private void upgradeRecyclerView(final String pTemplateQuery) {
        final String queryTemplate = "%s, %s, %s, %s, %s";
        final String conditionTemplate = "%s = ?";
        final String selectQuery = String.format(
                queryTemplate,
                DishModel.NAME,
                DishModel.COST,
                DishModel.CURRENCY,
                DishModel.AVERAGE_ESTIMATION,
                DishModel.INGREDIENTS);
        String selectCondition = String.format(conditionTemplate, DishModel.TYPE);
        final IResultCallback<Cursor> callback = new IResultCallback<Cursor>() {

            @Override
            public void onSuccess(final Cursor pCursor) {
                mRecyclerView.setAdapter(new CursorDataAdapter<>(pCursor, DishViewHolder.getCursorHelper(DishListActivity.this), R.layout.item_dishlist));
            }

            @Override
            public void onError(final Exception e) {
                mDialogHelper.showCleanCacheDialog();
            }
        };
        final CoreApplication application = ((CoreApplication) getApplication());
        final DatabaseHelper helper = application.getDatabaseHelper();

        if (pTemplateQuery != null && !TextUtils.isEmpty(pTemplateQuery)) {
            final String andTemplate = " AND UPPER(%s) LIKE UPPER(?)";
            selectCondition += String.format(andTemplate, DishModel.NAME);
            final String searchQuery = pTemplateQuery + StringUtils.PERCENT;
            helper.query(callback,
                    selectQuery,
                    DishModel.class,
                    selectCondition,
                    DatabaseHelper.getSqlStringInterpret(mDishType),
                    DatabaseHelper.getSqlStringInterpret(searchQuery));
        } else {
            helper.query(callback,
                    selectQuery,
                    DishModel.class,
                    selectCondition,
                    DatabaseHelper.getSqlStringInterpret(mDishType));
        }
    }
}
