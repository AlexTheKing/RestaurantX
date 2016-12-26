package com.example.alex.restaurantx.imageloader;

import com.example.alex.restaurantx.threads.PriorityRunnable;

class PriorityModel {

    private String mUrl;
    private final PriorityRunnable mRunnable;

    PriorityModel(final PriorityRunnable pRunnable) {
        mRunnable = pRunnable;
    }

    public PriorityRunnable getRunnable() {
        return mRunnable;
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
}
