package com.example.alex.restaurantx.ui.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.ui.activities.MainActivity;
import com.example.alex.restaurantx.ui.activities.SettingsActivity;

public class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

    private final Context mContext;
    private final DrawerLayout mDrawer;

    public NavigationViewListener(Context pContext, DrawerLayout pDrawer) {
        mContext = pContext;
        mDrawer = pDrawer;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean result = false;
        switch (item.getItemId()) {
            case R.id.nav_home:
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                result = true;
                break;
            case R.id.nav_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                result = true;
                break;
            case R.id.nav_help:
                Intent helpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.github_repo_wiki)));
                mContext.startActivity(helpIntent);
                result = true;
                break;
            case R.id.nav_opensource:
                Intent repoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.github_repo)));
                mContext.startActivity(repoIntent);
                result = true;
                break;
            case R.id.nav_about:
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
                dlgAlert.setTitle(mContext.getString(R.string.about));
                dlgAlert.setMessage(mContext.getString(R.string.about_info));
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                mDrawer.closeDrawer(GravityCompat.START);
                result = true;
                break;
        }
        item.setChecked(false);
        mDrawer.closeDrawer(GravityCompat.START);
        return result;
    }
}
