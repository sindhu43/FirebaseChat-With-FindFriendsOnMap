package com.example.android.assignment5_dup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SINDHU on 16-04-2017.
 */

public class DisplayChat extends AppCompatActivity {

    ListView listView;
    Button moreUsers, filter;
    DatabaseReference mRef;
    String response;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        String url = "https://assignment5dup.firebaseio.com/messages.json";
        listView = (ListView) findViewById(R.id.userlist);
        moreUsers = (Button) findViewById(R.id.moreUsers);
        moreUsers.setVisibility(View.GONE);
        filter = (Button) findViewById(R.id.filterbtn);
        filter.setVisibility(View.GONE);

        mRef = FirebaseDatabase.getInstance().getReference();
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                response=s;
                doOnSuccess(s);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(DisplayChat.this);
        rQueue.add(request);
        

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String user=(String) adapterView.getItemAtPosition(i);
                UserDetails.chatWith=user;
                Intent intentChat = new Intent(DisplayChat.this,Chat.class);
                startActivity(intentChat);

            }
        });


    }

    public void doOnSuccess(String s) {
        ArrayList<String> al = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(s);
            Iterator i = obj.keys();
            while (i.hasNext()) {
                String key = i.next().toString();
                    String[] keys = key.split("_");
                    al.add(keys[1]);
                ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(DisplayChat.this, android.R.layout.simple_list_item_1, al);
                listView.setAdapter(userAdapter);
            }

            Log.d("message", "Users retrieved for chat screen= " + al);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
