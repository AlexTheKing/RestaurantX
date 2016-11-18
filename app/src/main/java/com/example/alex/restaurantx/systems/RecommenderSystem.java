package com.example.alex.restaurantx.systems;

import android.provider.ContactsContract;

import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.model.IngredientIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecommenderSystem {

    private static RecommenderSystem sRecommenderSystem;
    private IngredientIndex mIndex;

    private RecommenderSystem() {
        mIndex = new IngredientIndex();
    }

    public static RecommenderSystem getInstance() {
        if (sRecommenderSystem == null) {
            sRecommenderSystem = new RecommenderSystem();
        }
        return sRecommenderSystem;
    }

    public void updateVoteForDish(Dish pDish) {
        DatabaseHelper helper = DatabaseHelper.getInstance(ContextHolder.getInstance().getContext(), DatabaseHelper.CURRENT_VERSION);
        helper.delete(DishModel.class, null, "name = ?", DatabaseHelper.getSqlStringInterpret(pDish.getName()));
        helper.insert(DishModel.class, pDish.convert(), null);
        //TODO : SEND NEW VOTE TO BACKEND FOR UPDATE
    }

    private List<Dish> getSortedMenuByTopVotes(List<Dish> pDishesOfOneType) {
        List<Dish> sortedDishes = new ArrayList<>();
        float maxVote = 0;
        Dish maxDish = null;
        for (int i = 0; i < pDishesOfOneType.size(); i++) {
            for (Dish dish : pDishesOfOneType) {
                if (dish.getVote().getAverageEstimation() > maxVote && !sortedDishes.contains(dish)) {
                    maxVote = dish.getVote().getAverageEstimation();
                    maxDish = dish;
                }
            }
            sortedDishes.add(maxDish);
            maxDish = null;
            maxVote = 0;
        }
        return sortedDishes;
    }

    private List<Dish> getSortedMenuByTopIngredients(List<Dish> pDishesOfOneType) {
        List<Dish> sortedDishes = new ArrayList<>();
        HashMap<Dish, Integer> estimations = new HashMap<>();
        for (Dish dish : pDishesOfOneType) {
            estimations.put(dish, mIndex.getEstimation(dish));
        }
        int maxEstimation = 0;
        Dish maxDish = null;
        for (int i = 0; i < pDishesOfOneType.size(); i++) {
            for (Dish dish : estimations.keySet()) {
                if (estimations.get(dish) > maxEstimation && !sortedDishes.contains(dish)) {
                    maxEstimation = estimations.get(dish);
                    maxDish = dish;
                }
            }
            sortedDishes.add(maxDish);
            maxDish = null;
            maxEstimation = 0;
        }
        return sortedDishes;
    }

    public HashMap<String, List<Dish>> analyzeMenu(HashMap<String, List<Dish>> pDishesWithTypes) {
        HashMap<String, List<Dish>> newMenu = new HashMap<>();
        for (String typeOfDish : pDishesWithTypes.keySet()) {
            newMenu.put(typeOfDish, getSortedMenuByTopVotes(pDishesWithTypes.get(typeOfDish)));
        }
        return newMenu;
    }
}
