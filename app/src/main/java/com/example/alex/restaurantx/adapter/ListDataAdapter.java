package com.example.alex.restaurantx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.restaurantx.R;

import java.util.List;

public class ListDataAdapter<Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {

    private final List<String> mData;
    private final int mLayout;
    private final IAdapterHelper<Holder, List<String>> mHelper;

    public ListDataAdapter(final List<String> pData, final IAdapterHelper<Holder, List<String>> pHelper, final int pLayout) {
        mData = pData;
        mLayout = pLayout;
        mHelper = pHelper;
    }

    @Override
    public Holder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        final View view = LayoutInflater.from(pParent.getContext()).inflate(mLayout, pParent, false);
        return mHelper.build(view);
    }

    @Override
    public void onBindViewHolder(final Holder pHolder, final int pPosition) {
        mHelper.OnBindViewHolder(mData, pHolder, pPosition);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder {

        final TextView mName;

        DataHolder(final View pView) {
            super(pView);
            mName = ((TextView) pView.findViewById(R.id.typelist_item_name));
        }
    }
}
