package com.example.alex.restaurantx.imageloader;

import com.example.alex.restaurantx.threads.PriorityRunnable;

public class PriorityModel {

    private String mUrl;
    private final PriorityRunnable mRunnable;

    public PriorityModel(final PriorityRunnable pRunnable) {
        mRunnable = pRunnable;
    }

    public PriorityRunnable getRunnable() {
        return mRunnable;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(final String pUrl) {
        mUrl = pUrl;
    }

    public int getPriority() {
        return mRunnable.getPriority();
    }

    public void setPriority(final int pPriority) {
        mRunnable.setPriority(pPriority);
    }
}
