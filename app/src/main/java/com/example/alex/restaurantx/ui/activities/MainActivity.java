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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockApplication;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.holders.viewholders.TypeViewHolder;
import com.example.alex.restaurantx.service.UpdateService;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BroadcastReceiver mReceiver;
    private ImageView mSpinner;
    private RotateAnimation mAnimation;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PROGRESS BAR
        mSpinner = (ImageView) findViewById(R.id.spinner);
        mAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(600);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setInterpolator(new LinearInterpolator());
        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //NAVIGATION DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawer));
        //RECYCLER VIEW
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_type_list_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView();
        //UPDATE LIST
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context pContext, Intent pIntent) {
                if (pIntent.getAction().equalsIgnoreCase(Constants.INTENT_SERVICE_UPDATE_ACTION)) {
                    if (pIntent.getBooleanExtra(Constants.BROADCAST_UPDATE_MESSAGE, false)) {
                        if (mSpinner.getAnimation() != null) {
                            mSpinner.getAnimation().cancel();
                        }
                        mSpinner.setAnimation(null);
                        mSpinner.setVisibility(View.GONE);
                        upgradeRecyclerView();
                    } else {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setTitle(R.string.problem_occurred);
                        dlgAlert.setMessage(R.string.checkout_internet);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        if (mSpinner.getAnimation() != null) {
                            mSpinner.getAnimation().cancel();
                        }
                        mSpinner.setAnimation(null);
                        mSpinner.setVisibility(View.GONE);
                    }
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
        ((CoreApplication) getApplication()).mDatabaseHelper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pCursor) {
                final CursorDataAdapter<TypeViewHolder> adapter = new CursorDataAdapter<>(pCursor, TypeViewHolder.getCursorHelper(MainActivity.this), R.layout.item_typelist);
                if (adapter.getItemCount() != 0) {
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slider);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    final View emptyListView = findViewById(android.R.id.empty);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation pAnimation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation pAnimation) {
                            mRecyclerView.setAdapter(adapter);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            emptyListView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation pAnimation) {
                        }
                    });
                    emptyListView.startAnimation(animation);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
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
                startService(new Intent(this, UpdateService.class));
                mSpinner.setAnimation(mAnimation);
                mAnimation.start();
                mSpinner.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}













