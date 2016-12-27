package com.example.alex.restaurantx;

import android.app.Application;

import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.json.JsonHandler;


public class CoreApplication extends Application {

    public ApiManager mApiManager;
    public DatabaseHelper mDatabaseHelper;
    public ImageLoader mImageLoader;
    public JsonHandler mJsonHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder contextHolder = ContextHolder.getInstance();
        contextHolder.setContext(this);
        mApiManager = ApiManager.getInstance();
        mDatabaseHelper = DatabaseHelper.getInstance(this, Constants.DATABASE_CURRENT_VERSION);
        mImageLoader = ImageLoader.getInstance();
        mJsonHandler = JsonHandler.getInstance();
    }
}
