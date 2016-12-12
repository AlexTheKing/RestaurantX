package com.example.alex.restaurantx;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        final String dishNameFromIntent = getIntent().getStringExtra(Constants.INTENT_EXTRA_DISHNAME);
        final boolean haveProhibited = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_HAVE_PROHIBITED, false);
        loadDishFromDatabase(dishNameFromIntent, haveProhibited);
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
        final TextView dishAverageRating = (TextView) findViewById(R.id.dish_average_rating);
        final ImageView dishImage = (ImageView) findViewById(R.id.dish_image_full);
        final DataManager dataManager = new DataManager();
        dataManager.loadDishes(new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(final List<Dish> pDishes) {
                final Dish dish = pDishes.get(0);
                dishType.setText(dish.getType());
                dishWeight.setText(dish.getWeight());
                dishCost.setText(String.format("%s"+dish.getCurrency(), String.valueOf(dish.getCost())));
                dishDescription.setText(dish.getDescription());
                dishIngredients.setText(String.format(getString(R.string.ingredients_string), dish.getIngredientsAsString()));
                if(pHaveProhibited){
                    dishIngredients.setTextColor(Color.RED);
                }
                dishUserRating.setRating(dish.getVote().getUserEstimation());
                dishAverageRating.setText(String.valueOf(dish.getVote().getAverageEstimation()));
                ImageLoader.getInstance().downloadAndDraw(dish.getBitmapUrl(), dishImage, null);
                dishUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(final RatingBar pRatingBar, final float pEstimation, final boolean pIsFromUser) {
                        if (pIsFromUser) {
                            dish.getVote().userSetUserEsimation((int) pEstimation);
                            //TODO : SEND REQUEST TO UPDATE USER VOTE TO BACKEND
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception e) {

            }

        }, WHERE_CLAUSE, DatabaseHelper.getSqlStringInterpret(pDishNameFromIntent));
    }
}
