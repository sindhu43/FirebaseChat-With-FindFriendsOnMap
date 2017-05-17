package com.example.android.assignment5_dup;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by SINDHU on 14-04-2017.
 */

public class Login extends AppCompatActivity {


    EditText user,pass;
    Button loginBtn,signUpBtn;
    FirebaseAuth mFirebaseAuth;
    private static final int RC_SIGN_IN = 200;
    private static final String PATH_TOS = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginBtn=(Button) findViewById(R.id.login);
        signUpBtn=(Button) findViewById(R.id.signup);

        if (isUserLogin()) {
            loginUser();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loginBtn = (Button) findViewById(R.id.login);
        signUpBtn = (Button) findViewById(R.id.signup);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setTosUrl(PATH_TOS)
                        .build(), RC_SIGN_IN);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                loginUser();
            }
            if(resultCode == RESULT_CANCELED){
                displayMessage(getString(R.string.signin_failed));
            }
            return;
        }


    }
    private boolean isUserLogin(){
        if(mFirebaseAuth != null){
            String user=mFirebaseAuth.getCurrentUser().toString();
            if(user!=null) {
                return true;
            }
        }
        return false;
    }
    private void loginUser(){
        String user="";
        Intent loginIntent = new Intent(Login.this, Success.class);
        startActivity(loginIntent);
        finish();
    }
    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
