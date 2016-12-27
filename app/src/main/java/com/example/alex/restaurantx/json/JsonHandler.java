package com.example.alex.restaurantx.json;

import android.os.AsyncTask;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.model.Dish;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    private static JsonHandler sJsonHandler;

    public static JsonHandler getInstance() {
        if (sJsonHandler == null) {
            sJsonHandler = new JsonHandler();
        }
        return sJsonHandler;
    }

    private JsonHandler() {
    }

    public void parseTypesOfDishes(final String pJsonString, @NotNull final IResultCallback<List<String>> pCallback) {
        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(final String... pJsonStrings) {
                try {
                    final List<String> types = new ArrayList<>();
                    final JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject(Constants.JsonHandlerSettings.RESPONSE);
                    final JSONArray typesArray = rootObject.getJSONArray(Constants.JsonHandlerSettings.TYPES);
                    for (int index = 0; index < typesArray.length(); index++) {
                        types.add(typesArray.getString(index));
                    }
                    return types;
                } catch (final JSONException e) {
                    pCallback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final List<String> pStrings) {
                if (pStrings != null) {
                    pCallback.onSuccess(pStrings);
                }
            }
        }.execute(pJsonString);
    }

    public void parseMenu(final String pJsonString, final String pTypeOfDishes, @NotNull final IResultCallback<List<Dish>> pCallback) {
        new AsyncTask<String, Void, List<Dish>>() {
            @Override
            protected List<Dish> doInBackground(final String... pJsonStrings) {
                try {
                    final List<Dish> menu = new ArrayList<>();
                    final JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject(Constants.JsonHandlerSettings.RESPONSE);
                    final JSONArray jsonArrayDishesOfType = rootObject.getJSONArray(pTypeOfDishes);
                    for (int dishesIndex = 0; dishesIndex < jsonArrayDishesOfType.length(); dishesIndex++) {
                        final JSONObject jsonDishObject = jsonArrayDishesOfType.getJSONObject(dishesIndex);
                        final String name = jsonDishObject.getString(DishModel.NAME);
                        final float cost = (float) jsonDishObject.getDouble(DishModel.COST);
                        final String currency = jsonDishObject.getString(DishModel.CURRENCY);
                        final String weight = jsonDishObject.getString(DishModel.WEIGHT);
                        final String description = jsonDishObject.getString(DishModel.DESCRIPTION);
                        final float averageEstimation = (float) jsonDishObject.getDouble(DishModel.AVERAGE_ESTIMATION);
                        final int userEstimation = Math.round(averageEstimation);
                        final String bitmapUrl = jsonDishObject.getString(DishModel.BITMAP_URL);
                        final JSONArray jsonIngredientsArray = jsonDishObject.getJSONArray(DishModel.INGREDIENTS);
                        final String ingredients[] = new String[jsonIngredientsArray.length()];
                        for (int ingredientIndex = 0; ingredientIndex < jsonIngredientsArray.length(); ingredientIndex++) {
                            ingredients[ingredientIndex] = jsonIngredientsArray.getString(ingredientIndex);
                        }
                        final Dish dish = new Dish(name, cost, weight, ingredients);
                        dish.setType(pTypeOfDishes);
                        dish.setCurrency(currency);
                        dish.setDescription(description);
                        dish.setBitmapUrl(bitmapUrl);
                        dish.getVote().setUserEstimation(userEstimation);
                        dish.getVote().setAverageEstimation(averageEstimation);
                        menu.add(dish);
                    }
                    return menu;
                } catch (final JSONException e) {
                    pCallback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final List<Dish> pMenu) {
                if (pMenu != null) {
                    pCallback.onSuccess(pMenu);
                }
            }
        }.execute(pJsonString);
    }

    public void parseComments(final String pJsonString, @NotNull final IResultCallback<List<String>> pCallback) {
        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(final String... pJsonStrings) {
                try {
                    final List<String> comments = new ArrayList<>();
                    final JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject(Constants.JsonHandlerSettings.RESPONSE);
                    final JSONArray commentsArray = rootObject.getJSONArray(Constants.JsonHandlerSettings.COMMENTS);
                    for (int index = 0; index < commentsArray.length(); index++) {
                        comments.add(commentsArray.getString(index));
                    }
                    return comments;
                } catch (final JSONException e) {
                    pCallback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final List<String> pComments) {
                if (pComments != null) {
                    pCallback.onSuccess(pComments);
                }
            }
        }.execute(pJsonString);
    }
}
