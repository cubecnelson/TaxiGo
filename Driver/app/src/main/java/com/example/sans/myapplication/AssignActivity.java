package com.example.sans.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Utility.EditProfile.EditProfileFragment;
import com.example.sans.myapplication.Utility.MenuItemActivity;
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


public class AssignActivity extends AppCompatActivity {
    private JSONObject j;
    private TextView actionBarTitle;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    //Defining Variables
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean nav = false;
    private Button reject;
    private Button accept;
    private View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        final SharedPreferences shares = getSharedPreferences("SHARES", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        actionBarTitle = (TextView) toolbar.findViewById(R.id.action_bar_title);

        Intent i = getIntent();

        Bundle bundle = i.getExtras();

        try {
            j = new JSONObject(bundle.getString("data", "ERROR"));


        } catch (JSONException e) {
            Toast.makeText(AssignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i = 30; i > 0; i--) {

                    try {
                        Thread.sleep(1000);
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actionBarTitle.setText(""+ finalI + "秒");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                Handler mainHandler = new Handler(AssignActivity.this.getMainLooper());

                Runnable myRunnable = new Runnable(){

                    @Override
                    public void run() {
                        final Client client = new Client();

                        RequestParams params = new RequestParams();

                        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

                        client.post("OrderDistributor/rejectOrder", params, new JsonHttpResponseHandler());
                    }
                };

                mainHandler.post(myRunnable);



                AssignActivity.this.finish();

            }
        }).start();





        setUpMapIfNeeded();


        //Initializing NavigationView

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
            client.get(driver_data.getString("image"), new FileAsyncHttpResponseHandler(AssignActivity.this.getApplicationContext()) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final File response) {


                    AssignActivity.this.runOnUiThread(new Runnable() {
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

                Intent i = new Intent(AssignActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", 0);
                AssignActivity.this.startActivity(i);
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
                        Intent i = new Intent(AssignActivity.this, MenuItemActivity.class);
                        i.putExtra("MENU_ITEM",menuItem.getItemId());
                        AssignActivity.this.startActivity(i);
                        break;
                    case R.id.login:
                        Toast.makeText(getApplicationContext(), "Logout Selected", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });


        reject = (Button) findViewById(R.id.reject);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Client client = new Client();

                RequestParams params = new RequestParams();

                client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

                client.post("OrderDistributor/rejectOrder", params, new JsonHttpResponseHandler());

                AssignActivity.this.finish();
            }
        });

        accept = (Button) findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Client client = new Client();

                final RequestParams params = new RequestParams();


                try {
                    params.put("orderId", j.getJSONObject("order_data").getDouble("id"));

                    client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));


                    client.post("OrderDistributor/takeOrder", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            Log.i("Status Code", "" + statusCode);
                        }
                    });

                    AssignActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



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

    @Override
    public void finish() {
        super.finish();
        SharedPreferences.Editor editor = getSharedPreferences("SHARES", 0).edit();
        editor.putBoolean("ASSIGN", false);
        editor.commit();
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

    private void setUpMap() {
        LatLng macau = null;
        try {
            macau = new LatLng(j.getJSONObject("order_data").getDouble("start_x"), j.getJSONObject("order_data").getDouble("start_y"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(macau, 18));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_start))
                .position(macau));
    }

}


