package com.example.alex.restaurantx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.alex.restaurantx.CoreApplication;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;


public class UpdateService extends Service {

    private boolean mIsStarted = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    private void execute() {
        if (mIsStarted) return;
        mIsStarted = true;
        final CoreApplication application = ((CoreApplication) getApplication());
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        final IResultCallback<List<Dish>> menuCallback = new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> pDishes) {
                DataManager dataManager = new DataManager(application.mDatabaseHelper);
                dataManager.resaveDishes(pDishes, new IResultCallback<Long>() {
                    @Override
                    public void onSuccess(Long pLong) {
                        Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                        intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, true);
                        broadcastManager.sendBroadcast(intent);
                        stopSelf();
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                        intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, false);
                        broadcastManager.sendBroadcast(intent);
                        stopSelf();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        };

        final IResultCallback<List<String>> typesCallback = new IResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> pTypes) {
                for (final String type : pTypes) {
                    application.mApiManager.getMenuMethod(new IResultCallback<String>() {
                        @Override
                        public void onSuccess(String pResponse) {
                            application.mJsonHandler.parseMenu(pResponse, type, menuCallback);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                            intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, false);
                            broadcastManager.sendBroadcast(intent);
                            stopSelf();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        };
        application.mApiManager.getTypesMethod(new IResultCallback<String>() {
            @Override
            public void onSuccess(final String pResponse) {
                application.mJsonHandler.parseTypesOfDishes(pResponse, typesCallback);
            }

            @Override
            public void onError(final Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(Constants.INTENT_SERVICE_UPDATE_ACTION);
                intent.putExtra(Constants.BROADCAST_UPDATE_MESSAGE, false);
                broadcastManager.sendBroadcast(intent);
                stopSelf();
            }
        });
    }
}
