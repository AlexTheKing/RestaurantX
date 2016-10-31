package com.example.alex.restaurantx.threads;

public interface IResultCallback<Result> {

    void onSuccess(Result pResult);

    void onError(Exception e);
}
