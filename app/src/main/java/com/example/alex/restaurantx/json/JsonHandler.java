package com.example.alex.restaurantx.json;

import com.example.alex.restaurantx.model.Dish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonHandler {

    private static JsonHandler sHandler;

    public List<String> parseTypesOfDishes(String pJsonString) throws JSONException {
        List<String> types = new ArrayList<>();
        JSONObject rootObject = new JSONObject(pJsonString).getJSONObject("response");
        JSONArray typesArray = rootObject.getJSONArray("types");
        for (int index = 0; index < typesArray.length(); index++) {
            types.add(typesArray.getString(index));
        }
        return types;
    }

    public HashMap<String, List<Dish>> parseMenu(String pJsonString, List<String> pTypesOfDishes) throws JSONException {
        HashMap<String, List<Dish>> menu = new HashMap<>();
        JSONObject rootObject = new JSONObject(pJsonString).getJSONObject("response");
        for (int index = 0; index < rootObject.length(); index++) {
            JSONArray jsonDishesOfTypeArray = rootObject.getJSONArray(pTypesOfDishes.get(index));
            menu.put(pTypesOfDishes.get(index), new ArrayList<Dish>());
            for (int index2 = 0; index2 < jsonDishesOfTypeArray.length(); index2++) {
                JSONObject jsonDishObject = jsonDishesOfTypeArray.getJSONObject(index2);
                String name = jsonDishObject.getString("name");
                int cost = jsonDishObject.getInt("cost");
                String weight = jsonDishObject.getString("weight");
                List<String> ingredients = new ArrayList<>();
                JSONArray jsonIngredientsArray = jsonDishObject.getJSONArray("ingredients");
                for (int index3 = 0; index3 < jsonIngredientsArray.length(); index3++) {
                    ingredients.add(jsonIngredientsArray.getString(index3));
                }
                menu.get(pTypesOfDishes.get(index)).add(new Dish(name, cost, weight, ingredients));
            }
        }
        return menu;
    }

}
