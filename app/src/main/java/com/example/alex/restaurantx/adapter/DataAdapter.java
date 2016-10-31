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

    private List<String> mData;
    private IClickCallback mCallback;

    public DataAdapter(List<String> pTypes, IClickCallback pCallback) {
        mData = pTypes;
        mCallback = pCallback;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        View view = LayoutInflater.from(pParent.getContext()).inflate(R.layout.list_item, pParent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder pHolder, final int pPosition) {
        pHolder.mTextView.setText(mData.get(pPosition));
        pHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mCallback.onClick(pPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class DataHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        DataHolder(View pView) {
            super(pView);
            mTextView = (TextView) pView.findViewById(android.R.id.text1);
        }
    }
}
