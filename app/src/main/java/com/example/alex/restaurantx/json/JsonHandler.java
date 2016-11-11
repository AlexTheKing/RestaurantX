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

    private static JsonHandler sHandler;

    public void parseTypesOfDishes(final String pJsonString, final IResultCallback<List<String>> pCallback) {
        new AsyncTask<String, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(String... pJsonStrings) {
                try {
                    List<String> types = new ArrayList<>();
                    JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject("response");
                    JSONArray typesArray = rootObject.getJSONArray("types");
                    for (int index = 0; index < typesArray.length(); index++) {
                        types.add(typesArray.getString(index));
                    }
                    return types;
                } catch (JSONException e) {
                    pCallback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<String> pStrings) {
                if (pStrings != null) {
                    pCallback.onSuccess(pStrings);
                }
            }
        }.execute(pJsonString);
    }

    public void parseMenu(final String pJsonString, final String pTypeOfDishes, final IResultCallback<List<Dish>> pCallback) {
        new AsyncTask<String, Void, List<Dish>>() {
            @Override
            protected List<Dish> doInBackground(String... pJsonStrings) {
                try {
                    List<Dish> menu = new ArrayList<>();
                    JSONObject rootObject = new JSONObject(pJsonStrings[0]).getJSONObject("response");
                    JSONArray jsonArrayDishesOfType = rootObject.getJSONArray(pTypeOfDishes);
                    for (int dishesIndex = 0; dishesIndex < jsonArrayDishesOfType.length(); dishesIndex++) {
                        JSONObject jsonDishObject = jsonArrayDishesOfType.getJSONObject(dishesIndex);
                        String name = jsonDishObject.getString("name");
                        int cost = jsonDishObject.getInt("cost");
                        String weight = jsonDishObject.getString("weight");
                        String description = jsonDishObject.getString("description");
                        //TODO : RESOLVE PROBLEM WITH USER ESTIMATION
                        int userEstimation = 1;
                        float averageEstimation = (float) jsonDishObject.getDouble("average_estimation");
                        String bitmapUrl = jsonDishObject.getString("bitmap_url");
                        JSONArray jsonIngredientsArray = jsonDishObject.getJSONArray("ingredients");
                        String ingredients[] = new String[jsonIngredientsArray.length()];
                        for (int ingredientIndex = 0; ingredientIndex < jsonIngredientsArray.length(); ingredientIndex++) {
                            ingredients[ingredientIndex] = jsonIngredientsArray.getString(ingredientIndex);
                        }
                        Dish dish = new Dish(name, cost, weight, ingredients);
                        dish.setType(pTypeOfDishes);
                        dish.setDescription(description);
                        dish.setBitmapUrl(bitmapUrl);
                        dish.getVote().setUserEstimation(userEstimation);
                        dish.getVote().setAverageEstimation(averageEstimation);
                        menu.add(dish);
                    }
                    return menu;
                } catch (JSONException e) {
                    pCallback.onError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Dish> pMenu) {
                if (pMenu != null) {
                    pCallback.onSuccess(pMenu);
                }
            }
        }.execute(pJsonString);
    }

}
