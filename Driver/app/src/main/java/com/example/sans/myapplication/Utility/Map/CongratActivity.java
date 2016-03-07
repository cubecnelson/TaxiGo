package com.example.sans.myapplication.Utility.Map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.MenuItemActivity;

public class CongratActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mission;
    private Button points;
    private Button main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrat);

        mission = (Button) findViewById(R.id.mission);
        points = (Button) findViewById(R.id.points);
        main = (Button) findViewById(R.id.main);
        mission.setOnClickListener(this);
        points.setOnClickListener(this);
        main.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mission: case R.id.points:
                Intent i = new Intent(CongratActivity.this, MenuItemActivity.class);
                i.putExtra("MENU_ITEM", v.getId());
                CongratActivity.this.startActivity(i);
                break;
            case R.id.main:
                break;
        }
        finish();
    }
}
