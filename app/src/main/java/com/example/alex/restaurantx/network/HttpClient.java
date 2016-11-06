package com.example.alex.restaurantx.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.alex.restaurantx.callbacks.IResultCallback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

    private static HttpClient sClient;

    private HttpClient() {
    }

    public static HttpClient getInstance() {
        if (sClient == null) {
            sClient = new HttpClient();
        }
        return sClient;
    }

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public URLConnection getConnection(final Request pRequest) throws Exception {
        String urlString = pRequest.getUrl();
        if (urlString == null) {
            throw new IllegalArgumentException("No url provided");
        }
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        if (connection == null) throw new Exception("connection == null");
        addProperties(pRequest, connection);
        return connection;
    }

    private void addProperties(final Request pRequest, URLConnection pConnection) throws Exception {
        Map<String, String> headers = pRequest.getHeaders();
        String bodyString = pRequest.getBody();
        if (headers != null) {
            for (String key : headers.keySet()) {
                pConnection.addRequestProperty(key, headers.get(key));
            }
        }
        if (bodyString != null) {
            byte[] body = bodyString.getBytes("UTF-8");
            OutputStream stream = pConnection.getOutputStream();
            stream.write(body);
            stream.close();
        }
    }

    public void makeAsyncRequest(final Request pRequest, final IResultCallback<String> pCallback) {
        new AsyncTask<Request, Void, String>() {
            @Override
            protected String doInBackground(Request... requests) {
                String response;
                try {
                    response = executeRequest(requests[0]);
                } catch (Exception e) {
                    Log.e("HTTPCLIENT", "doInBackground: ", e);
                    response = e.toString();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String pResult) {
                pCallback.onSuccess(pResult);
                super.onPostExecute(pResult);
            }
        }.execute(pRequest);
    }
}
