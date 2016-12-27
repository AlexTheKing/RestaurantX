package com.example.alex.restaurantx.backend.callbacks;

public interface IResultCallback<Result> {

    void onSuccess(Result result);

    void onError(Exception ex);
}
