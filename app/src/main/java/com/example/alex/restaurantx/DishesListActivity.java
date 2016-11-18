package com.example.alex.restaurantx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alex.restaurantx.adapter.DataAdapter;
import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IClickCallback;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.ArrayList;
import java.util.List;

public class DishesListActivity extends AppCompatActivity {

    //region JSONExample
    private String mResponseMenuJson = "{\n" +
            "  \"response\": {\n" +
            "    \"Cold Snacks\":[\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack1\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"The small version of this Jordanian and Palestinian dish looks like a pizza covered with a lamb carcass, while a larger banquet variety can cover a whole table. Despite the intimidating appearance, the tender mutton, covered in yogurt sauce and sprinkled with almond and pine nuts, makes for a culinary masterwork.\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack2\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack3\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack4\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack5\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack6\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack7\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack8\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack9\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack10\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":4.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        }\n" +
            "      ]\n" +
            "  }\n" +
            "}";
    //endregion
    private String mBaseUrl = "http://192.168.100.6/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);
        final String type = getIntent().getStringExtra("type");
        TextView textView = (TextView) findViewById(R.id.type_dishes_header);
        textView.setText(type);
        String[] options = getResources().getStringArray(R.array.drawer_options_eng);
        ListView drawerListView = (ListView) findViewById(R.id.drawer_listview);
        drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_element, R.id.drawer_textview, options));
        loadDishesOfType(type, new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> pDishes) {
                // TODO : CHECK FOR UPGRADING
                DataManager dataManager = new DataManager();
                dataManager.upgradeDishesWithType(pDishes, type);
                DishesListActivity.this.setUpDishesInRecyclerView(pDishes);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        //TODO : load image from url from backend
        String debugUrl = "https://static.dezeen.com/uploads/2014/11/Milan-food-course-IULM-University-and-Scuola-Politecnica-di-Design_dezeen_sq.jpg";
        ImageLoader.getInstance().downloadAndDraw(debugUrl, (ImageView) findViewById(R.id.drawer_image), null, 480, 400);
//        new ApiManager().getSlideMenuImageMethod(mBaseUrl, new IResultCallback<String>() {
//            @Override
//            public void onSuccess(String pUrl) {
//                ImageLoader.getInstance().downloadAndDraw(pUrl, (ImageView) findViewById(R.id.drawer_image), null);
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
    }

    private void loadDishesOfType(final String type, final IResultCallback<List<Dish>> pCallback) {
//        new ApiManager().getMenuMethod(mBaseUrl, new IResultCallback<String>() {
//            @Override
//            public void onSuccess(String pResponse) {
//                new JsonHandler().parseMenu(pResponse, type, pCallback);
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
        new JsonHandler().parseMenu(mResponseMenuJson, type, pCallback);
    }

    private void setUpDishesInRecyclerView(final List<Dish> pDishes) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final List<String> dishesNames = new ArrayList<>();
        for (Dish dish : pDishes) {
            dishesNames.add(dish.getName());
        }
        recyclerView.setAdapter(new DataAdapter(dishesNames, new IClickCallback() {

            @Override
            public void onClick(int pPosition) {
                Intent intent = new Intent(DishesListActivity.this, DishInfoActivity.class);
                intent.putExtra("dish_name", dishesNames.get(pPosition));
                startActivity(intent);
            }

        }));
    }

}
