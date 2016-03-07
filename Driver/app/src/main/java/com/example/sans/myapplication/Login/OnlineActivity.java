package com.example.sans.myapplication.Login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Login.Fragment.LoginBotFragment;
import com.example.sans.myapplication.Login.Fragment.LoginTopFragment;
import com.example.sans.myapplication.Login.Fragment.OnlineBotFragment;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.MenuItemActivity;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class OnlineActivity extends AppCompatActivity implements OnlineBotFragment.OnFragmentInteractionListener, LoginBotFragment.OnFragmentInteractionListener, LoginTopFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private boolean nav = false;
    private TextView order_num;
    private View header;
    private SharedPreferences shares;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        shares = getSharedPreferences("SHARES", 0);
        
        Fragment newFragment = new OnlineBotFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.login_bot, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        transaction = getFragmentManager().beginTransaction();
        newFragment = new LoginTopFragment();
        transaction.replace(R.id.login_top, newFragment);
        transaction.commit();


        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);



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
            Client client = new Client();
            client.get(driver_data.getString("image"), new FileAsyncHttpResponseHandler(OnlineActivity.this.getApplicationContext()) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final File response) {


                    OnlineActivity.this.runOnUiThread(new Runnable() {
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

                Intent i = new Intent(OnlineActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", 0);
                OnlineActivity.this.startActivity(i);
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
                        Intent i = new Intent(OnlineActivity.this, MenuItemActivity.class);
                        i.putExtra("MENU_ITEM", menuItem.getItemId());
                        OnlineActivity.this.startActivity(i);
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

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
