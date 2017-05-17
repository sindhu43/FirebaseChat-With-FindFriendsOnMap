package com.example.android.assignment5_dup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SINDHU on 20-03-2017.
 */

public class MapDummy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapdummy);
        String queries=getIntent().getStringExtra("queries");
        String parameters=getIntent().getStringExtra("parameters");
        Bundle arguments=new Bundle();
        arguments.putString("queries",queries);
        arguments.putString("parameters",parameters);
        MapViewFilter mapView = new MapViewFilter();
        mapView.setArguments(arguments);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragtran = fm.beginTransaction();
        fragtran.replace(R.id.mapHolder, mapView);
        fragtran.addToBackStack(null);
        fragtran.commit();
    }
}

