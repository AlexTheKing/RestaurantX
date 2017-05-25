package com.example.alex.restaurantx.util;

import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alex.restaurantx.R;

import java.lang.reflect.Field;

public final class SearchUtils {

    private static final String TAG = "SearchUtils";

    private static MenuItem getActionItem(final Menu menu){
        return menu.findItem(R.id.action_search);
    }

    public static void setupSearch(final Menu menu, final SearchView.OnQueryTextListener pQueryTextListener) {
        final MenuItem actionItem = getActionItem(menu);
        actionItem.setVisible(true);
        final SearchView searchView = (SearchView) actionItem.getActionView();
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        //Uses reflection :c
        try {
            final Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            //Set the cursor resource ID to @null and it'll change its color to text color
            mCursorDrawableRes.set(searchEditText, 0);
        } catch (final Exception e) {
            Log.e(TAG, "setupSearch: " + e.getMessage(), e);
        }

        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(pQueryTextListener);
    }

    public static void hideSearch(final Menu menu){
        final MenuItem actionItem = getActionItem(menu);
        actionItem.setVisible(false);
    }
}
