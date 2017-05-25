package com.example.alex.restaurantx.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.Request;
import com.example.alex.restaurantx.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class ApiManager {

    private static ApiManager sApiManager;
    private final String mBaseUrl = Constants.ApiSettings.HOST_URL + Constants.ApiSettings.RELATIVE_URL;
    private final HttpClient mClient;

    private ApiManager() {
        mClient = new HttpClient();
    }

    public static ApiManager getInstance() {
        if (sApiManager == null) {
            sApiManager = new ApiManager();
        }

        return sApiManager;
    }

    public void getTypesMethod(@NonNull final IResultCallback<String> pCallback) {
        makeApiRequest(Constants.ApiSettings.LIST_TYPES, pCallback);
    }

    public void getMenuMethod(@NonNull final IResultCallback<String> pCallback) {
        makeApiRequest(Constants.ApiSettings.LIST_DISHES, pCallback);
    }

    public void getCommentsMethod(final String pDishName, @NonNull final IResultCallback<String> pCallback) {
        try {
            final String encodedName = URLEncoder.encode(pDishName, StringUtils.UTF8);
            makeApiRequest(String.format(Constants.ApiSettings.LIST_COMMENTS, encodedName), pCallback);
        } catch (final UnsupportedEncodingException ex) {
            pCallback.onError(ex);
        }
    }

    public void getRecommendationsMethod(final String pInstanceId, @NonNull final IResultCallback<String> pCallback) {
        final String recommendationsMethod = String.format(Constants.ApiSettings.LIST_RECOMMENDATIONS, pInstanceId);
        makeApiRequest(recommendationsMethod, pCallback);
    }

    public void addRateMethod(final String pDishName, final String pInstanceId, final int pRate, @Nullable final IResultCallback<String> pCallback) {
        try {
            final String encodedName = URLEncoder.encode(pDishName, StringUtils.UTF8);
            final String rateMethod = String.format(Constants.ApiSettings.ADD_RATE, encodedName, pInstanceId, String.valueOf(pRate));
            makeApiRequest(rateMethod, pCallback);
        } catch (final UnsupportedEncodingException ex) {
            if (pCallback != null) {
                pCallback.onError(ex);
            }
        }
    }

    public void addCommentMethod(final String pDishName, final String pInstanceId, final String pComment, @Nullable final IResultCallback<String> pCallback) {
        try {
            final String encodedName = URLEncoder.encode(pDishName, StringUtils.UTF8);
            final String encodedComment = URLEncoder.encode(pComment, StringUtils.UTF8);
            final String commentMethod = String.format(Constants.ApiSettings.ADD_COMMENT, encodedName, pInstanceId, encodedComment);
            makeApiRequest(commentMethod, pCallback);
        } catch (final UnsupportedEncodingException ex) {
            if (pCallback != null) {
                pCallback.onError(ex);
            }
        }
    }



    private void makeApiRequest(final String pMethodUrl, @Nullable final IResultCallback<String> pCallback) {
        final Request request = new Request.Builder()
                .setMethod(HttpClient.GET_METHOD)
                .setUrl(mBaseUrl + pMethodUrl)
                .build();

        mClient.makeAsyncRequest(request, new IResultCallback<String>() {

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
