package com.example.android.assignment5_dup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SINDHU on 15-04-2017.
 */

public class MapViewDummy extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapdummy);
        MapView maps = new MapView();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragtran = fm.beginTransaction();
        fragtran.replace(R.id.mapHolder, maps);
        fragtran.addToBackStack(null);
        fragtran.commit();
    }
}
