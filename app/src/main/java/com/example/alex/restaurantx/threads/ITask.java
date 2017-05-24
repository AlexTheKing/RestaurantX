package com.example.alex.restaurantx.threads;

public interface ITask<Params, Progress, Result> {

    Result doInBackground(Params pParams, IProgressCallback<Progress> pProgressCallback) throws Exception;
}
