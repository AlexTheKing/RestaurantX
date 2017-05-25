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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.CursorDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.TypeViewHolder;
import com.example.alex.restaurantx.service.UpdateService;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;
import com.example.alex.restaurantx.util.DialogHelper;
import com.example.alex.restaurantx.util.SlidingAnimationUtils;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BroadcastReceiver mReceiver;
    private ImageView mSpinner;
    private RotateAnimation mAnimation;
    private DialogHelper mDialogHelper;

    //THREE DOTS MENU
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.threedots_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Refresh:
                startService(new Intent(this, UpdateService.class));
                mSpinner.setAnimation(mAnimation);
                mAnimation.start();
                mSpinner.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.updating, Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialogHelper = new DialogHelper(this);
        setupProgressBar();
        final Toolbar toolbar = setupToolbar();
        setupNavigationDrawer(toolbar);
        setupRecyclerView();
        //UPDATE LIST BROADCAST RECEIVER
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context pContext, final Intent pIntent) {
                if (pIntent.getAction().equalsIgnoreCase(Constants.INTENT_EXTRA_ACTION_UPDATE)) {
                    hideSpinner();

                    if (pIntent.getBooleanExtra(Constants.ACTION_UPDATE_BROADCAST, false)) {
                        upgradeRecyclerView();
                    } else {
                        mDialogHelper.showNoInternetDialog();
                    }
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

    private void hideSpinner() {
        if (mSpinner.getAnimation() != null) {
            mSpinner.getAnimation().cancel();
        }

        mSpinner.setAnimation(null);
        mSpinner.setVisibility(View.GONE);
    }

    private void setupProgressBar() {
        mSpinner = (ImageView) findViewById(R.id.spinner);
        mAnimation = SlidingAnimationUtils.getAnimation();
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
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
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
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_type_list_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView();
    }

    private void upgradeRecyclerView() {
        final CoreApplication application = ((CoreApplication) getApplication());
        final DatabaseHelper databaseHelper = application.getDatabaseHelper();
        final String selectQuery = String.format(DatabaseHelper.SQLTemplates.SQL_DISTINCT, DishModel.TYPE);
        databaseHelper.query(new IResultCallback<Cursor>() {

            @Override
            public void onSuccess(final Cursor pCursor) {
                final CursorDataAdapter<TypeViewHolder> adapter = new CursorDataAdapter<>(pCursor, TypeViewHolder.getCursorBinder(MainActivity.this), R.layout.item_typelist);

                if (adapter.getItemCount() != 0) {
                    final Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slider);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    final View emptyListView = findViewById(android.R.id.empty);
                    final Animation.AnimationListener listener = SlidingAnimationUtils.getCustomAnimationListener(new Runnable() {

                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(adapter);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            emptyListView.setVisibility(View.GONE);
                        }
                    });
                    animation.setAnimationListener(listener);
                    emptyListView.startAnimation(animation);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(final Exception e) {
                mDialogHelper.showCleanCacheDialog();
            }
        }, selectQuery, DishModel.class, null);
    }
}













