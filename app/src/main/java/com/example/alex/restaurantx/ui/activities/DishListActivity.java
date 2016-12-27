package com.example.alex.restaurantx.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import java.lang.reflect.Field;

public class DishListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BroadcastReceiver mReceiver;
    private String mDishType;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);
        mDishType = getIntent().getStringExtra(Constants.INTENT_EXTRA_TYPE);
        final TextView textView = (TextView) findViewById(R.id.type_dishes_header);
        textView.setText(mDishType);
        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //NAVIGATION DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_dish_list);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawer));
        //RECYCLER VIEW
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_dish_list_recycleview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView(null);
        //UPDATE LIST
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context pContext, Intent pIntent) {
                if (pIntent.getBooleanExtra(Constants.BROADCAST_UPDATE_MESSAGE, false)) {
                    upgradeRecyclerView(null);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.INTENT_SERVICE_UPDATE_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void upgradeRecyclerView(String templateQuery) {
        final String selectQuery = DishModel.NAME + ", " + DishModel.COST + ", " + DishModel.CURRENCY + ", " + DishModel.AVERAGE_ESTIMATION + ", " + DishModel.INGREDIENTS;
        String selectCondition = DishModel.TYPE + " = ?";
        final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(R.string.problem_occurred);
        dlgAlert.setCancelable(true);
        IResultCallback<Cursor> callback = new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pCursor) {
                mRecyclerView.setAdapter(new CursorDataAdapter<>(pCursor, DishViewHolder.getCursorHelper(DishListActivity.this), R.layout.item_dishlist));
            }

            @Override
            public void onError(Exception e) {
                dlgAlert.setMessage(R.string.clean_cache);
                dlgAlert.create().show();
            }
        };
        final DatabaseHelper helper = ((CoreApplication) getApplication()).mDatabaseHelper;
        if (templateQuery != null && !TextUtils.isEmpty(templateQuery)) {
            selectCondition += " AND UPPER(" + DishModel.NAME + ") LIKE UPPER(?)";
            helper.query(callback, selectQuery, DishModel.class,
                    selectCondition, DatabaseHelper.getSqlStringInterpret(mDishType), DatabaseHelper.getSqlStringInterpret(templateQuery + "%"));
        } else {
            helper.query(callback, selectQuery, DishModel.class,
                    selectCondition, DatabaseHelper.getSqlStringInterpret(mDishType));
        }
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
