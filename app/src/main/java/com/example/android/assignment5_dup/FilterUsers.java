package com.example.android.assignment5_dup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by SINDHU on 19-03-2017.
 */

public class FilterUsers extends AppCompatActivity {

    Spinner countryspinner,statespinner,yearspinner;
    Button backBtn,filterBtn,filterMapBtn;
    FrameLayout newFrame;
    HttpURLConnection urlConnection=null;
    String countries,states,country_selected,state_selected,year_selected,params,parameter,usersResult,query,queries;
    Boolean intialSelection=true,stateSelection=true,yearSelection=true;
    Bundle sendingBundle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filteruser);

        //Spinner
        countryspinner = (Spinner) findViewById(R.id.countriesspin);
        statespinner = (Spinner) findViewById(R.id.statesspin);
        yearspinner = (Spinner) findViewById(R.id.yearsspin);

        //Buttons
        backBtn = (Button) findViewById(R.id.goBack);
        filterBtn = (Button) findViewById(R.id.filterUsers);
        filterMapBtn = (Button) findViewById(R.id.mapBtn);

        //Frame Layout
        //newFrame = (FrameLayout) findViewById(R.id.mapHolder);

        //Populating country and state spinners
        try {
            Log.d("message", "inside country try block line 76");
            DownloadWebpageTask countryTask = new DownloadWebpageTask();
            countries = countryTask.execute("http://bismarck.sdsu.edu/hometown/countries").get();
            countries = countries.replace("[", "");
            countries = countries.replace("]", "");
            countries = countries.replace("\"", "");
            Log.d("message", "Countries returned from list view asyctask" + countries);
            ArrayList<String> countriesList = new ArrayList<String>(Arrays.asList(countries.split(",")));
            countriesList.add("Select Country");
            ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countriesList);
            countryspinner.setAdapter(countryAdapter);
            int position=countryAdapter.getPosition("Select Country");
            countryspinner.setSelection(position);
            countryspinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                            try {
                                if(!intialSelection){
                                    country_selected=countryspinner.getItemAtPosition(position).toString();
                                    Log.d("message", "inside state try block line 86 from list view asyctask");
                                    DownloadWebpageTask stateTask = new DownloadWebpageTask();
                                    states = stateTask.execute("http://bismarck.sdsu.edu/hometown/states?country=" + country_selected).get();
                                    states = states.replace("[", "");
                                    states = states.replace("]", "");
                                    states = states.replace("\"", "");
                                    Log.d("mesage", "states returned from list view asyctask" + states);
                                    ArrayList<String> stateList = new ArrayList<String>(Arrays.asList(states.split(",")));
                                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(FilterUsers.this, android.R.layout.simple_spinner_item, stateList);
                                    statespinner.setAdapter(stateAdapter);
                                    statespinner.setOnItemSelectedListener(
                                            new AdapterView.OnItemSelectedListener() {
                                                public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                                                    if(!stateSelection) {
                                                        state_selected = statespinner.getItemAtPosition(position).toString();
                                                    }
                                                    stateSelection=false;

                                                }
                                                public void onNothingSelected(AdapterView<?> arg0) {
                                                    // TODO Auto-generated method stub
                                                }
                                            });
                                } }catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            intialSelection=false;
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
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("Select an year!");
        for (int i = 1970; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        int yposition = adapter.getPosition("Select an year!");
        yearspinner = (Spinner) findViewById(R.id.yearsspin);
        yearspinner.setAdapter(adapter);
        yearspinner.setSelection(yposition);
        yearspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (!yearSelection) {
                    year_selected = yearspinner.getItemAtPosition(position).toString();
                }
                yearSelection=false;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //Button click listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(country_selected!=null) {
                    params="country=\""+country_selected+"\"";
                    query ="country="+country_selected;
                }
                if(state_selected!=null) {
                    params+=" AND state=\""+state_selected+"\"";
                    query ="&state="+state_selected;
                }
                if(year_selected!=null) {
                    params+=" AND year="+year_selected;
                    query ="&year="+year_selected;
                }
                Log.d("message","from filterusers.java"+params);
                Intent intent=new Intent(FilterUsers.this,ListViewFilteredUsers.class);
                intent.putExtra("parameter",params);
                intent.putExtra("queryParameter",query);
                startActivity(intent);
            }


        });

        filterMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(country_selected!=null) {
                    queries="country=\""+country_selected+"\"";
                    parameter="country="+country_selected;

                }
                if(state_selected!=null) {
                    queries = "AND state=\""+state_selected+"\"";
                    parameter+="&state="+state_selected;

                }
                if(year_selected!=null) {
                    queries = "AND year=\""+year_selected+"\"";
                    parameter+="&year="+year_selected;
                }

                Intent newintent = new Intent(FilterUsers.this,MapDummy.class);
                newintent.putExtra("parameters",parameter);
                newintent.putExtra("queries",queries);
                startActivity(newintent);
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
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readIt(in, contentLength);
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

        public String readIt(InputStream stream, int len) throws IOException,
                UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }
}
