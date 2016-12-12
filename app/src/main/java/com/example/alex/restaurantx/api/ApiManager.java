package com.example.alex.restaurantx.api;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.Request;

public class ApiManager {

    //restaurant/api/request?method=
    private final String mPartUrl = "api/";
    private final String mDishTypesMethod = "types";
    private final String mMenuMethod = "dishes";

    public void getTypesMethod(final String pBaseUrl, final IResultCallback<String> pCallback) {
        makeApiRequest(pBaseUrl, mDishTypesMethod, pCallback);
    }

    public void getMenuMethod(final String pBaseUrl, final IResultCallback<String> pCallback) {
        makeApiRequest(pBaseUrl, mMenuMethod, pCallback);
    }

    private void makeApiRequest(final String pBaseUrl, final String pMethodUrl, final IResultCallback<String> pCallback) {
        final HttpClient client = new HttpClient();
        final Request request = new Request.Builder()
                .setMethod(Constants.GET_METHOD)
                .setUrl(pBaseUrl + mPartUrl + pMethodUrl)
                .build();

        client.makeAsyncRequest(request, new IResultCallback<String>() {
            @Override
            public void onSuccess(final String pResponse) {
                pCallback.onSuccess(pResponse);
            }

            @Override
            public void onError(final Exception e) {

            }
        });
    }
}
