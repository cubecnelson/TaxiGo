package com.example.sans.myapplication.Utility;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Service.GetStatusService;
import com.example.sans.myapplication.Utility.Calendar.CalendarFragment;
import com.example.sans.myapplication.Utility.EditProfile.EditProfileFragment;
import com.example.sans.myapplication.Utility.Gift.GiftHistoryFragment;
import com.example.sans.myapplication.Utility.Gift.GiftInfoFragment;
import com.example.sans.myapplication.Utility.History.HistoryFragment;
import com.example.sans.myapplication.Utility.Mission.MissionFragment;
import com.example.sans.myapplication.Utility.News.NewsFragment;
import com.example.sans.myapplication.Utility.OrderList.OrderListFragment;
import com.example.sans.myapplication.Utility.Points.PointsFragment;
import com.example.sans.myapplication.Utility.Service.ServiceFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

public class MenuItemActivity extends AppCompatActivity implements
        CalendarFragment.OnFragmentInteractionListener,
        HistoryFragment.OnFragmentInteractionListener,
        ServiceFragment.OnFragmentInteractionListener,
        GiftHistoryFragment.OnFragmentInteractionListener,
        NewsFragment.OnFragmentInteractionListener,
        PointsFragment.OnFragmentInteractionListener,
        GiftInfoFragment.OnFragmentInteractionListener,
        EditProfileFragment.OnFragmentInteractionListener,
        MissionFragment.OnFragmentInteractionListener
        {

    private Toolbar toolbar;
    private TextView action_bar_title;
    private ImageView right;
    private ImageView left;
    private int item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        right = (ImageView) toolbar.findViewById(R.id.finish_activity);

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        left = (ImageView) toolbar.findViewById(R.id.back);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slide_in3, R.anim.activity_slide_in4);
            }
        });

        Intent i = getIntent();


        action_bar_title = (TextView) toolbar.findViewById(R.id.menu_item_title);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new CalendarFragment();
        item = i.getIntExtra("MENU_ITEM", 0);
        switch(item) {
            case 0:
                action_bar_title.setText("帳戶");
                fragment = new EditProfileFragment();
                left.setEnabled(false);
                break;
            case R.id.timetable:
                action_bar_title.setText(R.string.timetable_string);
                fragment = new CalendarFragment();
                left.setEnabled(false);
                break;
            case R.id.service:
                action_bar_title.setText(R.string.service_string);
                fragment = new ServiceFragment();
                break;
            case R.id.history:
                action_bar_title.setText(R.string.history_string);
                fragment = new HistoryFragment();
                break;
            case R.id.mission:
                action_bar_title.setText(R.string.mission_string);
                fragment = new MissionFragment();
                left.setEnabled(false);
                break;
            case R.id.points:
                action_bar_title.setText(R.string.points_string);
                fragment = new PointsFragment();
                left.setEnabled(false);
                break;
            case R.id.notice:
                action_bar_title.setText(R.string.notice_string);
                fragment = new NewsFragment();
                left.setEnabled(false);
                break;

            case R.id.gift_history:
                action_bar_title.setText(R.string.gift_history);
                fragment = new GiftHistoryFragment();
                right.setEnabled(false);
                right.setImageDrawable(null);
                left.setImageResource(R.drawable.arrow_left);
                break;

            case 1:
                action_bar_title.setText(R.string.gift_info);
                fragment = new GiftInfoFragment();
                right.setEnabled(false);
                right.setImageDrawable(null);
                left.setImageResource(R.drawable.arrow_left);
                break;

        }

        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_item, menu);
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
            public void onBackPressed() {
                super.onBackPressed();
                if(item == R.id.gift_history)
                    overridePendingTransition(R.anim.activity_slide_in3, R.anim.activity_slide_in4);
            }

            @Override
    public void onFragmentInteraction(Uri uri) {

    }


        }
