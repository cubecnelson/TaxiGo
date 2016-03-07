package com.example.sans.myapplication.Utility;

import android.Manifest;
import android.app.Activity;
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.AssignActivity;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.EditProfile.EditProfileFragment;
import com.example.sans.myapplication.Utility.Map.MapsActivity;
import com.example.sans.myapplication.Utility.OrderList.OrderListFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements OrderListFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener, ConnectionCallbacks, OnConnectionFailedListener {

    private AppController controller;
    private SharedPreferences shares;
    //Defining Variables

    private LocationManager lm;
    private LocationListener ll;
    private ArrayList<Location> stored_locations;
    private String loc_array;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean nav = false;
    private TextView order_num;
    private View header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shares = getSharedPreferences("SHARES", 0);

        Client client = new Client();

        loc_array = "";

        RequestParams params = new RequestParams();
        params.put("id", shares.getInt("ID", -1));
        params.put("loc_array", loc_array);

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", ""));

        client.post("driver/updateLocation", params, new JsonHttpResponseHandler());


        Fragment newFragment = new OrderListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        LocationRequest lr = LocationRequest.create();
        lr.setInterval(1000);
        lr.setFastestInterval(500);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        stored_locations = new ArrayList<Location>();

        controller = (AppController) getApplication();

        controller.startGetStatus();
        controller.startAquireOrder();





        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        order_num = (TextView) toolbar.findViewById(R.id.order_num);

        order_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav = true;
                Intent i = new Intent(MainActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", R.id.timetable);
                MainActivity.this.startActivity(i);
            }
        });

        toolbar.findViewById(R.id.toolbar_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav = true;
                Intent i = new Intent(MainActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", R.id.timetable);
                MainActivity.this.startActivity(i);
            }
        });
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        //Location
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new myLocationListener();

        startLocationUpdates();


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu

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
            client = new Client();
            client.get(driver_data.getString("image"), new FileAsyncHttpResponseHandler(MainActivity.this.getApplicationContext()) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final File response) {


                    MainActivity.this.runOnUiThread(new Runnable() {
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
        } catch (NullPointerException e) {
            SharedPreferences.Editor editor = shares.edit();

            final Client client1 = new Client();
            RequestParams params1 = new RequestParams();

            editor.putBoolean("ONLINE", false);
            editor.putBoolean("LOGIN", false);
            editor.commit();
            Intent intent = getApplicationContext().getPackageManager()
                    .getLaunchIntentForPackage(getApplicationContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", 0);
                MainActivity.this.startActivity(i);
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
                switch (menuItem.getItemId()) {
                    case R.id.timetable:
                    case R.id.service:
                    case R.id.history:
                    case R.id.mission:
                    case R.id.points:
                    case R.id.notice:
                        Intent i = new Intent(MainActivity.this, MenuItemActivity.class);
                        i.putExtra("MENU_ITEM", menuItem.getItemId());
                        MainActivity.this.startActivity(i);
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
                                .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    protected void startLocationUpdates() {
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
    }

    protected void stopLocationUpdates() {
        lm.removeUpdates(ll);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = shares.edit();
        editor.putBoolean("ONLINE", false);
        editor.commit();
        final Client client = new Client();
        RequestParams params = new RequestParams();
        params.put("id", shares.getInt("ID", 0));
        params.put("status", 999);
        client.post("driver/setStatus", params, new JsonHttpResponseHandler());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("login", 1);
        setResult(Activity.RESULT_OK, resultIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.stopGetStatus();
        controller.stopAquireOrder();
    }

    @Override
    public void finish() {
        super.finish();
        controller.stopGetStatus();
        controller.stopAquireOrder();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
    }

    private class myLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                if (stored_locations.size() == 0)
                    stored_locations.add(location);
                stored_locations.add(location);
                if (stored_locations.size() >= 2) {
                    loc_array = "";
                    for (int i = 0; i < stored_locations.size(); i++) {
                        loc_array += stored_locations.get(i).getLatitude() + "," + stored_locations.get(i).getLongitude();
                        if (i < stored_locations.size() - 1)
                            loc_array += "|";
                    }


                    final Client client = new Client();

                    RequestParams params = new RequestParams();
                    params.put("id", shares.getInt("ID", -1));
                    params.put("loc_array", loc_array);

                    client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", ""));

                    client.post("driver/updateLocation", params, new JsonHttpResponseHandler());
                }

                if (stored_locations.size() > 20) {
                    for (int i = 0; i < 4; i++) {
                        stored_locations.remove(i);
                    }

                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    }




}