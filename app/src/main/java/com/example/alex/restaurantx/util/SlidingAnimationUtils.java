package com.example.alex.restaurantx.util;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public final class SlidingAnimationUtils {

    private static final float FROM_DEGREES = 0f;
    private static final float TO_DEGREES = 360f;
    private static final float PIVOT_X = 0.5f;
    private static final float PIVOT_Y = 0.5f;
    private static final long DURATION_IN_MILLIS = 600;

    public static RotateAnimation getAnimation() {
        final RotateAnimation animation = new RotateAnimation(FROM_DEGREES, TO_DEGREES, Animation.RELATIVE_TO_SELF, PIVOT_X, Animation.RELATIVE_TO_SELF, PIVOT_Y);
        animation.setDuration(DURATION_IN_MILLIS);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());

        return animation;
    }

    public static Animation.AnimationListener getCustomAnimationListener(final Runnable pRunnable) {
        return new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(final Animation pAnimation) {
            }

            @Override
            public void onAnimationEnd(final Animation pAnimation) {
                pRunnable.run();
            }

            @Override
            public void onAnimationRepeat(final Animation pAnimation) {
            }
        };
    }

    public static Animation.AnimationListener getRedirectAnimationListener(final Context pContext, final Intent pIntent) {
        return getCustomAnimationListener(new Runnable() {

            @Override
            public void run() {
                pContext.startActivity(pIntent);
            }
        });
    }
}
