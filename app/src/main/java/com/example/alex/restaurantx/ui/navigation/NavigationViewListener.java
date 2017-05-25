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
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.ui.activities.DishListActivity;
import com.example.alex.restaurantx.ui.activities.MainActivity;
import com.example.alex.restaurantx.ui.activities.SettingsActivity;
import com.example.alex.restaurantx.util.DialogHelper;

public class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

    private final Context mContext;
    private final DrawerLayout mDrawer;
    private final DialogHelper mDialogHelper;

    public NavigationViewListener(final Context pContext, final DrawerLayout pDrawer) {
        mContext = pContext;
        mDrawer = pDrawer;
        mDialogHelper = new DialogHelper(mContext);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        boolean result = false;
        switch (item.getItemId()) {
            case R.id.nav_home:
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                result = true;
                break;
            case R.id.nav_recommendations:
                final Intent intent = new Intent(mContext, DishListActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_DISH_TYPE, Constants.INTENT_EXTRA_RECOMMENDATIONS);
                mContext.startActivity(intent);
                result = true;
                break;
            case R.id.nav_settings:
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                result = true;
                break;
            case R.id.nav_help:
                final Intent helpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.github_repo_wiki)));
                mContext.startActivity(helpIntent);
                result = true;
                break;
            case R.id.nav_opensource:
                final Intent repoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.github_repo)));
                mContext.startActivity(repoIntent);
                result = true;
                break;
            case R.id.nav_about:
                mDialogHelper.showAboutDialog();
                mDrawer.closeDrawer(GravityCompat.START);
                result = true;
                break;
        }
        item.setChecked(false);
        mDrawer.closeDrawer(GravityCompat.START);
        return result;
    }
}
