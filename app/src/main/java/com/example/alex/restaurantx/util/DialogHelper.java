package com.example.alex.restaurantx.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;

import com.example.alex.restaurantx.R;

public class DialogHelper {

    private final Context mContext;
    private final Handler mHandler;

    public DialogHelper(final Context pContext) {
        mContext = pContext;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void showNoInternetDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setMessage(R.string.checkout_internet);
        builder.setCancelable(true);
        showDialog(builder);
    }

    public void showCleanCacheDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.clean_cache);
        showDialog(builder);
    }

    public void showSentButNotSavedRateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.problem_but_sent);
        showDialog(builder);
    }

    public void showAboutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.about);
        builder.setMessage(R.string.about_info);
        builder.setCancelable(true);
        showDialog(builder);
    }

    public void showErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setMessage(R.string.error_application);
        builder.setCancelable(true);
        showDialog(builder);
    }

    private void showDialog(final AlertDialog.Builder pBuilder){
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                final AlertDialog dialog = pBuilder.create();
                dialog.show();
            }
        });
    }
}
