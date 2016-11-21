package com.example.alex.restaurantx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.callbacks.IClickCallback;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataHolder> {

    private final List<String> mData;
    private final IClickCallback mCallback;

    public DataAdapter(final List<String> pTypes, final IClickCallback pCallback) {
        mData = pTypes;
        mCallback = pCallback;
    }

    @Override
    public DataHolder onCreateViewHolder(final ViewGroup pParent, final int pViewType) {
        final View view = LayoutInflater.from(pParent.getContext()).inflate(R.layout.list_item, pParent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataHolder pHolder, final int pPosition) {
        pHolder.mTextView.setText(mData.get(pPosition));
        pHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View pView) {
                mCallback.onClick(pPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder {

        final TextView mTextView;

        DataHolder(final View pView) {
            super(pView);
            mTextView = (TextView) pView.findViewById(android.R.id.text1);
        }
    }
}
