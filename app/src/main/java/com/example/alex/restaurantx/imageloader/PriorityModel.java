package com.example.alex.restaurantx.imageloader;

import com.example.alex.restaurantx.threads.PriorityRunnable;

class PriorityModel {

    private final PriorityRunnable mRunnable;
    private String mUrl;

    PriorityModel(final PriorityRunnable pRunnable) {
        mRunnable = pRunnable;
    }

    String getUrl() {
        return mUrl;
    }

    void setUrl(final String pUrl) {
        mUrl = pUrl;
    }

    int getPriority() {
        return mRunnable.getPriority();
    }

    void setPriority(final int pPriority) {
        mRunnable.setPriority(pPriority);
    }

    public PriorityRunnable getRunnable() {
        return mRunnable;
    }
}
