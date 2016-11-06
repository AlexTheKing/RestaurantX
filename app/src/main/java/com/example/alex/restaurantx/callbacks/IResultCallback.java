package com.example.alex.restaurantx.callbacks;

public interface IResultCallback<Result> {

    void onSuccess(Result pResult);

    void onError(Exception e);
}
