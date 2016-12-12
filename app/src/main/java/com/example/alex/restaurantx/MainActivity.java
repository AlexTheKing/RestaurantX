package com.example.alex.restaurantx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alex.restaurantx.adapter.CursorDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.holders.TypeViewHolder;
import com.example.alex.restaurantx.service.UpdateService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String mResponseTypesJson = "{\n" +
            "  \"response\": {\n" +
            "    types: [\n" +
            "        \"Cold Snacks\",\n" +
            "        \"Hot Appetizers\",\n" +
            "        \"Meat Dishes\"\n" +
            "      ]\n" +
            "  }\n" +
            "}";

    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawer;
    private BroadcastReceiver mReceiver;
    private boolean mIsServiceEnded = true;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //NAVIGATION DRAWER
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //CONTEXT HOLDER
        //TODO : PLACE CONTEXTHOLDER IN PROPER PLACE
        ContextHolder.getInstance().setContext(getApplicationContext());
        //RECYCLER VIEW
        mRecyclerView = (RecyclerView) findViewById(R.id.typelist);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView();
        //UPDATE LIST
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context pContext, Intent pIntent) {
                //TODO : CHECK FOR UPGRADING
                if(pIntent.getBooleanExtra(Constants.BROADCAST_UPDATE_MESSAGE, false)){
                    mIsServiceEnded = true;
                    upgradeRecyclerView();
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

    private void upgradeRecyclerView() {
        final String selectQuery = "DISTINCT " + DishModel.TYPE;
        DatabaseHelper.getInstance(this, DatabaseHelper.CURRENT_VERSION).query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pCursor) {
                mRecyclerView.setAdapter(new CursorDataAdapter<>(pCursor, TypeViewHolder.getCursorHelper(MainActivity.this), R.layout.item_typelist));
            }

            @Override
            public void onError(Exception e) {
            }
        }, selectQuery, DishModel.class, null);
    }

    //THREE DOTS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.threedots_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Refresh:
                //TODO : START SERVICE FOR UPDATING
                if(mIsServiceEnded) {
                    startService(new Intent(this, UpdateService.class));
                    mIsServiceEnded = false;
                } else {
                    Toast.makeText(this, "Already updating...", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //NAVIGATION VIEW MENU
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean result = false;
        switch (item.getItemId()) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                result = true;
                break;
            case R.id.nav_help:
                Intent helpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_repo_readme)));
                startActivity(helpIntent);
                result = true;
                break;
            case R.id.nav_opensource:
                Intent repoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_repo)));
                startActivity(repoIntent);
                result = true;
                break;
            case R.id.nav_about:
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setTitle(getString(R.string.about));
                dlgAlert.setMessage(getString(R.string.about_info));
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                mDrawer.closeDrawer(GravityCompat.START);
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        item.setChecked(false);
        return result;
    }
}













