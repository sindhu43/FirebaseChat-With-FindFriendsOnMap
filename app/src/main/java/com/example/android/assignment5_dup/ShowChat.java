package com.example.android.assignment5_dup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by SINDHU on 16-04-2017.
 */

public class ShowChat extends AppCompatActivity {
    Bundle bundle = new Bundle();
    Button filter,more;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        filter=(Button)findViewById(R.id.filterbtn) ;
        more = (Button) findViewById(R.id.moreUsers);
        filter.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        bundle = getIntent().getExtras();
        ArrayList<ChatView> al;
        al = (ArrayList<ChatView>) bundle.getSerializable("chat");
        Log.d("message", "Chat View =" + al);
    }
}
