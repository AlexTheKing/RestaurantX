package com.example.alex.restaurantx.holders.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.alex.restaurantx.R;
import com.example.alex.restaurantx.adapter.IAdapterBinder;

import java.util.List;

public final class CommentViewHolder extends RecyclerView.ViewHolder {

    private final TextView mComment;

    private CommentViewHolder(final View pView) {
        super(pView);

        mComment = ((TextView) pView.findViewById(R.id.comments_list_item_comment));
    }

    public static IAdapterBinder<CommentViewHolder, List<String>> getListBinder(final ScrollView pParentScrollView) {
        return new IAdapterBinder<CommentViewHolder, List<String>>() {

            @Override
            public void onBindViewHolder(final List<String> pStrings, final CommentViewHolder pHolder, final int pPosition) {
                pHolder.mComment.setHorizontalScrollBarEnabled(false);
                pHolder.mComment.setMovementMethod(new ScrollingMovementMethod());
                pHolder.mComment.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(final View pView, final MotionEvent pMotionEvent) {
                        pParentScrollView.requestDisallowInterceptTouchEvent(true);

                        return false;
                    }
                });
                final String comment = pStrings.get(pPosition);
                pHolder.mComment.setText(comment);
            }

            @Override
            public CommentViewHolder onCreateViewHolder(final View pView) {
                return new CommentViewHolder(pView);
            }
        };
    }
}
