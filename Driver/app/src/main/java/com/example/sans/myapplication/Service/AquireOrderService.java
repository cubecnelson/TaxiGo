package com.example.sans.myapplication.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.OrderList.OrderListAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AquireOrderService extends Service {
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
        handler.postDelayed(showTime, 20000);

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
;

            client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

            client.post("OrderDistributor/aquireOrders", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Intent i = new Intent();
                    try {
                        i.putExtra("order_data", response.getJSONObject("data").toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    i.setAction("taxigo.api.aquireOrderService");
                    sendBroadcast(i);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {

                }
            });


            handler.postDelayed(this, 20000);


        }
    };
}
