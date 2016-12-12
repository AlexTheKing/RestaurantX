package com.example.alex.restaurantx.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CursorDataAdapter<Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {

    private final Cursor mCursor;
    private final IAdapterHelper<Holder, Cursor> mHelper;
    private final int mLayout;

    public CursorDataAdapter(final Cursor pCursor, final IAdapterHelper<Holder, Cursor> pCallback, final int pLayout) {
        mCursor = pCursor;
        mHelper = pCallback;
        mLayout = pLayout;
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        final View view = LayoutInflater.from(pParent.getContext()).inflate(mLayout, pParent, false);
        return mHelper.build(view);
    }

    @Override
    public void onBindViewHolder(final Holder pHolder, final int pPosition) {
        mHelper.OnBindViewHolder(mCursor, pHolder, pPosition);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
