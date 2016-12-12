package com.example.alex.restaurantx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.alex.restaurantx.adapter.CursorDataAdapter;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.database.models.DishModel;
import com.example.alex.restaurantx.holders.DishViewHolder;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;

public class DishesListActivity extends AppCompatActivity {

    //region JSONExample
    private final String mResponseMenuJson = "{\n" +
            "  \"response\": {\n" +
            "    \"Cold Snacks\":[\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack1\",\n" +
            "          \"cost\":\"12345$\",\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"The small version of this Jordanian and Palestinian dish looks like a pizza covered with a lamb carcass, while a larger banquet variety can cover a whole table. Despite the intimidating appearance, the tender mutton, covered in yogurt sauce and sprinkled with almond and pine nuts, makes for a culinary masterwork.\",\n" +
            "          \"average_estimation\":2.57,\n" +
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
            "          \"cost\":\"12345$\",\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":1.57,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"orange\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack3\",\n" +
            "          \"cost\":\"12345$\",\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":3.27,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"orange\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack4\",\n" +
            "          \"cost\":\"12345$\",\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":3.37,\n" +
            "          \"bitmap_url\":\"http://www.chillhour.com/img/misc/most_beautiful_restaurant_dishes/beautiful_restaurant_dishes_5.jpg\",\n" +
            "          ingredients:[\n" +
            "              \"onion\",\n" +
            "              \"garlic\",\n" +
            "              \"bread\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack5\",\n" +
            "          \"cost\":\"12345$\",\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"description\":\"This is a wonderful fish for you and all your familli, best goes with lemon and pretty ireland cognac\",\n" +
            "          \"average_estimation\":3.57,\n" +
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
            "          \"cost\":\"12345$\",\n" +
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
            "          \"cost\":\"12345$\",\n" +
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
            "          \"cost\":\"12345$\",\n" +
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
            "          \"cost\":\"12345$\",\n" +
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
            "          \"cost\":\"12345$\",\n" +
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

    private RecyclerView mRecyclerView;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);
        final String type = getIntent().getStringExtra(Constants.INTENT_EXTRA_TYPE);
        final TextView textView = (TextView) findViewById(R.id.type_dishes_header);
        textView.setText(type);
        mRecyclerView = (RecyclerView) findViewById(R.id.dishlist);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        upgradeRecyclerView(type);
        //UPDATE LIST
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context pContext, Intent pIntent) {
                //TODO : CHECK FOR UPGRADING
                if(pIntent.getBooleanExtra(Constants.BROADCAST_UPDATE_MESSAGE, false)){
                    upgradeRecyclerView(type);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.INTENT_SERVICE_UPDATE_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void upgradeRecyclerView(String type) {
        final String selectQuery = DishModel.NAME + ", " + DishModel.COST + ", " + DishModel.CURRENCY + ", " + DishModel.AVERAGE_ESTIMATION + ", " + DishModel.INGREDIENTS;
        final String selectCondition = DishModel.TYPE + " = ?";
        DatabaseHelper.getInstance(this, DatabaseHelper.CURRENT_VERSION).query(new IResultCallback<Cursor>() {
            @Override
            public void onSuccess(final Cursor pCursor) {
                mRecyclerView.setAdapter(new CursorDataAdapter<>(pCursor, DishViewHolder.getCursorHelper(DishesListActivity.this), R.layout.item_dishlist));
            }

            @Override
            public void onError(Exception e) {
            }
        }, selectQuery, DishModel.class, selectCondition, DatabaseHelper.getSqlStringInterpret(type));
    }
}
