package com.example.android.assignment5_dup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by SINDHU on 18-03-2017.
 */

public class PostFragment extends Fragment {
    TextView nickName, paswrd, country, state, city, year;
    EditText nickEdit, paswrdEdit, citiesEdit,emailEdit;
    TextView latandlong;
    Spinner country_spin, state_spin, spinYear;
    Button latandlongbtn, submit,done;
    Double lat, longi;
    Boolean duplicate, countrydrop = true;
    private View frootView;
    String countries, states, cities, country_selected, state_selected, city_selected, ANickName, latlong;
    String[] latlongArray;
    HttpURLConnection urlConnection = null;
    JSONObject post = new JSONObject();
    int REQUEST_CODE = 123;
    String locationValue=null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d("MAps", "Maps, Post, onCreateView()");
        firebaseAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference("users");
        //Inflate the layout for this fragment
        frootView = inflater.inflate(
                R.layout.postfragment, container, false);
        nickName = (TextView) frootView.findViewById(R.id.nickNameText);
        paswrd = (TextView) frootView.findViewById(R.id.passwordText);
        country = (TextView) frootView.findViewById(R.id.countryText);
        state = (TextView) frootView.findViewById(R.id.stateText);
        city = (TextView) frootView.findViewById(R.id.cityText);
        year = (TextView) frootView.findViewById(R.id.yearText);


        //Edit text

        nickEdit = (EditText) frootView.findViewById(R.id.nickNameEdit);
        paswrdEdit = (EditText) frootView.findViewById(R.id.passwordEdit);
        citiesEdit = (EditText) frootView.findViewById(R.id.cityEdit);
        latandlong = (TextView) frootView.findViewById(R.id.latlongText);
        emailEdit =(EditText) frootView.findViewById(R.id.emailEdit);
        //Spinners
        country_spin = (Spinner) frootView.findViewById(R.id.countryspin);
        state_spin = (Spinner) frootView.findViewById(R.id.statespin);

        //Button
        latandlongbtn = (Button) frootView.findViewById(R.id.latlongBtn);
        submit = (Button) frootView.findViewById(R.id.save);


        nickEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Log.d("message", "Edit text nickname lost focus!");
                    DownloadWebpageTask nickNameDup = new DownloadWebpageTask();
                    ANickName = nickEdit.getText().toString();
                    Log.d("message", "Nick Name is " + ANickName);
                    try {
                        String url = "http://bismarck.sdsu.edu/hometown/nicknameexists?name=ANickName";
                        Log.d("message", "url is " + url);
                        duplicate = Boolean.parseBoolean(nickNameDup.execute("http://bismarck.sdsu.edu/hometown/nicknameexists?name=" + ANickName).get());
                        Log.d("message", "exists?" + duplicate);
                        if (duplicate) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Nick Name already exists!Pick a new one!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            nickEdit.setFocusable(true);
                                            nickEdit.setText("");
                                            nickEdit.requestFocus();

                                        }
                                    });
                            alertDialog.show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        try {
            Log.d("message", "inside country try block line 76");
            DownloadWebpageTask countryTask = new DownloadWebpageTask();
            countries = countryTask.execute("http://bismarck.sdsu.edu/hometown/countries").get();
            countries = countries.replace("[", "");
            countries = countries.replace("]", "");
            countries = countries.replace("\"", "");
            Log.d("message", "Countries returned" + countries);
            ArrayList<String> countryList = new ArrayList<String>(Arrays.asList(countries.split(",")));
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countryList);
            country_spin.setAdapter(countryAdapter);
            country_spin.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                            try {
                                if (!countrydrop) {
                                    country_selected = parent.getItemAtPosition(position).toString();
                                    Log.d("message", "inside state try block line 86");
                                    DownloadWebpageTask stateTask = new DownloadWebpageTask();
                                    states = stateTask.execute("http://bismarck.sdsu.edu/hometown/states?country=" + country_selected).get();
                                    states = states.replace("[", "");
                                    states = states.replace("]", "");
                                    states = states.replace("\"", "");
                                    Log.d("mesage", "states returned" + states);
                                    ArrayList<String> stateList = new ArrayList<String>(Arrays.asList(states.split(",")));
                                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stateList);
                                    state_spin.setAdapter(stateAdapter);
                                }
                                countrydrop = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub
                        }
                    });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

