package com.example.android.assignment5_dup;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class ListViewFilteredUsers extends AppCompatActivity {
    ListView lviewfilter;
    Button okbtn,morebtn,filter;
    int count=0,afterid,page=0;
    SQLiteDatabase db;
    DBHelper dbHelper;
    String user_Array,users,result,param,query,results;
    ArrayList<String> serverUsers = new ArrayList<String>();
    ArrayList<String> moreServerUsers = new ArrayList<String>();
    ArrayList<String> dbUsers = new ArrayList<String>();
    ArrayList<String> moreDbUsers = new ArrayList<String>();
    ArrayList<String> extras = new ArrayList<String>();
    ArrayList<String> extraUsers = new ArrayList<String>();
    HttpURLConnection urlConnection = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        dbHelper = new DBHelper(this);
        param=getIntent().getStringExtra("parameter");
        query=getIntent().getStringExtra("queryParameter");
        Log.d("message","parameters to filter"+param);
        lviewfilter = (ListView) findViewById(R.id.userlist);
        okbtn= (Button) findViewById(R.id.okaybtn);
        morebtn = (Button) findViewById(R.id.moreUsers);
        filter = (Button) findViewById(R.id.filterbtn);
        filter.setVisibility(View.GONE);

        dbUsers=getFilteredUsers(count);
        if(dbUsers.size()>0){
            Log.d("message","data present in the database");
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbUsers);
            lviewfilter.setAdapter(itemsAdapter);
        }

        else {
            try {
                Log.d("message","query parameters"+query);
                Log.d("message","downloading data from server");
                afterid=getID();
                int pagenumber= afterid/20;
                Log.d("message","Maximum id in db is "+afterid);
                DownloadWebpageTask userdownloadfilter = new DownloadWebpageTask();
                user_Array = userdownloadfilter.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid"+afterid+"&"+query+"&page="+pagenumber+"&pagesize=20").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.d("message", "user List from server" + user_Array);
            try {
                JSONArray jObj = new JSONArray(user_Array);
                Log.d("message", "jobj" + jObj.length());
                for (int i = 0; i < jObj.length(); i++) {
                    JSONObject userJSON = jObj.getJSONObject(i);
                    result = userJSON.getString("nickname") + "," + userJSON.getString("country") + "," + userJSON.getString("state") + "," + userJSON.getString("city")+","+userJSON.getString("year")+","+userJSON.getString("latitude")+","+userJSON.getString("longitude");
                    serverUsers.add(result);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("message","users going to db"+serverUsers);

            int insert=insertUser(serverUsers);
            Log.d("message","inserted?? "+insert);
            if(insert>0){
                Log.d("message","getting newly inserted data from server");
                extraUsers=getFilteredUsers(afterid);
                Log.d("message","data newly inserted "+extraUsers );
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, extraUsers);
                lviewfilter.setAdapter(itemsAdapter);
            }
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
                int offset=20;
                count=count+offset;
                moreDbUsers=getFilteredUsers(count);
                Log.d("message","moreDbUsers "+moreDbUsers);
                if(moreDbUsers.size()>0){
                    Log.d("message","data present in the database");
                    ArrayAdapter<String> itemsAdapter =
                            new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, moreDbUsers);
                    lviewfilter.setAdapter(itemsAdapter);
                }
                else {
                    try {
                        afterid = getID();
                        Log.d("message", "downloading data from server");
                        Log.d("message","query parameter"+query);
                        Log.d("message", "Maximum id in db is " + afterid);
                        DownloadWebpageTask userdownloadfilter = new DownloadWebpageTask();
                        users = userdownloadfilter.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid="+afterid+"&"+query+"&page="+page+"&pagesize=20").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Log.d("message", "user List from server" + users);
                    try {
                        JSONArray jObj = new JSONArray(users);
                        Log.d("message", "jobj" + jObj.length());
                        for (int i = 0; i < jObj.length(); i++) {
                            JSONObject userJSON = jObj.getJSONObject(i);
                            results="Nick Name: " + userJSON.getString("nickname") + " Country: " + userJSON.getString("country") + " State: " + userJSON.getString("state") + " Year: " + userJSON.getString("year");
                            result = userJSON.getString("nickname") + "," + userJSON.getString("country") + "," + userJSON.getString("state") + "," + userJSON.getString("city") + "," + userJSON.getString("year") + "," + userJSON.getString("latitude") + "," + userJSON.getString("longitude");
                            moreServerUsers.add(result);
                            extras.add(results);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("message", "users going to db" + moreServerUsers);
                    if(moreServerUsers.size()>0){
                        ArrayAdapter<String> itemsAdapter =
                                    new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, extras);
                        lviewfilter.setAdapter(itemsAdapter);
                        int insert = insertUser(moreServerUsers);
                        Log.d("message", "inserted?? " + insert);
                        page++;
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No more users for this filter",Toast.LENGTH_SHORT);
                    }
                }
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

    public ArrayList<String> getFilteredUsers(int n) {
        ArrayList<String> userList = new ArrayList<String>();
        db = dbHelper.getReadableDatabase();
        Cursor c= db.rawQuery("select * from users where "+param+" limit 20"+" offset "+ n,null);
        c.moveToFirst();
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String user = "Nick Name: " + c.getString(c.getColumnIndex("nickname")) + " Country: " + c.getString(c.getColumnIndex("country")) + " State: " + c.getString(c.getColumnIndex("state")) + " Year: " + c.getInt(c.getColumnIndex("year"));
                userList.add(user);
            }
        }
        return userList;
    }

    public int insertUser(ArrayList<String> userData){
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
            //Log.d("message","inserted? "+row);
            if(row>0) {
                count++;
            }
        }
        Log.d("message","inserted ="+count);
        return count;
}


    public int getID(){
        int id=0;
        db=dbHelper.getReadableDatabase();
        Cursor c= db.rawQuery("select MAX(ID) from users where "+param,null);
        if(c.getCount()>0){
            c.moveToFirst();
            id = c.getInt(0);
        }
        return id;
    }

}
