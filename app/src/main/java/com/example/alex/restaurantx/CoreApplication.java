package com.example.alex.restaurantx;

import android.app.Application;

import com.example.alex.restaurantx.api.ApiManager;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.database.DatabaseHelper;
import com.example.alex.restaurantx.holders.ContextHolder;
import com.example.alex.restaurantx.imageloader.ImageLoader;
import com.example.alex.restaurantx.json.JsonHandler;
import com.example.alex.restaurantx.threads.ThreadManager;


public class CoreApplication extends Application {

    public final ContextHolder mContextHolder;
    public final ApiManager mApiManager;
    public final DatabaseHelper mDatabaseHelper;
    public final ImageLoader mImageLoader;
    public final JsonHandler mJsonHandler;
    public final ThreadManager mThreadManager;

    public CoreApplication() {
        super();
        mContextHolder = ContextHolder.getInstance();
        mContextHolder.setContext(getApplicationContext());
        mApiManager = ApiManager.getInstance();
        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext(), Constants.DATABASE_CURRENT_VERSION);
        mImageLoader = ImageLoader.getInstance();
        mJsonHandler = JsonHandler.getInstance();
        mThreadManager = ThreadManager.getInstance();
    }

}