// Populating year spinner
        ArrayList<String> years = new ArrayList<String>();
        years.add("Select a year!");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        spinYear = (Spinner) frootView.findViewById(R.id.yearspin);
        spinYear.setAdapter(adapter);
        int yposition = adapter.getPosition("Select Country!");
        spinYear.setSelection(yposition);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidNickName(getNickName()) && isValidPassword(getPassword()) && isValidCity(getCity())){
                    JSONObject params = new JSONObject();
                    try {
                        params.put("nickname",getNickName());
                        params.put("password",getPassword());
                        params.put("country", getCountry());
                        params.put("state",getState());
                        params.put("city",getCity());
                        params.put("year",getYear());
                        if(latlong!= null){
                            String[] values =latlong.split(",");
                            params.put("latitude",Double.parseDouble(values[0]));
                            params.put("longitude",Double.parseDouble(values[1]));
                        }
                    } catch (JSONException error) {
                        Log.e("rew", "JSON eorror", error);
                        return;
                    }

                    registerUser(params);
                    registerwithFirebase();
                }
            }
        });

        latandlongbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MapFragment mapFragment = new MapFragment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragtran = fm.beginTransaction();
                        fragtran.addToBackStack("");
                        mapFragment.setTargetFragment(PostFragment.this,12);
                        fragtran.replace(R.id.mapFrame, mapFragment, "MAP_FRGAMENT_TAG");
                        fragtran.commit();
                    }
                }, 300);
            }
        });



        latandlong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                latlong=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return frootView;

    }

    private void registerwithFirebase() {
        firebaseAuth.createUserWithEmailAndPassword(getEmail(),getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getActivity(),"Successfully registered with Firebase",Toast.LENGTH_SHORT).show();
            }
        });

        UserPOJO user= new UserPOJO(getEmail(),getPassword());
        userRef.child(getNickName()).setValue(user);
        Log.d("message","Successfully added to Firebase db");

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 12) {
                {
                    locationValue = data.getExtras().getString("LATLONG");
                    Log.d("message", "value got" + locationValue);
                }
            }
        }
    }

    private boolean isValidNickName(String name) {
        return !TextUtils.isEmpty(name) && name.matches("[a-zA-Z]+([ '-][a-zA-Z]+)*");
    }

    private boolean isValidPassword(String passwrd) {
        return !TextUtils.isEmpty(passwrd) && (passwrd.length() > 3);
    }

    private boolean isValidCity(String city) {
        return !TextUtils.isEmpty(city) && city.matches("[a-zA-Z]+([ '-][a-zA-Z]+)*");
    }

    private String getNickName() {
        return nickEdit.getText().toString().trim();
    }

    private String getPassword() {
        return paswrdEdit.getText().toString().trim();
    }

    private String getCountry() {
        return country_spin.getSelectedItem().toString();
    }

    private String getState() {
        return state_spin.getSelectedItem().toString();
    }

    private String getCity() {
        return citiesEdit.getText().toString().trim();
    }

    private int getYear() {

        String  years = spinYear.getSelectedItem().toString();
           int choosenYear = Integer.parseInt(years);
    return choosenYear;
    }


    private String getEmail(){return emailEdit.getText().toString();}



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

        protected void onPostExecute(Double result) {
            //pb.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "command sent", Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress) {
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
                    Log.d("message", "Server not happy");
                }
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readIt(in, contentLength);
            } catch (MalformedURLException badURL) {
                Log.e("rew", "Bad URL", badURL);
            } catch (IOException io) {
                Log.e("rew", "network issue", io);
            } finally {
                urlConnection.disconnect();
            }
            return "";//error
        }

        public String readIt(InputStream stream, int len) throws IOException,
                UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(locationValue!=null){
            latandlong.setText(locationValue);
        }
    }

    private void registerUser(JSONObject params){
        String REGISTER_URL= "http://bismarck.sdsu.edu/hometown/adduser";

        JsonObjectRequest stringRequest = new JsonObjectRequest( Request.Method.POST , REGISTER_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d("rew", "JSON success");

                        Toast.makeText(getActivity(),"Successfully added!",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(),Login.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<String, String>();
                return header;
            }

            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }



        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}


