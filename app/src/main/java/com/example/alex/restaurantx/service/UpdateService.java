package com.example.alex.restaurantx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;

public class UpdateService extends Service {

    private final String TAG = this.getClass().getCanonicalName();
    private boolean mIsStarted;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent pIntent) {
        return null;
    }

    private void execute() {
        if (mIsStarted) {
            return;
        }

        mIsStarted = true;
        final CoreApplication application = ((CoreApplication) getApplication());
        final ApiManager apiManager = application.getApiManager();
        final JsonHandler jsonHandler = application.getJsonHandler();
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        final IResultCallback<Void> errorCallback = new IResultCallback<Void>() {

            @Override
            public void onSuccess(final Void pVoid) {

            }

            @Override
            public void onError(final Exception e) {
                Log.e(TAG, "onError: " + e.getMessage(), e);
                final Intent intent = new Intent(Constants.INTENT_EXTRA_ACTION_UPDATE);
                intent.putExtra(Constants.ACTION_UPDATE_BROADCAST, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        };
        final IResultCallback<List<Dish>> menuCallback = new IResultCallback<List<Dish>>() {

            @Override
            public void onSuccess(final List<Dish> pDishes) {
                final DataManager dataManager = new DataManager(application.getDatabaseHelper());
                dataManager.resaveDishes(pDishes, new IResultCallback<Long>() {

                    @Override
                    public void onSuccess(final Long pLong) {
                        final Intent intent = new Intent(Constants.INTENT_EXTRA_ACTION_UPDATE);
                        intent.putExtra(Constants.ACTION_UPDATE_BROADCAST, true);
                        broadcastManager.sendBroadcast(intent);
                        stopSelf();
                    }

                    @Override
                    public void onError(final Exception e) {
                        errorCallback.onError(e);
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                Log.e(TAG, "onError: " + e.getMessage(), e);
                final Intent intent = new Intent(Constants.INTENT_EXTRA_ACTION_UPDATE);
                intent.putExtra(Constants.ACTION_UPDATE_BROADCAST, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        };

        final IResultCallback<List<String>> typesCallback = new IResultCallback<List<String>>() {

            @Override
            public void onSuccess(final List<String> pTypes) {
                for (final String type : pTypes) {
                    apiManager.getMenuMethod(new IResultCallback<String>() {

                        @Override
                        public void onSuccess(final String pResponse) {
                            jsonHandler.parseMenu(pResponse, type, menuCallback);
                        }

                        @Override
                        public void onError(final Exception e) {
                            errorCallback.onError(e);
                        }
                    });
                }
            }

            @Override
            public void onError(final Exception e) {
                Log.e(TAG, "onError: " + e.getMessage(), e);
                final Intent intent = new Intent(Constants.INTENT_EXTRA_ACTION_UPDATE);
                intent.putExtra(Constants.ACTION_UPDATE_BROADCAST, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        };

        apiManager.getTypesMethod(new IResultCallback<String>() {

            @Override
            public void onSuccess(final String pResponse) {
                jsonHandler.parseTypesOfDishes(pResponse, typesCallback);
            }

            @Override
            public void onError(final Exception e) {
                errorCallback.onError(e);
            }
        });
    }
}
