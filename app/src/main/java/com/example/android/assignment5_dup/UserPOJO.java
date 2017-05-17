package com.example.android.assignment5_dup;

/**
 * Created by SINDHU on 14-04-2017.
 */

public class UserPOJO  {

    String email;
    String password;

    public UserPOJO(){}


    public UserPOJO( String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
