package com.example.sans.myapplication.Utility.Map;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.Calendar.CalendarFragment;
import com.example.sans.myapplication.Utility.EditProfile.EditProfileFragment;
import com.example.sans.myapplication.Utility.History.HistoryFragment;
import com.example.sans.myapplication.Utility.MainActivity;
import com.example.sans.myapplication.Utility.MenuItemActivity;
import com.example.sans.myapplication.Utility.News.NewsFragment;
import com.example.sans.myapplication.Utility.Points.PointsFragment;
import com.example.sans.myapplication.Utility.Service.ServiceFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends AppCompatActivity implements MapsGetOnFragment.OnFragmentInteractionListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    //Defining Variables
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean nav = false;
    private JSONObject j;
    private MyReceiver receiver;
    private AppController controller;
    private SharedPreferences shares;
    private View header;
    private ImageView pass_phone;
    private ImageView pass_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        shares = getSharedPreferences("SHARES", 0);

        controller = (AppController) getApplication();

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        this.registerReceiver(receiver, filter);


        Intent i = getIntent();

        Bundle bundle = i.getExtras();

        try {
                j = new JSONObject(bundle.getString("data", "ERROR"));
                shares.edit().putString("ORDER", bundle.getString("data", "ERROR")).commit();
        } catch (JSONException e) {
            try {
                j = new JSONObject(shares.getString("ORDER", "ERROR"));
            } catch (JSONException e1) {
                SharedPreferences.Editor editor = shares.edit();
                final Client client = new Client();
                RequestParams params = new RequestParams();
                params.put("id", shares.getInt("ID", 0));
                params.put("status", 999);
                client.post("driver/setStatus", params, new JsonHttpResponseHandler());

                editor.putBoolean("ONLINE", false);
                editor.putBoolean("LOGIN", false);
                editor.commit();
                Intent intent = getApplicationContext().getPackageManager()
                        .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        try {
            Log.e("PHONE_NO", String.valueOf(j.getJSONObject("passenger_data")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pass_phone = (ImageView) findViewById(R.id.pass_phone);
        pass_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = null;
                try {
                    phoneIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + j.getJSONObject("passenger_data").getString("tel")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(phoneIntent);
            }
        });
        pass_msg = (ImageView) findViewById(R.id.pass_msg);
        pass_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msgIntent = new Intent(Intent.ACTION_VIEW);

                try {
                    msgIntent.setData(Uri.parse("sms:" + j.getJSONObject("passenger_data").getString("tel")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                startActivity(msgIntent);
            }
        });


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TextView actionBarTitle = (TextView) toolbar.findViewById(R.id.action_bar_title);

        try {
            actionBarTitle.setText(j.getJSONObject("passenger_data").getString("family_name") + j.getJSONObject("passenger_data").getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setUpMapIfNeeded();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        header = navigationView.inflateHeaderView(R.layout.header);
        JSONObject driver_data = null;
        try {
            driver_data = new JSONObject(shares.getString("DRIVER_DATA", "ERROR"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView driver_name_header = (TextView) header.findViewById(R.id.username);
        TextView car_id_header = (TextView) header.findViewById(R.id.car_id);
        final ImageView pp_header = (ImageView) header.findViewById(R.id.profile_image);

        try {
            car_id_header.setText(driver_data.getString("license"));
            driver_name_header.setText(driver_data.getString("name"));
            Client client = new Client();
            client.get(driver_data.getString("image"), new FileAsyncHttpResponseHandler(MapsActivity.this.getApplicationContext()) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final File response) {


                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap image = BitmapFactory.decodeFile(response.getPath());

                            pp_header.setImageBitmap(image);

                        }
                    });


                }


            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapsActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", 0);
                MapsActivity.this.startActivity(i);
                drawerLayout.closeDrawers();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state

                nav = true;

                menuItem.setChecked(false);

                //Closing drawer on item click
                drawerLayout.closeDrawers();


                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    case R.id.timetable:
                    case R.id.service:
                    case R.id.history:
                    case R.id.mission:
                    case R.id.points:
                    case R.id.notice:
                        Intent i = new Intent(MapsActivity.this, MenuItemActivity.class);
                        i.putExtra("MENU_ITEM",menuItem.getItemId());
                        MapsActivity.this.startActivity(i);
                        break;
                    case R.id.login:
                        //Toast.makeText(getApplicationContext(), "Logout Selected", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = shares.edit();

                        final Client client = new Client();
                        RequestParams params = new RequestParams();
                        params.put("id", shares.getInt("ID", 0));
                        params.put("status", 999);
                        client.post("driver/setStatus", params, new JsonHttpResponseHandler());

                        editor.putBoolean("ONLINE", false);
                        editor.putBoolean("LOGIN", false);
                        editor.commit();
                        Intent intent = getApplicationContext().getPackageManager()
                                .getLaunchIntentForPackage( getApplicationContext().getPackageName() );
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    default:
                        //Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });




        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                drawerLayout.setSelected(false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed(){

    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                 .getMap();
            mMap.getUiSettings().setMapToolbarEnabled(false);
            // Check if we were successful in obtaining the map.

            if (mMap != null) {
                setUpMap();

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */



    protected void destination(){
        LatLng destination = null;
        try {
            destination = new LatLng(j.getJSONObject("order_data").getDouble("end_x"), j.getJSONObject("order_data").getDouble("end_y"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMap.clear();

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 18));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_end))
                .position(destination));
    }
    @Override
    public void finish(){
        super.finish();
        shares = getSharedPreferences("SHARES", 0);
        SharedPreferences.Editor editor = shares.edit();
        editor.putBoolean("MAPS", false);
        editor.commit();
    }
    private void setUpMap() {
        LatLng macau = null;
        try {
            macau = new LatLng(j.getJSONObject("order_data").getDouble("start_x"), j.getJSONObject("order_data").getDouble("start_y"));
            shares.edit().putString("ORDER", j.toString()).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(macau, 18));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_start))
                .position(macau));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        uri.getFragment();
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject driver_data = null;
            try {
                driver_data = new JSONObject(intent.getStringExtra("driver_data"));
                if (driver_data.getJSONObject("driver_data").getString("status").compareTo("999") == 0) {
                    finish();
                    getParent().onBackPressed();
                }else if(driver_data.getJSONObject("driver_data").getString("status").compareTo("1") == 0){
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
