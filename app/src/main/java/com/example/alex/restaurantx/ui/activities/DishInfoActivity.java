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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.viewholders.CommentViewHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;
import com.example.alex.restaurantx.ui.navigation.NavigationViewListener;
import com.example.alex.restaurantx.util.DialogHelper;
import com.example.alex.restaurantx.util.StringUtils;
import com.google.android.gms.iid.InstanceID;

import java.util.List;
import java.util.Locale;

public class DishInfoActivity extends AppCompatActivity {

    private ApiManager mApiManager;
    private JsonHandler mJsonHandler;
    private DatabaseHelper mDatabaseHelper;
    private ImageLoader mImageLoader;
    private final String TAG = this.getClass().getCanonicalName();
    private DialogHelper mDialogHelper;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        final CoreApplication application = ((CoreApplication) getApplication());
        mApiManager = application.getApiManager();
        mJsonHandler = application.getJsonHandler();
        mDatabaseHelper = application.getDatabaseHelper();
        mImageLoader = application.getImageLoader();
        mDialogHelper = new DialogHelper(this);
        final String dishNameFromIntent = getIntent().getStringExtra(Constants.INTENT_EXTRA_DISH_NAME);
        final boolean haveProhibited = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_HAVE_PROHIBITED, false);
        final Toolbar toolbar = setupToolbar();
        setupNavigationDrawer(toolbar);
        final IResultCallback<String> loadCommentsCallback = setupComments();
        setupCommentsActionListener(dishNameFromIntent, loadCommentsCallback);
        loadDishFromDatabase(dishNameFromIntent, haveProhibited);
        mApiManager.getCommentsMethod(dishNameFromIntent, loadCommentsCallback);
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
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_dish_info);
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

    private IResultCallback<String> setupComments() {
        final RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.dish_comments));
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final ScrollView scrollView = ((ScrollView) findViewById(R.id.dish_info_main_scroll));
        return new IResultCallback<String>() {

            @Override
            public void onSuccess(final String pResponse) {

                mJsonHandler.parseComments(pResponse, new IResultCallback<List<String>>() {

                    @Override
                    public void onSuccess(final List<String> pStrings) {
                        recyclerView.setAdapter(new ListDataAdapter<>(pStrings, CommentViewHolder.getListBinder(scrollView), R.layout.item_comment));
                    }

                    @Override
                    public void onError(final Exception e) {
                        Log.e(TAG, "onError: " + e.getMessage(), e);
                    }
                });

            }

            @Override
            public void onError(final Exception e) {
                mDialogHelper.showNoInternetDialog();
            }
        };
    }

    private void setupCommentsActionListener(final String dishName, final IResultCallback<String> pCallback) {
        final EditText editText = (EditText) findViewById(R.id.dish_edit_comment);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                boolean handled = false;

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    final String instanceId = InstanceID.getInstance(DishInfoActivity.this).getId();
                    mApiManager.addCommentMethod(dishName, instanceId, v.getText().toString(), pCallback);
                    v.setText(StringUtils.EMPTY);
                    handled = true;
                }

                return handled;
            }
        });
    }

    private void loadDishFromDatabase(final String pDishNameFromIntent, final boolean pHaveProhibited) {
        final String whereTemplate = "%s = ?";
        final String WHERE_CLAUSE = String.format(whereTemplate, DishModel.NAME);
        final TextView dishName = (TextView) findViewById(R.id.dish_name);
        dishName.setText(pDishNameFromIntent);
        final TextView dishType = (TextView) findViewById(R.id.dish_type);
        final TextView dishWeight = (TextView) findViewById(R.id.dish_weight);
        final TextView dishCost = (TextView) findViewById(R.id.dish_cost);
        final TextView dishDescription = (TextView) findViewById(R.id.dish_description);
        final TextView dishIngredients = (TextView) findViewById(R.id.dish_ingredients);
        final RatingBar dishUserRating = (RatingBar) findViewById(R.id.dish_user_rating);
        final LayerDrawable stars = ((LayerDrawable) dishUserRating.getProgressDrawable());
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.colorDarkGrey), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.colorLightGreen), PorterDuff.Mode.SRC_ATOP);
        final TextView dishAverageRating = (TextView) findViewById(R.id.dish_average_rating);
        final ImageView dishImage = (ImageView) findViewById(R.id.dish_image_full);
        final DataManager dataManager = new DataManager(mDatabaseHelper);

        dataManager.loadDishes(new IResultCallback<List<Dish>>() {

            @Override
            public void onSuccess(final List<Dish> pDishes) {
                final Dish dish = pDishes.get(0);
                dishType.setText(dish.getType());
                final String weightFormatter = "%sg";
                final String weightFormatted = String.format(Locale.US, weightFormatter, dish.getWeight());
                dishWeight.setText(weightFormatted);
                final String costFormatter = "%.2f %s";
                final String costFormatted = String.format(Locale.US, costFormatter, dish.getCost(), dish.getCurrency());
                dishCost.setText(costFormatted);
                dishDescription.setText(dish.getDescription());
                dishIngredients.setText(String.format(getString(R.string.ingredients_string), dish.getIngredientsAsString()));

                if (pHaveProhibited) {
                    dishIngredients.setTextColor(Color.RED);
                }

                dishUserRating.setRating(dish.getVote().getUserEstimation());
                dishAverageRating.setText(String.valueOf(dish.getVote().getAverageEstimation()));
                mImageLoader.downloadAndDraw(dish.getBitmapUrl(), dishImage, null);
                dishUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(final RatingBar pRatingBar, final float pEstimation, final boolean pIsFromUser) {
                        if (pIsFromUser) {
                            final String instanceId = InstanceID.getInstance(DishInfoActivity.this).getId();
                            mApiManager.addRateMethod(dishName.getText().toString(), instanceId, ((int) pEstimation), new IResultCallback<String>() {

                                @Override
                                public void onSuccess(final String pS) {
                                    try {
                                        dishAverageRating.setText(pS);
                                        dish.getVote().setUserEstimation(((int) pEstimation));
                                        dataManager.updateDish(dish, new IResultCallback<Integer>() {

                                            @Override
                                            public void onSuccess(final Integer pInteger) {
                                                Toast.makeText(DishInfoActivity.this, R.string.rating_saved, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(final Exception e) {
                                                mDialogHelper.showSentButNotSavedRateDialog();
                                            }
                                        });
                                    } catch (final NumberFormatException ex) {
                                        onError(ex);
                                    }
                                }

                                @Override
                                public void onError(final Exception e) {
                                    Log.e(TAG, "onError: " + e.getMessage(), e);
                                    mDialogHelper.showNoInternetDialog();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                Log.e(TAG, "onError: " + e.getMessage(), e);
                mDialogHelper.showCleanCacheDialog();
            }

        }, WHERE_CLAUSE, DatabaseHelper.getSqlStringInterpret(pDishNameFromIntent));
    }
}
