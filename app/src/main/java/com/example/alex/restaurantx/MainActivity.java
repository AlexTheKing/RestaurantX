package com.example.alex.restaurantx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alex.restaurantx.adapter.DataAdapter;
import com.example.alex.restaurantx.callbacks.IClickCallback;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.json.JsonHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String mResponseTypesJson = "{\n" +
            "  \"response\": {\n" +
            "    types: [\n" +
            "        \"Cold Snacks\",\n" +
            "        \"Hot Appetizers\",\n" +
            "        \"Meat Dishes\"\n" +
            "      ]\n" +
            "  }\n" +
            "}";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO : PLACE CONTEXTHOLDER IN PROPER PLACE
        ContextHolder.getInstance().setContext(getApplicationContext());
        loadDishesTypes(new IResultCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> pResult) {
                MainActivity.this.setUpTypesInRecyclerView(pResult);
            }

            @Override
            public void onError(final Exception e) {

            }
        });
    }

    private void loadDishesTypes(final IResultCallback<List<String>> pCallback) {
//        new ApiManager().getTypesMethod(Constants.BASE_URL, new IResultCallback<String>() {
//            @Override
//            public void onSuccess(final String pResponse) {
//                new JsonHandler().parseTypesOfDishes(pResponse, pCallback);
//            }
//
//            @Override
//            public void onError(final Exception e) {
//
//            }
//        });
        new JsonHandler().parseTypesOfDishes(mResponseTypesJson, pCallback);
    }

    private void setUpTypesInRecyclerView(final List<String> pTypes) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final DataAdapter adapter = new DataAdapter(pTypes, new IClickCallback() {

            @Override
            public void onClick(int pPosition) {
                final Intent intent = new Intent(MainActivity.this, DishesListActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_TYPE, pTypes.get(pPosition));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}













