package com.example.alex.restaurantx;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_info);
        String dishNameFromIntent = getIntent().getStringExtra("dish_name");
        loadDishFromDatabase(dishNameFromIntent);
    }

    private void loadDishFromDatabase(String pDishNameFromIntent) {
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
        DataManager dataManager = new DataManager();
        dataManager.loadDishes(new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> pDishes) {
                Dish dish = pDishes.get(0);
                dishType.setText(dish.getType());
                dishWeight.setText(dish.getWeight());
                dishCost.setText(String.valueOf(dish.getCost()) + getString(R.string.basic_currency));
                dishDescription.setText(dish.getDescription());
                dishIngredients.setText(getString(R.string.ingredients_string) + dish.getIngredientsAsString());
                dishUserRating.setRating(dish.getVote().getUserEstimation());
                dishAverageRating.setText(String.valueOf(dish.getVote().getAverageEstimation()));
                ImageLoader.getInstance().downloadAndDraw(dish.getBitmapUrl(), dishImage, null);
            }

            @Override
            public void onError(Exception e) {

            }

        }, "WHERE " + DishModel.NAME + " = ?", DatabaseHelper.getSqlStringInterpret(pDishNameFromIntent));

        dishUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            //TODO : ADD RATING TO DATABASE AND SEND RATING REQUEST TO SERVER
            }
        });
    }
}
