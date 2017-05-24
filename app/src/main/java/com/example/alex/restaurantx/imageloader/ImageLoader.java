package com.example.alex.restaurantx.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.constants.Constants;
import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.threads.PriorityRunnable;
import com.example.alex.restaurantx.threads.ThreadManager;

import java.util.Stack;

public final class ImageLoader {

    private static ImageLoader sLoader;
    private final HttpClient mClient;
    private final ThreadManager mThreadManagerInstance;
    private final Handler mHandler;
    private final LruCache<String, Bitmap> mLruCache;
    private final Stack<PriorityModel> mDownloadPriorities;
    private final Object mLockObj = new Object();
    private final String TAG = this.getClass().getCanonicalName();

    private ImageLoader() {
        mThreadManagerInstance = ThreadManager.getInstance();
        mDownloadPriorities = new Stack<>();
        mHandler = new Handler(Looper.getMainLooper());
        mClient = new HttpClient();
        final long size;

        if (Runtime.getRuntime().maxMemory() / Constants.ImageLoaderSettings.MEMORY_FACTOR > Constants.ImageLoaderSettings.DEFAULT_MEMORY_CACHE_SIZE) {
            size = Constants.ImageLoaderSettings.DEFAULT_MEMORY_CACHE_SIZE;
        } else {
            size = Runtime.getRuntime().maxMemory() / Constants.ImageLoaderSettings.MEMORY_FACTOR;
        }

        Log.d(TAG, "MEMORY ALLOCATED FOR LRU CACHE: " + size);
        mLruCache = new LruCache<String, Bitmap>(((int) size)) {

            @Override
            protected int sizeOf(final String pKey, final Bitmap pValue) {
                return pKey.getBytes().length + pValue.getByteCount();
            }

        };
    }

    public static ImageLoader getInstance() {
        if (sLoader == null) {
            sLoader = new ImageLoader();
        }

        return sLoader;
    }

    public void downloadAndDraw(final String pUrl, final ImageView pView, @Nullable final IBitmapDownloadedCallback pCallback) {
        downloadAndDraw(pUrl, pView, pCallback, null, null);
    }

    public void downloadAndDraw(final String pUrl, final ImageView pView, @Nullable final IBitmapDownloadedCallback pCallback, final Integer pWidth, final Integer pHeight) {
        if (findModel(pUrl) != -1) {
            risePriority(pUrl);
            recalculatePriorities();
            Log.d(TAG, "PRIORITY UP FOR " + pUrl);

            return;
        }

        pView.setTag(pUrl);
        final Bitmap cachedBitmap = setupCachedBitmap(pUrl, pView, pWidth, pHeight);

        if (pCallback != null && cachedBitmap != null) {
            pCallback.onDownload(cachedBitmap);
        }

        final BitmapDownloadTask task = new BitmapDownloadTask(mClient, TAG);
        final PriorityRunnable<String, Void, Bitmap> priorityRunnable = new PriorityRunnable<>(
                mHandler,
                task,
                pUrl,
                null,
                new IResultCallback<Bitmap>() {

                    @Override
                    public void onSuccess(final Bitmap pBitmap) {
                        if (pBitmap != null) {
                            Log.d(TAG, "SUCCESS: DOWNLOADED " + pUrl);
                            Log.d(TAG, "SUCCESS: LAY IN LRU");

                            synchronized (mLockObj) {
                                if (!mLruCache.snapshot().containsKey(pUrl)) {
                                    mLruCache.put(pUrl, pBitmap);
                                }
                            }

                            if (pView.getTag() == pUrl) {
                                if (pWidth != null && pHeight != null) {
                                    final Bitmap resizedBitmap = Bitmap.createScaledBitmap(pBitmap, pWidth, pHeight, true);
                                    pView.setImageBitmap(resizedBitmap);
                                } else {
                                    pView.setImageBitmap(pBitmap);
                                }

                                if (pCallback != null) {
                                    pCallback.onDownload(pBitmap);
                                }
                            }
                        }

                        cleanStack(pUrl);
                    }

                    @Override
                    public void onError(final Exception e) {
                        cleanStack(pUrl);
                        Log.e(TAG, "FAILURE: ", e);
                    }
                });
        final PriorityModel model = new PriorityModel(priorityRunnable);
        model.setUrl(pUrl);
        model.setPriority(Thread.MAX_PRIORITY);

        synchronized (mLockObj) {
            mDownloadPriorities.push(model);
        }

        recalculatePriorities();
        mThreadManagerInstance.execute(priorityRunnable);
    }

    private Bitmap setupCachedBitmap(final String pUrl, final ImageView pView, final Integer pWidth, final Integer pHeight) {
        final Bitmap cachedBitmap;
        synchronized (mLockObj) {
            cachedBitmap = mLruCache.get(pUrl);
        }
        Log.d(TAG, "CACHED: " + (cachedBitmap == null ? "NO" : "YES") + " " + pUrl);
        if (cachedBitmap != null && pView.getTag() == pUrl) {
            Log.d(TAG, "TAKE UP FROM LRU CACHE " + pUrl);
            if (pWidth != null && pHeight != null) {
                final Bitmap resizedBitmap = Bitmap.createScaledBitmap(cachedBitmap, pWidth, pHeight, true);
                pView.setImageBitmap(resizedBitmap);
            } else {
                pView.setImageBitmap(cachedBitmap);
            }

            return cachedBitmap;
        }

        return null;
    }

    private void cleanStack(final String pUrl) {
        synchronized (mLockObj) {
            if (findModel(pUrl) != -1) {
                mDownloadPriorities.remove(findModel(pUrl));
            }
        }

        recalculatePriorities();
    }

    private synchronized int findModel(final String pUrl) {
        int index = 0;

        for (final PriorityModel priorityModel : mDownloadPriorities) {
            if (priorityModel.getUrl().equalsIgnoreCase(pUrl)) {
                return index;
            }

            index++;
        }

        return -1;
    }

    private synchronized void risePriority(final String pUrl) {
        final PriorityModel modelToRise = mDownloadPriorities.remove(findModel(pUrl));
        modelToRise.setPriority(Thread.MAX_PRIORITY);
        mDownloadPriorities.push(modelToRise);
    }

    private synchronized void recalculatePriorities() {
        for (final PriorityModel priorityModel : mDownloadPriorities) {
            int priority = Math.round((float) mDownloadPriorities.indexOf(priorityModel) * 10 / mDownloadPriorities.size());

            if (priority == 0) {
                priority = Thread.MIN_PRIORITY;
            }

            priorityModel.setPriority(priority);
        }
    }
}
