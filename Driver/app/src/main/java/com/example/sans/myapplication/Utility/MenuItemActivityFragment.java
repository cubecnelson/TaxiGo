package com.example.sans.myapplication.Utility;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sans.myapplication.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuItemActivityFragment extends Fragment {

    public MenuItemActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_item, container, false);
    }
}
