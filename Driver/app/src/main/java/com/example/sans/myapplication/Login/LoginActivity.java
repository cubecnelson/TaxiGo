package com.example.sans.myapplication.Login;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Login.Fragment.LoginBotFragment;
import com.example.sans.myapplication.Login.Fragment.LoginTopFragment;
import com.example.sans.myapplication.Login.Fragment.OnlineBotFragment;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.MainActivity;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends FragmentActivity implements OnlineBotFragment.OnFragmentInteractionListener, LoginBotFragment.OnFragmentInteractionListener, LoginTopFragment.OnFragmentInteractionListener {


    private Button login_button;
    private Button key_button;
    private Spinner country_code;
    private EditText tel_no;
    private EditText verifyCode;
    private AppController controller;
    private SharedPreferences shares;
    private FrameLayout login_top;
    private FrameLayout login_bot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment newFragment = new LoginBotFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.login_bot, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        transaction = getFragmentManager().beginTransaction();
        newFragment = new LoginTopFragment();
        transaction.replace(R.id.login_top, newFragment);
        transaction.commit();


        shares = getSharedPreferences("SHARES", 0);



    }


    @Override
    protected void onResume() {
        super.onResume();
        /*
        if(shares.getBoolean("LOGIN", true)){
                OnlineBotFragment newFragment = new OnlineBotFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.setCustomAnimations(R.anim.fadein,
                        R.anim.fadeout);
                transaction.replace(R.id.login_bot, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();

        }*/
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


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
