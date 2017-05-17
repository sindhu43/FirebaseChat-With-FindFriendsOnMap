package com.example.android.assignment5_dup;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
 * Created by SINDHU on 21-03-2017.
 */

public class MapViewFilter extends Fragment {

    SupportMapFragment mapFragment;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    SQLiteDatabase db;
    DBHelper dbHelper;
    String current;
    private View rootView;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private com.google.android.gms.maps.MapView mapView;
    private GoogleMap gMap;
    Bundle usersbundle;
    HttpURLConnection urlConnection;
    String queries,parameters,user_Array,results,serverusers,finalString;
    ArrayList<String> Dbusers = new ArrayList<String>();
    ArrayList<String> moreDbusers = new ArrayList<String>();
    ArrayList<String> toServer = new ArrayList<String>();
    ArrayList<String> moreToServer = new ArrayList<String>();
    int count=0,afterid;
    Button more;
    String url = "https://assignment5dup.firebaseio.com/users.json";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mapfragment, container, false);
        usersbundle=getArguments();
        more=(Button)rootView.findViewById(R.id.mapBtn);
         parameters=usersbundle.getString("queries");
        queries = usersbundle.getString("parameters");
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        dbHelper = new DBHelper(getActivity());


        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    gMap = map;
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            current=marker.getTitle();
                            UserDetails.chatWith=current;
                            Intent intent = new Intent(getActivity(),Chat.class);
                            startActivity(intent);
                            return true;
                        }

                    });
                    Dbusers=getFilteredUsers(count);
                    if(Dbusers.size()>0) {
                        Log.d("message", "data present in the database");
                        Log.d("message","data from db"+Dbusers);
                        int i;
                        String[] array;
                        for (i = 0; i < Dbusers.size(); i++) {
                            array = Dbusers.get(i).split(",");
                            LatLng latlng = new LatLng(Double.parseDouble(array[1]), Double.parseDouble(array[2]));
                            Log.d("message", "Lat Long " + latlng);
                            handleNewLocation(latlng, array[0]);
                        }
                    }
                    else{
                        try {
                            Log.d("message","query parameters"+queries);
                            Log.d("message","downloading data from server");
                            afterid=getID();
                            int pagenumber= afterid/20;
                            Log.d("message","Maximum id in db is "+afterid);
                            DownloadWebpageTask userdownloadfilter = new DownloadWebpageTask();
                            user_Array = userdownloadfilter.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid"+afterid+"&"+queries+"&page="+pagenumber+"&pagesize=20").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                            try {
                                JSONArray jObj = new JSONArray(user_Array);
                                Log.d("message", "jsonarray" + jObj);
                                Log.d("message", "jobj " + jObj.length());
                                for (int i = 0; i < jObj.length(); i++) {
                                    //Log.d("message", "inside for loop");
                                    JSONObject userJSON = jObj.getJSONObject(i);
                                    Log.d("message", "inside for loop" + userJSON);
                                    LatLng latlng = new LatLng(userJSON.getDouble("latitude"), userJSON.getDouble("longitude"));
                                    Log.d("message","latlng"+latlng);
                                    //lists.add(latlng);
                                    results=userJSON.getString("nickname")+","+userJSON.getString("country")+","+userJSON.getString("state")+","+userJSON.getString("city")+","+userJSON.getString("year")+","+userJSON.getString("latitude")+","+userJSON.getString("longitude");
                                    handleNewLocation(latlng, userJSON.getString("nickname"));
                                    toServer.add(results);
                                    Log.d("message","Data going to server"+toServer);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int inserted=insertUser(toServer);
                        }
                }
            });


        }

        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int offset=20;
                count=count+offset;
                moreDbusers=getFilteredUsers(count);
                if(moreDbusers.size()>0) {
                    Log.d("message", "data present in the database");
                    Log.d("message","data from db"+moreDbusers);
                    int i;
                    String[] array;
                    for (i = 0; i < moreDbusers.size(); i++) {
                        array = moreDbusers.get(i).split(",");
                        LatLng latlng = new LatLng(Double.parseDouble(array[1]), Double.parseDouble(array[2]));
                        Log.d("message", "Lat Long " + latlng);
                        handleNewLocation(latlng, array[0]);
                    }
                }
                else{
                    try {
                        Log.d("message","query parameters"+queries);
                        Log.d("message","downloading data from server");
                        afterid=getID();
                        int pagenumber= afterid/20;
                        Log.d("message","Maximum id in db is "+afterid);
                        DownloadWebpageTask userdownloadfilter = new DownloadWebpageTask();
                        serverusers = userdownloadfilter.execute("http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid"+afterid+"&"+queries+"&page="+pagenumber+"&pagesize=20").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray jObj = new JSONArray(serverusers);
                        Log.d("message", "jsonarray" + jObj);
                        Log.d("message", "jobj " + jObj.length());
                        for (int i = 0; i < jObj.length(); i++) {
                            //Log.d("message", "inside for loop");
                            JSONObject userJSON = jObj.getJSONObject(i);
                            Log.d("message", "inside for loop" + userJSON);
                            LatLng latlng = new LatLng(userJSON.getDouble("latitude"), userJSON.getDouble("longitude"));
                            Log.d("message","latlng"+latlng);
                            //lists.add(latlng);
                            finalString=userJSON.getString("nickname")+","+userJSON.getString("country")+","+userJSON.getString("state")+","+userJSON.getString("city")+","+userJSON.getString("year")+","+userJSON.getString("latitude")+","+userJSON.getString("longitude");
                            handleNewLocation(latlng, userJSON.getString("nickname"));
                            moreToServer.add(finalString);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int inserted=insertUser(moreToServer);
                }
            }
        });


        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void handleNewLocation(LatLng latLng,String name) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(name);
        gMap.addMarker(options).setDraggable(true);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

        gMap.clear();

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
            Toast.makeText(getActivity(), "command sent", Toast.LENGTH_LONG).show();
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
        Cursor c= db.rawQuery("select * from users where "+parameters+" limit 20"+" offset "+ n,null);
        c.moveToFirst();
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String user = c.getString(c.getColumnIndex("nickname")) + "," + c.getString(c.getColumnIndex("latitude")) + "," + c.getString(c.getColumnIndex("longitude"));
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
        Cursor c= db.rawQuery("select MAX(ID) from users where "+parameters,null);
        if(c.getCount()>0){
            c.moveToFirst();
            id = c.getInt(0);
        }
        return id;
    }
}

