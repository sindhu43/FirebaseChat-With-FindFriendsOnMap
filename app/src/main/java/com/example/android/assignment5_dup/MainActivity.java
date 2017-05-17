package com.example.android.assignment5_dup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;


/**
 * References : http://www.thecrazyprogrammer.com/2016/10/android-real-time-chat-application-using-firebase-tutorial.html
 */

public class MainActivity extends AppCompatActivity {

    FrameLayout mframe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mframe = (FrameLayout) findViewById(R.id.mapFrame);
        PostFragment postFragment = new PostFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragtran = fm.beginTransaction();
        fragtran.replace(R.id.mapFrame, postFragment);
        fragtran.addToBackStack(null);
        fragtran.commit();


    }
}

