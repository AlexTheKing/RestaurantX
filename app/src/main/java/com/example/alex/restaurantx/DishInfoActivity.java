package com.example.alex.restaurantx;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;

public class DishInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getIntent().getIntExtra("dish_id", -1);
        if (id == -1) {
            //TODO SHOW ERROR IF ID WAS NOT FOUND
        }
        setContentView(R.layout.activity_dishinfo);
        final TextView dishName = (TextView) findViewById(R.id.dish_name);
        final TextView dishType = (TextView) findViewById(R.id.dish_type);
        final TextView dishWeight = (TextView) findViewById(R.id.dish_weight);
        final TextView dishCost = (TextView) findViewById(R.id.dish_cost);
        final TextView dishDescription = (TextView) findViewById(R.id.dish_description);
        final TextView dishIngredients = (TextView) findViewById(R.id.dish_ingredients);
        final RatingBar dishUserRating = (RatingBar) findViewById(R.id.dish_user_rating);
        final TextView dishAverageRating = (TextView) findViewById(R.id.dish_average_rating);
        final ImageView dishImage = (ImageView) findViewById(R.id.dish_image_full);
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor pCursor) {
                int indexNameColumn = pCursor.getColumnIndex(DishModel.NAME);
                int indexTypeColumn = pCursor.getColumnIndex(DishModel.TYPE);
                int indexWeightColumn = pCursor.getColumnIndex(DishModel.WEIGHT);
                int indexCostColumn = pCursor.getColumnIndex(DishModel.COST);
                int indexDescriptionColumn = pCursor.getColumnIndex(DishModel.DESCRIPTION);
                int indexIngredientsColumn = pCursor.getColumnIndex(DishModel.INGREDIENTS);
                int indexUserEstimationColumn = pCursor.getColumnIndex(DishModel.USER_ESTIMATION);
                int indexAverageEstimationColumn = pCursor.getColumnIndex(DishModel.AVERAGE_ESTIMATION);
                int indexBitmapUrlColumn = pCursor.getColumnIndex(DishModel.BITMAP_URL);
                if(pCursor.moveToFirst()){
                    dishName.setText(pCursor.getString(indexNameColumn));
                    dishType.setText(pCursor.getString(indexTypeColumn));
                    dishWeight.setText(pCursor.getString(indexWeightColumn));
                    dishCost.setText(pCursor.getString(indexCostColumn));
                    dishDescription.setText(pCursor.getString(indexDescriptionColumn));
                    dishIngredients.setText(pCursor.getString(indexIngredientsColumn));
                    dishUserRating.setRating(pCursor.getInt(indexUserEstimationColumn));
                    dishAverageRating.setText(String.valueOf(pCursor.getFloat(indexAverageEstimationColumn)));
                    ImageLoader.getInstance().downloadAndDraw(pCursor.getString(indexBitmapUrlColumn), dishImage, null);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        }, "*", DishModel.class, "WHERE id = ", String.valueOf(id));

        dishUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //TODO ADD RATING TO SQLITE AND SEND RATING REQUEST TO SERVER
            }
        });
    }
}
