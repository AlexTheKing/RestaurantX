package com.example.alex.restaurantx.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.alex.restaurantx.network.HttpClient;
import com.example.alex.restaurantx.network.Request;
import com.example.alex.restaurantx.threads.IProgressCallback;
import com.example.alex.restaurantx.threads.ITask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

class BitmapDownloadTask implements ITask<String, Void, Bitmap> {

    private final HttpClient mClient;
    private final String TAG;

    BitmapDownloadTask(final HttpClient pClient, final String pTag) {
        mClient = pClient;
        TAG = pTag;
    }

    @Override
    public Bitmap doInBackground(final String pUrl, final IProgressCallback<Void> pProgressCallback) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            final Request request = new Request.Builder()
                    .setUrl(pUrl)
                    .setMethod(HttpClient.GET_METHOD)
                    .build();
            connection = (HttpURLConnection) mClient.getConnection(request);
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final Exception e) {
            Log.e(TAG, "FAILURE: ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    Log.e(TAG, "FAILURE: inputStream did not closed: ", e);
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return bitmap;
    }
}
