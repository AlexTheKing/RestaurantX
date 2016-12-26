package com.example.alex.restaurantx.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ApiManager {

    private static ApiManager sApiManager;

    private ApiManager() { };

    public static ApiManager getInstance(){
        if(sApiManager == null){
            sApiManager = new ApiManager();
        }
        return sApiManager;
    }

    public void getTypesMethod(@NonNull final IResultCallback<String> pCallback) {
        makeApiRequest(Constants.ApiSettings.TYPES_METHOD, pCallback);
    }

    public void getMenuMethod(@NonNull final IResultCallback<String> pCallback) {
        makeApiRequest(Constants.ApiSettings.MENU_METHOD, pCallback);
    }

    public void getCommentsMethod(final String pDishName, @NonNull final IResultCallback<String> pCallback) {
        try {
            String encodedName = URLEncoder.encode(pDishName, "UTF-8");
            makeApiRequest(String.format(Constants.ApiSettings.COMMENTS_METHOD, encodedName), pCallback);
        } catch (UnsupportedEncodingException ex) {
            pCallback.onError(ex);
        }
    }

    public void setRateMethod(final String pDishName, final String pInstanceId, final int pRate, @Nullable final IResultCallback<String> pCallback) {
        try {
            String encodedName = URLEncoder.encode(pDishName, "UTF-8");
            final String rateMethod = String.format(Constants.ApiSettings.RATE_METHOD, encodedName, pInstanceId, String.valueOf(pRate));
            makeApiRequest(rateMethod, pCallback);
        } catch (UnsupportedEncodingException ex) {
            if (pCallback != null) {
                pCallback.onError(ex);
            }
        }
    }

    public void setCommentMethod(final String pDishName, final String pInstanceId, final String pComment, @Nullable final IResultCallback<String> pCallback) {
        try {
            String encodedName = URLEncoder.encode(pDishName, "UTF-8");
            String encodedComment = URLEncoder.encode(pComment, "UTF-8");
            final String commentMethod = String.format(Constants.ApiSettings.ADD_COMMENT_METHOD, encodedName, pInstanceId, encodedComment);
            makeApiRequest(commentMethod, pCallback);
        } catch (UnsupportedEncodingException ex) {
            if (pCallback != null) {
                pCallback.onError(ex);
            }
        }
    }

    private void makeApiRequest(final String pMethodUrl, @Nullable final IResultCallback<String> pCallback) {
        final HttpClient client = new HttpClient();
        final Request request = new Request.Builder()
                .setMethod(Constants.ApiSettings.GET_METHOD)
                .setUrl(Constants.ApiSettings.BASE_URL + Constants.ApiSettings.PART_URL + pMethodUrl)
                .build();

        client.makeAsyncRequest(request, new IResultCallback<String>() {
            @Override
            public void onSuccess(final String pResponse) {
                if (pCallback != null) {
                    pCallback.onSuccess(pResponse);
                }
            }

            @Override
            public void onError(final Exception e) {
                if (pCallback != null) {
                    pCallback.onError(e);
                }
            }
        });
    }
}
