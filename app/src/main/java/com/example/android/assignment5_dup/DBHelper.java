package com.example.android.assignment5_dup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SINDHU on 03-04-2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "users";
    private static final String COL1 = "ID";
    private static final String COL2 = "NICKNAME";
    private static final String COL3 = "PASSWORD";
    private static final String COL4 = "COUNTRY";
    private static final String COL5 = "STATE";
    private static final String COL6 = "YEAR";
    private static final String COL7 = "LATITUDE";
    private static final String COL8 = "LONGITUDE";

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( ID integer primary key autoincrement,nickname text UNIQUE,country text,state text,city text,year integer,latitude real,longitude real);";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}

