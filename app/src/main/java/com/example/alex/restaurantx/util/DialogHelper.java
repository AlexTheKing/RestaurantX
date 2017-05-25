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

        return showDialog(builder);
    }

    public AlertDialog showCleanCacheDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.clean_cache);

        return showDialog(builder);
    }

    public AlertDialog showSentButNotSavedRateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setCancelable(true);
        builder.setMessage(R.string.problem_but_sent);

        return showDialog(builder);
    }

    public AlertDialog showAboutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.about);
        builder.setMessage(R.string.about_info);
        builder.setCancelable(true);

        return showDialog(builder);
    }

    public AlertDialog showErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.problem_occurred);
        builder.setMessage(R.string.error_application);
        builder.setCancelable(true);

        return showDialog(builder);
    }

    private AlertDialog showDialog(final AlertDialog.Builder pBuilder){
        final AlertDialog dialog = pBuilder.create();
        dialog.show();

        return dialog;
    }
}
