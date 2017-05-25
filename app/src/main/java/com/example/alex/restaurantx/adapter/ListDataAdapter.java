package com.example.alex.restaurantx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ListDataAdapter<Holder extends RecyclerView.ViewHolder, Type> extends RecyclerView.Adapter<Holder> {

    private final List<Type> mData;
    private final int mLayout;
    private final IAdapterBinder<Holder, List<Type>> mHelper;

    public ListDataAdapter(final List<Type> pData, final IAdapterBinder<Holder, List<Type>> pHelper, final int pLayout) {
        mData = pData;
        mLayout = pLayout;
        mHelper = pHelper;
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        final View view = LayoutInflater.from(pParent.getContext()).inflate(mLayout, pParent, false);

        return mHelper.onCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Holder pHolder, final int pPosition) {
        mHelper.onBindViewHolder(mData, pHolder, pPosition);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
