package com.example.sans.myapplication.Client;

/**
 * Created by sans on 20/11/15.
 */
import com.loopj.android.http.*;

public class Client  {
    private static final String BASE_URL = "http://taxigomo.com:22023/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();



    public static void addHeader(String header, String value){
        client.addHeader(header,value);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void get(String image, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(image, fileAsyncHttpResponseHandler);

    }
}
