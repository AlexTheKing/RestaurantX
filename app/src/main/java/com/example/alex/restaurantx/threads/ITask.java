package com.example.alex.restaurantx.threads;

public abstract class ITask<Params, Progress, Result> {

    public abstract Result doInBackground(Params pParams, IProgressCallback<Progress> pProgressCallback);

}
