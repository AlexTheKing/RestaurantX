package com.example.alex.restaurantx.json;

import android.os.AsyncTask;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.model.Dish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    public void parseTypesOfDishes(final String pJsonString, final IResultCallback<List<String>> pCallback) {
        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(final String... pJsonStrings) {
                try {
                    final List<String> types = new ArrayList<>();
                    final JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject("response");
                    final JSONArray typesArray = rootObject.getJSONArray("types");
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

    public void parseMenu(final String pJsonString, final String pTypeOfDishes, final IResultCallback<List<Dish>> pCallback) {
        new AsyncTask<String, Void, List<Dish>>() {
            @Override
            protected List<Dish> doInBackground(final String... pJsonStrings) {
                try {
                    final List<Dish> menu = new ArrayList<>();
                    final JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject("response");
                    final JSONArray jsonArrayDishesOfType = rootObject.getJSONArray(pTypeOfDishes);
                    for (int dishesIndex = 0; dishesIndex < jsonArrayDishesOfType.length(); dishesIndex++) {
                        final JSONObject jsonDishObject = jsonArrayDishesOfType.getJSONObject(dishesIndex);
                        final String name = jsonDishObject.getString("name");
                        final int cost = jsonDishObject.getInt("cost");
                        final String weight = jsonDishObject.getString("weight");
                        final String description = jsonDishObject.getString("description");
                        final int userEstimation = -1;
                        final float averageEstimation = (float) jsonDishObject.getDouble("average_estimation");
                        final String bitmapUrl = jsonDishObject.getString("bitmap_url");
                        final JSONArray jsonIngredientsArray = jsonDishObject.getJSONArray("ingredients");
                        final String ingredients[] = new String[jsonIngredientsArray.length()];
                        for (int ingredientIndex = 0; ingredientIndex < jsonIngredientsArray.length(); ingredientIndex++) {
                            ingredients[ingredientIndex] = jsonIngredientsArray.getString(ingredientIndex);
                        }
                        final Dish dish = new Dish(name, cost, weight, ingredients);
                        dish.setType(pTypeOfDishes);
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
}
