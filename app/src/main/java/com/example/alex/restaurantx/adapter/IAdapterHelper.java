package com.example.alex.restaurantx.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface IAdapterHelper<Holder extends RecyclerView.ViewHolder, DataStructure> {

    void OnBindViewHolder(final DataStructure structure, final Holder pHolder, final int pPosition);

    Holder build(View view);

}
