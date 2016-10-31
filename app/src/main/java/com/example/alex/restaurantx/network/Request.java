package com.example.alex.restaurantx.network;

import java.util.Map;

public class Request {

    private String mUrl;
    private Map<String, String> mHeaders;
    private String mBody;
    private String mMethod;

    public String getMethod() {
        return mMethod;
    }

    private void setMethod(String mMethod) {
        this.mMethod = mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    private void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    private void setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public String getBody() {
        return mBody;
    }

    private void setBody(String mBody) {
        this.mBody = mBody;
    }

    public static class Builder {

        private Request request = new Request();

        public Builder setUrl(String pUrl) {
            this.request.setUrl(pUrl);
            return this;
        }

        public Builder setHeaders(Map<String, String> pHeaders) {
            this.request.setHeaders(pHeaders);
            return this;
        }

        public Builder setBody(String pBody) {
            this.request.setBody(pBody);
            return this;
        }

        public Builder setMethod(String pMethod) {
            this.request.setMethod(pMethod);
            return this;
        }

        public Request build() {
            return request;
        }
    }

}
