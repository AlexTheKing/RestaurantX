package com.example.alex.restaurantx.ui.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.ListDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.CommentViewHolder;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;
import com.google.android.gms.iid.InstanceID;

import java.util.List;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        final String dishNameFromIntent = getIntent().getStringExtra(Constants.INTENT_EXTRA_DISH_NAME);
        final boolean haveProhibited = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_HAVE_PROHIBITED, false);
        final CoreApplication application = ((CoreApplication) getApplication());
        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        //NAVIGATION DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_dish_info);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener(this, drawer));
        //LOAD COMMENTS CALLBACK
        final RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.dish_comments));
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final ScrollView scrollView = ((ScrollView) findViewById(R.id.dish_info_main_scroll));
        final IResultCallback<String> loadCommentsCallback = new IResultCallback<String>() {
            @Override
            public void onSuccess(String pResponse) {

                application.mJsonHandler.parseComments(pResponse, new IResultCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> pStrings) {
                        recyclerView.setAdapter(new ListDataAdapter<>(pStrings, CommentViewHolder.getListHelper(scrollView), R.layout.item_comment));
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(DishInfoActivity.this);
                dlgAlert.setTitle(R.string.problem_occurred);
                dlgAlert.setCancelable(true);
                dlgAlert.setMessage(R.string.checkout_internet);
                dlgAlert.create().show();
            }
        };
        //EDIT TEXT ADD COMMENT
        EditText editText = (EditText) findViewById(R.id.dish_edit_comment);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    final String instanceId = InstanceID.getInstance(DishInfoActivity.this).getId();
                    application.mApiManager.setCommentMethod(dishNameFromIntent, instanceId, v.getText().toString(), loadCommentsCallback);
                    v.setText("");
                    handled = true;
                }
                return handled;
            }
        });
        //LOAD DISH
        loadDishFromDatabase(dishNameFromIntent, haveProhibited);
        //LOAD COMMENTS
        application.mApiManager.getCommentsMethod(dishNameFromIntent, loadCommentsCallback);
    }

    private void loadDishFromDatabase(String pDishNameFromIntent, final boolean pHaveProhibited) {
        final String WHERE_CLAUSE = DishModel.NAME + " = ?";
        final TextView dishName = (TextView) findViewById(R.id.dish_name);
        dishName.setText(pDishNameFromIntent);
        final TextView dishType = (TextView) findViewById(R.id.dish_type);
        final TextView dishWeight = (TextView) findViewById(R.id.dish_weight);
        final TextView dishCost = (TextView) findViewById(R.id.dish_cost);
        final TextView dishDescription = (TextView) findViewById(R.id.dish_description);
        final TextView dishIngredients = (TextView) findViewById(R.id.dish_ingredients);
        final RatingBar dishUserRating = (RatingBar) findViewById(R.id.dish_user_rating);
        LayerDrawable stars = ((LayerDrawable) dishUserRating.getProgressDrawable());
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.colorDarkGrey), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
        final TextView dishAverageRating = (TextView) findViewById(R.id.dish_average_rating);
        final ImageView dishImage = (ImageView) findViewById(R.id.dish_image_full);
        final CoreApplication application = ((CoreApplication) getApplication());
        final DataManager dataManager = new DataManager(application.mDatabaseHelper);
        final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(DishInfoActivity.this);
        dlgAlert.setTitle(R.string.problem_occurred);
        dlgAlert.setCancelable(true);
        dataManager.loadDishes(new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(final List<Dish> pDishes) {
                final Dish dish = pDishes.get(0);
                dishType.setText(dish.getType());
                dishWeight.setText(dish.getWeight());
                dishCost.setText(String.format("%.2f" + dish.getCurrency(), dish.getCost()));
                dishDescription.setText(dish.getDescription());
                dishIngredients.setText(String.format(getString(R.string.ingredients_string), dish.getIngredientsAsString()));
                if (pHaveProhibited) {
                    dishIngredients.setTextColor(Color.RED);
                }
                dishUserRating.setRating(dish.getVote().getUserEstimation());
                dishAverageRating.setText(String.valueOf(dish.getVote().getAverageEstimation()));
                ((CoreApplication) getApplication()).mImageLoader.downloadAndDraw(dish.getBitmapUrl(), dishImage, null);
                dishUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(final RatingBar pRatingBar, final float pEstimation, final boolean pIsFromUser) {
                        if (pIsFromUser) {
                            final String instanceId = InstanceID.getInstance(DishInfoActivity.this).getId();
                            application.mApiManager.setRateMethod(dishName.getText().toString(), instanceId, ((int) pEstimation), new IResultCallback<String>() {
                                @Override
                                public void onSuccess(String pS) {
                                    try {
                                        final float average = Float.parseFloat(pS);
                                        dishAverageRating.setText(pS);
                                        dish.getVote().setUserEstimation(((int) pEstimation));
                                        dataManager.updateDish(dish, new IResultCallback<Integer>() {
                                            @Override
                                            public void onSuccess(Integer pInteger) {
                                                Toast.makeText(DishInfoActivity.this, "Rating is saved!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                dlgAlert.setMessage(R.string.problem_but_sent);
                                                dlgAlert.create().show();
                                            }
                                        });
                                    } catch (NumberFormatException ex) {
                                        onError(ex);
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    e.printStackTrace();
                                    dlgAlert.setMessage(R.string.checkout_internet);
                                    dlgAlert.create().show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                e.printStackTrace();
                dlgAlert.setMessage(R.string.clean_cache);
                dlgAlert.create().show();
            }

        }, WHERE_CLAUSE, DatabaseHelper.getSqlStringInterpret(pDishNameFromIntent));
    }
}
