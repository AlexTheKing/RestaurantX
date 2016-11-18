package com.example.alex.restaurantx.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.Request;
import com.example.alex.restaurantx.threads.IProgressCallback;
import com.example.alex.restaurantx.threads.ITask;
import com.example.alex.restaurantx.threads.PriorityRunnable;
import com.example.alex.restaurantx.threads.ThreadManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Stack;

public class ImageLoader {

    private static ImageLoader sLoader;
    private HttpClient mClient;
    private ThreadManager mThreadManagerInstance;
    private volatile LruCache<String, Bitmap> mLruCache;
    private volatile Stack<PriorityModel> mDownloadPriorities;
    private int defaultCacheSize = 12 * 1024 * 1024;
    private final Object mLockObj = new Object();
    private String TAG = "IMAGELOADER";

    private ImageLoader() {
        mThreadManagerInstance = ThreadManager.getInstance();
        mDownloadPriorities = new Stack<>();
        mClient = HttpClient.getInstance();
        long size;
        if (Runtime.getRuntime().maxMemory() / 4 > defaultCacheSize) {
            size = defaultCacheSize;
        } else {
            size = Runtime.getRuntime().maxMemory() / 4;
        }
        Log.d(TAG, "ImageLoader: MEMORY ALLOCATED FOR LRU CACHE: " + size);
        mLruCache = new LruCache<String, Bitmap>(((int) size)) {

            @Override
            protected int sizeOf(String pKey, Bitmap pValue) {
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

    public void downloadAndDraw(final String pUrl, final ImageView pView, @Nullable final IBitmapDownloadedCallback pCallback, final int... pArgs) {
        if (findModel(pUrl) != -1) {
            risePriority(pUrl);
            recalculatePriorities();
            Log.d(TAG, "downloadAndDraw: UP PRIORITY FOR " + pUrl);
            return;
        }
        pView.setTag(pUrl);
        Bitmap cachedBitmap;
        synchronized (mLockObj) {
            cachedBitmap = mLruCache.get(pUrl);
        }
        Log.d(TAG, "downloadAndDraw: CACHED: " + (cachedBitmap == null ? "NO" : "YES") + " " + pUrl);
        if (cachedBitmap != null && pView.getTag() == pUrl) {
            Log.d(TAG, "downloadAndDraw: FROM LRU CACHE " + pUrl);
            if(pArgs.length != 0) {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(cachedBitmap, pArgs[0], pArgs[1], true);
                pView.setImageBitmap(resizedBitmap);
            } else {
                pView.setImageBitmap(cachedBitmap);
            }
            if (pCallback != null) {
                pCallback.onDownload(cachedBitmap);
            }
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        final PriorityRunnable<String, Void, Bitmap> priorityRunnable = new PriorityRunnable<>(handler, new ITask<String, Void, Bitmap>() {

            @Override
            public Bitmap doInBackground(String pUrl, IProgressCallback<Void> pProgressCallback) {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                Bitmap bitmap = null;
                try {
                    connection = (HttpURLConnection) mClient.getConnection(new Request.Builder().setUrl(pUrl).setMethod("GET").build());
                    inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    Log.e(TAG, "download: ", e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Log.e(TAG, "download: inputStream did not closed: ", e);
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                return bitmap;
            }
        },
        pUrl,
        null,
        new IResultCallback<Bitmap>() {

            private void cleanStack() {
                synchronized (mLockObj) {
                    if (findModel(pUrl) != -1) {
                        mDownloadPriorities.remove(findModel(pUrl));
                    }
                }
                recalculatePriorities();
            }

            @Override
            public void onSuccess(Bitmap pBitmap) {
                if (pBitmap != null) {
                    Log.d(TAG, "onSuccess: DOWNLOADED " + pUrl);
                    Log.d(TAG, "onSuccess: LAY IN LRU");
                    synchronized (mLockObj) {
                        if (!mLruCache.snapshot().containsKey(pUrl)) {
                            mLruCache.put(pUrl, pBitmap);
                        }
                    }
                    if (pView.getTag() == pUrl) {
                        if(pArgs.length != 0) {
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(pBitmap, pArgs[0], pArgs[1], true);
                            pView.setImageBitmap(resizedBitmap);
                        } else {
                            pView.setImageBitmap(pBitmap);
                        }
                        if (pCallback != null) {
                            pCallback.onDownload(pBitmap);
                        }
                    }
                }
                cleanStack();
            }

            @Override
            public void onError(Exception e) {
                cleanStack();
                Log.e(TAG, "onError: ", e);
            }
        });
        PriorityModel model = new PriorityModel(priorityRunnable);
        model.setUrl(pUrl);
        model.setPriority(Thread.MAX_PRIORITY);
        synchronized (mLockObj) {
            mDownloadPriorities.push(model);
        }
        recalculatePriorities();
        mThreadManagerInstance.execute(priorityRunnable);
    }

    private synchronized int findModel(String pUrl) {
        int index = 0;
        for (PriorityModel priorityModel : mDownloadPriorities) {
            if (priorityModel.getUrl().equalsIgnoreCase(pUrl)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private synchronized void risePriority(String pUrl) {
        PriorityModel modelToRise = mDownloadPriorities.remove(findModel(pUrl));
        modelToRise.setPriority(Thread.MAX_PRIORITY);
        mDownloadPriorities.push(modelToRise);
    }

    private synchronized void recalculatePriorities() {
        for (PriorityModel priorityModel : mDownloadPriorities) {
            int priority = Math.round((float) mDownloadPriorities.indexOf(priorityModel) * 10 / mDownloadPriorities.size());
            if (priority == 0) {
                priority = Thread.MIN_PRIORITY;
            }
            priorityModel.setPriority(priority);
        }
    }
}
