package com.example.android.assignment5_dup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by SINDHU on 14-04-2017.
 */

public class Success extends AppCompatActivity {

    FirebaseAuth auth;
    TextView welcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        auth= FirebaseAuth.getInstance();
        String user= getIntent().getStringExtra("username");
        Log.d("message","Username="+user);
        welcome = (TextView) findViewById(R.id.welcomeText);
        if(auth!=null) {
            welcome.setText("Welcome " + auth.getCurrentUser().getEmail());
        }

    }


    //inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewmode:
                Log.d("message","inside view mode");
                return true;
            case R.id.submenu1:
                Intent intent = new Intent(Success.this,ListViewAllUsers.class);
                startActivity(intent);
                return true;
            case R.id.submenu2:
                Intent intentMap = new Intent(Success.this,MapViewDummy.class);
                startActivity(intentMap);
                return true;
            case R.id.signout:
                Intent intentLogin = new Intent(Success.this,Login.class);
                startActivity(intentLogin);
                finish();
                Toast.makeText(this,"Successfully logged out!",Toast.LENGTH_SHORT);
                return true;
        }
        return true;
    }
}



