package com.example.alex.restaurantx.api;

import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.IResultCallback;
import com.example.alex.restaurantx.network.Request;

public class ApiManager {

    //restaurant/api/request?method=
    private String mPartUrl = "restaurant/api/";
    private String mDishTypesMethod = "dishtypes.txt";
    private String mMenuMethod = "menu.txt";
    private String mResponse;
    private final Object mLock = new Object();

    public void getTypesMethod(String pBaseUrl, IResultCallback pCallback) {
        makeApiRequest(pBaseUrl, mDishTypesMethod, pCallback);
    }

    public void getMenuMethod(String pBaseUrl, IResultCallback pCallback) {
        makeApiRequest(pBaseUrl, mMenuMethod, pCallback);
    }

    private void makeApiRequest(String pBaseUrl, String pMethodUrl, final IResultCallback<String> pCallback) {
        HttpClient client = HttpClient.getInstance();
        final String[] response = new String[1];
        client.makeAsyncRequest(new Request.Builder().setMethod("GET").setUrl(pBaseUrl + mPartUrl + pMethodUrl).build(), new IResultCallback<String>() {
            @Override
            public void processResults(String pResponse) {
                pCallback.processResults(pResponse);
            }
        });
    }
}
