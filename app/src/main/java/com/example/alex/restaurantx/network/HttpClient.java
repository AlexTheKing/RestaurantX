package com.example.alex.restaurantx.network;

import android.os.AsyncTask;

import com.example.alex.restaurantx.callbacks.IResultCallback;

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

    private String executeRequest(final Request pRequest) throws Exception {
        InputStream inputStream = null;
        HttpURLConnection connection;
        if (pRequest.getUrl().contains("https")) {
            connection = (HttpsURLConnection) getConnection(pRequest);
        } else {
            connection = (HttpURLConnection) getConnection(pRequest);
        }
        try {
            inputStream = connection.getInputStream();
            if (inputStream == null) throw new Exception("inputStream == null");
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
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public URLConnection getConnection(final Request pRequest) throws Exception {
        final String urlString = pRequest.getUrl();
        if (urlString == null) {
            throw new IllegalArgumentException("No url provided");
        }
        final URL url = new URL(urlString);
        final URLConnection connection = url.openConnection();
        if (connection == null) throw new Exception("connection == null");
        addProperties(pRequest, connection);
        return connection;
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
            final byte[] body = bodyString.getBytes("UTF-8");
            final OutputStream stream = pConnection.getOutputStream();
            stream.write(body);
            stream.close();
        }
    }

    public void makeAsyncRequest(final Request pRequest, final IResultCallback<String> pCallback) {
        new AsyncTask<Request, Void, String>() {
            @Override
            protected String doInBackground(final Request... requests) {
                String response;
                try {
                    response = executeRequest(requests[0]);
                } catch (Exception e) {
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
}
