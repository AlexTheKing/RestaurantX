package com.example.alex.restaurantx.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.alex.restaurantx.R;

public class DialogHelper {

    private final Context mContext;

    public DialogHelper(final Context pContext) {
        mContext = pContext;
    }

    public AlertDialog showNoInternetDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setMessage(R.string.checkout_internet);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public AlertDialog showCleanCacheDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.clean_cache);
        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public AlertDialog showSentButNotSavedRateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.problem_but_sent);
        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
