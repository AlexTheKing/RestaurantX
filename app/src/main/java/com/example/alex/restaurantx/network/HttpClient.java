package com.example.alex.restaurantx.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.alex.restaurantx.callbacks.IResultCallback;
import com.example.alex.restaurantx.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

    public static final String GET_METHOD = "GET";
    private static final String HTTPS = "https";
    private final String TAG = this.getClass().getCanonicalName();

    public URLConnection getConnection(final Request pRequest) throws Exception {
        final String urlString = pRequest.getUrl();

        if (urlString == null) {
            throw new IllegalArgumentException("No url provided");
        }

        final URL url = new URL(urlString);
        final URLConnection connection = url.openConnection();

        if (connection == null) {
            throw new Exception("connection == null");
        }

        addProperties(pRequest, connection);

        return connection;
    }

    public void makeAsyncRequest(final Request pRequest, final IResultCallback<String> pCallback) {
        new AsyncTask<Request, Void, String>() {

            @Override
            protected String doInBackground(final Request... requests) {
                String response;

                try {
                    response = executeRequest(requests[0]);
                } catch (final Exception e) {
                    response = e.toString();
                }

                return response;
            }

            @Override
            protected void onPostExecute(final String pResult) {
                pCallback.onSuccess(pResult);
                super.onPostExecute(pResult);
            }
        }.execute(pRequest);
    }

    private String executeRequest(final Request pRequest) throws Exception {
        InputStream inputStream = null;
        final HttpURLConnection connection;

        if (pRequest.getUrl().contains(HTTPS)) {
            connection = (HttpsURLConnection) getConnection(pRequest);
        } else {
            connection = (HttpURLConnection) getConnection(pRequest);
        }
        try {
            inputStream = connection.getInputStream();

            if (inputStream == null) {
                throw new Exception("inputStream == null");
            }

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ex) {
                    Log.e(TAG, "executeRequest: " + ex.getMessage(), ex);
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void addProperties(final Request pRequest, final URLConnection pConnection) throws Exception {
        final Map<String, String> headers = pRequest.getHeaders();
        final String bodyString = pRequest.getBody();

        if (headers != null) {
            for (final String key : headers.keySet()) {
                pConnection.addRequestProperty(key, headers.get(key));
            }
        }

        if (bodyString != null) {
            final byte[] body = bodyString.getBytes(StringUtils.UTF8);
            final OutputStream stream = pConnection.getOutputStream();
            stream.write(body);
            stream.close();
        }
    }
}
