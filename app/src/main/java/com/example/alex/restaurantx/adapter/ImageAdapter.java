package com.example.alex.restaurantx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.callbacks.IClickCallback;
import com.example.alex.restaurantx.imageloader.ImageLoader;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.DataHolder> {

    private String[] mData;
    private IClickCallback mCallback;

    public ImageAdapter(String[] pTypes, IClickCallback pCallback) {
        mData = pTypes;
        mCallback = pCallback;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup pParent, int pViewType) {
        View view = LayoutInflater.from(pParent.getContext()).inflate(R.layout.list_image_item, pParent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(DataHolder pHolder, final int pPosition) {
        ImageLoader.getInstance().downloadAndDraw(mData[pPosition], pHolder.mImageView, null);
        pHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mCallback.onClick(pPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    static class DataHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        DataHolder(View pView) {
            super(pView);
            mImageView = (ImageView) pView.findViewById(android.R.id.text1);
        }
    }
}
