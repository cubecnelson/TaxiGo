package com.example.sans.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Login.OnlineActivity;
import com.example.sans.myapplication.Service.AquireOrderService;
import com.example.sans.myapplication.Service.GetStatusService;
import com.example.sans.myapplication.Utility.MainActivity;
import com.example.sans.myapplication.Utility.Map.MapsActivity;
import com.example.sans.myapplication.Utility.NoConnectionActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sans on 23/11/15.
 */
public class AppController extends Application {
    private static AppController mInstance;
    private static HashMap<String,Object> map = new HashMap<String, Object>();

    private MyReceiver receiver;
    private SharedPreferences shares;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        this.registerReceiver(receiver, filter);


        shares = getSharedPreferences("SHARES", 0);


        SharedPreferences.Editor editor = shares.edit();
        editor.putBoolean("ASSIGN", false);
        editor.putBoolean("MAPS", false);
        editor.commit();

        final Client client = new Client();

        RequestParams params = new RequestParams();
        params.put("id", shares.getInt("ID", 0));

        if (shares.getBoolean("ONLINE", false)) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }else if (shares.getBoolean("LOGIN", false)){
            Intent i = new Intent(getApplicationContext(), OnlineActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
    public void putMapData(String key, Object value){
        map.put(key, value);
    }

    public Object getMapData(String key){
        return map.get(key);
    }

    public void startGetStatus() {
        Intent intent = new Intent(getApplicationContext(), GetStatusService.class);
        startService(intent);
    }

    public void startAquireOrder() {
        Intent intent = new Intent(getApplicationContext(), AquireOrderService.class);
        startService(intent);
    }

    public void stopAquireOrder() {
        Intent intent = new Intent(getApplicationContext(), AquireOrderService.class);
        stopService(intent);
    }

    public void stopGetStatus(){
        Intent intent = new Intent(getApplicationContext(), GetStatusService.class);
        stopService(intent);
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        stopGetStatus();
        stopAquireOrder();
        this.unregisterReceiver(receiver);
    }




    public class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject driver_data = null;
            if(intent.getStringExtra("status_code").compareTo("200") == 0) {
                try {
                    driver_data = new JSONObject(intent.getStringExtra("driver_data"));
                    //Toast.makeText(MainActivity.this, driver_data.getString("status"), Toast.LENGTH_SHORT).show();
                    if (driver_data.getJSONObject("driver_data").getString("status").compareTo("999") == 0) {

                    } else if (driver_data.getJSONObject("driver_data").getString("status").compareTo("3") == 0) {
                        if (!shares.getBoolean("MAPS", false)) {
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle extras = new Bundle();
                            SharedPreferences.Editor editor = shares.edit();
                            editor.putBoolean("MAPS", true);
                            editor.commit();
                            try {
                                extras.putString("data", driver_data.toString());
                                shares.edit().putString("order_data", driver_data.getJSONObject("data").toString());
                                shares.edit().commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i.putExtras(extras);
                            startActivity(i);
                        }
                    } else if (driver_data.getJSONObject("driver_data").getString("status").compareTo("2") == 0) {
                        if (!shares.getBoolean("ASSIGN", false)) {
                            Intent i = new Intent(getApplicationContext(), AssignActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle extras = new Bundle();
                            SharedPreferences.Editor editor = shares.edit();
                            editor.putBoolean("ASSIGN", true);
                            editor.commit();
                            try {
                                extras.putString("data", driver_data.toString());
                                shares.edit().putString("order_data", driver_data.getJSONObject("data").toString());
                                shares.edit().commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            i.putExtras(extras);
                            startActivity(i);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if (shares.getBoolean("ONLINE",true)&&shares.getBoolean("LOGIN",true)){
                stopGetStatus();
                stopAquireOrder();
                Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = shares.edit();

                final Client client = new Client();
                RequestParams params = new RequestParams();
                params.put("id", shares.getInt("ID", 0));
                params.put("status", 999);
                client.post("driver/setStatus", params, new JsonHttpResponseHandler());

                editor.putBoolean("ONLINE", false);
                editor.putBoolean("LOGIN", false);
                editor.commit();
                intent = getApplicationContext().getPackageManager()
                        .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}