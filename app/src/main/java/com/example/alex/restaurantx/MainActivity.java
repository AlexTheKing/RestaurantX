package com.example.alex.restaurantx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alex.restaurantx.adapter.DataAdapter;
import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IClickCallback;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.json.JsonHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String mBaseUrl = "http://192.168.100.6/";
    private String mResponseTypesJson = "{\n" +
            "  \"response\": {\n" +
            "    types: [\n" +
            "        \"Cold Snacks\",\n" +
            "        \"Hot Appetizers\",\n" +
            "        \"Meat Dishes\"\n" +
            "      ]\n" +
            "  }\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO PLACE CONTEXTHOLDER IN PROPER PLACE
        ContextHolder.getInstance().setContext(getApplicationContext());
        loadDishesTypes(new IResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> pResult) {
                MainActivity.this.setUpTypesInRecyclerView(pResult);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void loadDishesTypes(final IResultCallback<List<String>> pCallback) {
//        new ApiManager().getTypesMethod(mBaseUrl, new IResultCallback<String>() {
//            @Override
//            public void onSuccess(final String pResponse) {
//                new JsonHandler().parseTypesOfDishes(pResponse, pCallback);
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
        new JsonHandler().parseTypesOfDishes(mResponseTypesJson, pCallback);
    }

    private void setUpTypesInRecyclerView(final List<String> pTypes) {
        final RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DataAdapter adapter = new DataAdapter(pTypes, new IClickCallback() {

            @Override
            public void onClick(int pPosition) {
                Intent intent = new Intent(MainActivity.this, DishesListActivity.class);
                intent.putExtra("type", pTypes.get(pPosition));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}













