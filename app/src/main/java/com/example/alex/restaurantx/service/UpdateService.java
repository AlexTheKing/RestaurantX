package com.example.alex.restaurantx.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;

import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.model.Dish;
import com.example.alex.restaurantx.systems.DataManager;

import java.util.List;


public class UpdateService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

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

    private void execute(){
        final ApiManager apiManager = new ApiManager();
        final JsonHandler jsonHandler = new JsonHandler();
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final IResultCallback<List<Dish>> menuCallback = new IResultCallback<List<Dish>>() {
            @Override
            public void onSuccess(List<Dish> pDishes) {
                DataManager dataManager = new DataManager();
                dataManager.saveDishes(pDishes, new IResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer pInteger) {
                        //TODO : IMPLEMENT NOTIFICATION
                        Toast.makeText(UpdateService.this, "Menu updated", Toast.LENGTH_SHORT).show();
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
                    apiManager.getMenuMethod(Constants.BASE_URL, new IResultCallback<String>() {
                        @Override
                        public void onSuccess(String pResponse) {
                            jsonHandler.parseMenu(pResponse, type, menuCallback);
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
        apiManager.getTypesMethod(Constants.BASE_URL, new IResultCallback<String>() {
            @Override
            public void onSuccess(final String pResponse) {
                jsonHandler.parseTypesOfDishes(pResponse, typesCallback);
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
