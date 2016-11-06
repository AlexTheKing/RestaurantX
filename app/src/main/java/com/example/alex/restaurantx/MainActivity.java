package com.example.alex.restaurantx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.alex.restaurantx.adapter.DataAdapter;
import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IClickCallback;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.holder.ContextHolder;
import com.example.alex.restaurantx.json.JsonHandler;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String mBaseUrl = "http://192.168.100.6/";
    private String mResponseMenuJson = "{\n" +
            "  \"response\": {\n" +
            "    \"Cold Snacks\":[\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack1\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"carrot\",\n" +
            "              \"onions\",\n" +
            "              \"garlic\",\n" +
            "              \"milk\",\n" +
            "              \"nuts\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"ColdSnack2\",\n" +
            "          \"cost\":67890,\n" +
            "          \"weight\":\"250g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"banana\",\n" +
            "              \"orange\",\n" +
            "              \"lemon\",\n" +
            "              \"lime\"\n" +
            "            ]\n" +
            "        }\n" +
            "        \n" +
            "      ],\n" +
            "    \"Hot Appetizers\":[\n" +
            "        {\n" +
            "          \"name\":\"HotAppetizer1\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"carrot\",\n" +
            "              \"onions\",\n" +
            "              \"garlic\",\n" +
            "              \"milk\",\n" +
            "              \"nuts\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"HotAppetizer2\",\n" +
            "          \"cost\":67890,\n" +
            "          \"weight\":\"250g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"banana\",\n" +
            "              \"orange\",\n" +
            "              \"lemon\",\n" +
            "              \"lime\"\n" +
            "            ]\n" +
            "        }\n" +
            "      ],\n" +
            "    \"Meat Dishes\":[\n" +
            "        {\n" +
            "          \"name\":\"HotAppetizer1\",\n" +
            "          \"cost\":12345,\n" +
            "          \"weight\":\"400g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"carrot\",\n" +
            "              \"onions\",\n" +
            "              \"garlic\",\n" +
            "              \"milk\",\n" +
            "              \"nuts\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\":\"HotAppetizer2\",\n" +
            "          \"cost\":67890,\n" +
            "          \"weight\":\"250g\",\n" +
            "          \"ingredients\":[\n" +
            "              \"banana\",\n" +
            "              \"orange\",\n" +
            "              \"lemon\",\n" +
            "              \"lime\"\n" +
            "            ]\n" +
            "        }\n" +
            "      ]\n" +
            "  }\n" +
            "}";

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
        new ApiManager().getTypesMethod(mBaseUrl, new IResultCallback<String>() {

            @Override
            public void onSuccess(String pResponse) {
                MainActivity.this.setUpTypesInRecyclerView(pResponse);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void setUpTypesInRecyclerView(String pResponse) {
        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        final JsonHandler handler = new JsonHandler();
        List<String> types;
        try {
            types = handler.parseTypesOfDishes(pResponse);
        } catch (JSONException pE) {
            pE.printStackTrace();
            return;
        }
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        final List<String> finalTypes = types;
        recyclerView.setAdapter(new DataAdapter(types, new IClickCallback() {

            @Override
            public void onClick(int pPosition) {
                Toast.makeText(MainActivity.this, finalTypes.get(pPosition), Toast.LENGTH_SHORT).show();
            }

        }));
    }
}
