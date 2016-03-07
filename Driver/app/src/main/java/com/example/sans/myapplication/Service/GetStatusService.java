package com.example.sans.myapplication.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class GetStatusService extends Service {
    private Handler handler = new Handler();
    private AppController controller = (AppController) getApplication();
    private SharedPreferences shares;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(showTime, 2000);




        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }

    private Runnable showTime = new Runnable() {
        public void run() {

            shares = getSharedPreferences("SHARES", 0);

            final Client client = new Client();

            RequestParams params = new RequestParams();
            params.put("id", shares.getInt("ID", 0));

            final JSONObject[] driver_data = new JSONObject[1];

            final int status_code[] = new int[1];

            status_code[0] = 689;


            client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

            client.post("driver/getStatus", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Intent i = new Intent();
                    i.putExtra("driver_data", statusCode);
                    i.setAction("android.intent.action.test");
                    try {
                        i.putExtra("driver_data", response.getJSONObject("data").toString());
                        i.putExtra("status_code", ""+statusCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sendBroadcast(i);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                    Intent i = new Intent();
                    i.putExtra("status_code", ""+statusCode);
                    i.setAction("android.intent.action.test");

                    sendBroadcast(i);
                }
            });

            handler.postDelayed(this, 2000);


        }
    };
}
