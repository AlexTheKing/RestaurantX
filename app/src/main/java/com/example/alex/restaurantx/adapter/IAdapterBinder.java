package com.example.alex.restaurantx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IAdapterBinder<Holder extends RecyclerView.ViewHolder, DataStructure> {

    void onBindViewHolder(final DataStructure structure, final Holder pHolder, final int pPosition);

    Holder onCreateViewHolder(View view);
}
