package com.example.android.assignment5_dup;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * Created by SINDHU on 19-03-2017.
 */

public class ListViewAllUsers extends AppCompatActivity {
    ListView lview;
    String[] data;
    Button okbtn,morebtn,chatbtn,filterbtn;
    int count=0,limit=0,offset=25;
    String userArray,userarray,result,fullResult="",extra,nickname;
    ArrayList<String> lists = new ArrayList<String>();
    ArrayList<String> moreUsers = new ArrayList<String>();
    ArrayList<String> moreU = new ArrayList<String>();
    HttpURLConnection urlConnection = null;
    private DBHelper dbHelper;
    SQLiteDatabase db;
    Boolean returned;
    int RESULT_CODE=123;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        lview = (ListView) findViewById(R.id.userlist);
        okbtn= (Button) findViewById(R.id.okaybtn);
        morebtn = (Button) findViewById(R.id.moreUsers);
        chatbtn = (Button) findViewById(R.id.chatbtn);
        filterbtn = (Button) findViewById(R.id.filterbtn);
        dbHelper = new DBHelper(this);


        lists=getAllUsers(limit);
        if(lists.size()>0){
            Log.d("message","data present in the database");
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
            lview.setAdapter(itemsAdapter);
        }
        else {
            Log.d("message","data not on db,downloading from server");
            try {
                DownloadWebpageTask userdownload = new DownloadWebpageTask();
                userarray = userdownload.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&page=0&pagesize=20").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("message", "First page of user List from server" + userarray);
            ArrayList<String> users = new ArrayList<String>();
            try {
                Log.d("message","inside try loop");
                JSONArray array = new JSONArray(userarray);
                Log.d("message","jobj "+array);
                Log.d("message", "jobj " + array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject userJSON = array.getJSONObject(i);
                    //Log.d("message","jsonobject "+userJSON);
                    result = "Nick Name: " + userJSON.getString("nickname") + " Country: " + userJSON.getString("country") + " State: " + userJSON.getString("state") + " Year: " + userJSON.getString("year");
                    //Log.d("message","result= "+result);
                    fullResult=userJSON.getString("nickname")+","+userJSON.getString("country")+","+userJSON.getString("state")+","+userJSON.getString("city")+","+userJSON.getString("year")+","+userJSON.getString("latitude")+","+userJSON.getString("longitude");
                    lists.add(result);
                    Log.d("message","user="+fullResult);
                    users.add(fullResult);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.d("message","data got from server "+lists.size());
//            Log.d("message","data from server to db "+users.size());
            returned=insertUser(users);
            Log.d("message","from db: "+returned);
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lists);
            lview.setAdapter(itemsAdapter);
        }


        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                limit=limit+offset;
                moreUsers=getAllUsers(limit);
                Log.d("message","users from data base"+moreUsers);
                Log.d("message","user list from database"+moreUsers.size());
                if(moreUsers.size()>0){
                    Log.d("message","there is data in the database");
                    ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(ListViewAllUsers.this, android.R.layout.simple_list_item_1, moreUsers);
                    lview.setAdapter(userAdapter);
                }
                else {
                    Log.d("message", "No data in the database,downloading from server");
                    DownloadWebpageTask userdownloads = new DownloadWebpageTask();
                    try {
                        userArray = userdownloads.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&page=" + count+"&pagesize=20").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Log.d("message", "user List from server" + userArray);
                    try {
                        JSONArray jObj = new JSONArray(userArray);
                        Log.d("message", "jobj" + jObj.length());
                        for (int i = 0; i < jObj.length(); i++) {
                            JSONObject userJSON = jObj.getJSONObject(i);
                            String moreresult = "Nick Name: " + userJSON.getString("nickname") + " Country: " + userJSON.getString("country") + " State: " + userJSON.getString("state") + " Year: " + userJSON.getString("year");
                            moreUsers.add(moreresult);
                            extra = userJSON.getString("nickname") + "," + userJSON.getString("country") + "," + userJSON.getString("state") + ","+userJSON.getString("city")+ "," + userJSON.getString("year")+","+userJSON.getString("latitude")+","+userJSON.getString("longitude");
                            moreU.add(extra);

                        }
                        Log.d("message", "data from the server" + moreUsers);
                        Log.d("message", "data from the server to the db" + moreU);
                        Boolean more = insertUser(moreU);
                        Log.d("message", "response from db" + more);
                        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(ListViewAllUsers.this, android.R.layout.simple_list_item_1, moreUsers);
                        lview.setAdapter(userAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNew = new Intent(getApplicationContext(), FilterUsers.class);
                startActivity(intentNew);
            }
        });

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int j;
                chatbtn.setVisibility(View.VISIBLE);
                String ldata=(String)adapterView.getItemAtPosition(i);
                data = ldata.split( ":");
                nickname=data[1];
                Log.d("message","Name selected "+nickname);
            }
        });

        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            UserDetails.chatWith=nickname;
                            Intent intent = new Intent(ListViewAllUsers.this,Chat.class);
                            startActivity(intent);
                        }


        });

    }



    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                Log.e("rew", "Error accessing " + params[0], e);
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        protected void onPostExecute(Double result){
            //pb.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        private String downloadUrl(String urlString) throws IOException {
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                int contentLength = urlConnection.getContentLength();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    // handle error here
                    Log.d("message","Server not happy") ;
                }
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String result= bf.readLine();
                Log.d("message","data from server "+result);
                return result;
            } catch(MalformedURLException badURL) {
                Log.e("rew", "Bad URL", badURL);
            } catch (IOException io) {
                Log.e("rew", "network issue", io);
            }
            finally {
                urlConnection.disconnect();
            }
            return "";//error
        }


    }


    public ArrayList<String> getAllUsers(int n) {
        ArrayList<String> userList = new ArrayList<String>();
         db = dbHelper.getReadableDatabase();
        Cursor c= db.rawQuery("select * from users limit 20"+" offset "+ n,null);
        c.moveToFirst();
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String user = "Nick Name: " + c.getString(c.getColumnIndex("nickname")) + " Country: " + c.getString(c.getColumnIndex("country")) + " State: " + c.getString(c.getColumnIndex("state")) + " Year: " + c.getInt(c.getColumnIndex("year"));
                userList.add(user);
            }
        }
        return userList;
    }

    public boolean insertUser(ArrayList<String> userData){
        int i,count=0;
        Log.d("message","Data going to database "+userData);
        db = dbHelper.getWritableDatabase();
        for(i=0;i<userData.size();i++){
            String eachUser=userData.get(i);
            String[] user = eachUser.split(",");
            ContentValues content = new ContentValues();
            content.put("nickname", user[0]);
            content.put("country", user[1]);
            content.put("state", user[2]);
            content.put("city", user[3]);
            content.put("year", Integer.parseInt(user[4]));
            content.put("latitude", Double.parseDouble(user[5]));
            content.put("longitude", Double.parseDouble(user[6]));
            long row=db.insert("users", null, content);
            Log.d("message","inserted? "+row);
            if(row>0) {
                count++;
            }
        }
        Log.d("message","inserted ="+count);
        return true;
    }

    public int getID(){
        int id=0;
        db=dbHelper.getReadableDatabase();
        Cursor c= db.rawQuery("select MAX(ID) from users",null);
        if(c.getCount()>0){
            c.moveToFirst();
            id = c.getInt(0);
        }
        return id;
    }



}
