package com.example.alex.restaurantx.holders;

import android.content.Context;

public final class ContextHolder {

    private static ContextHolder sContextHolder;
    private Context mContext;

    private ContextHolder() {
    }

    public static ContextHolder getInstance() {
        if (sContextHolder == null) {
            sContextHolder = new ContextHolder();
        }

        return sContextHolder;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(final Context pContext) {
        mContext = pContext;
    }

}
