package com.example.android.assignment5_dup;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by SINDHU on 16-03-2017.
 */

public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    SupportMapFragment mapFragment;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private static final int REQUEST_CODE=123;
    private View rootView;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private MapView mapView;
    private GoogleMap gMap;
    Bundle bundle;
    LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    double zoomLevel = 16.0;
    private LocationRequest mLocationRequest;
    Button map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mapfragment, container, false);
        map= (Button) rootView.findViewById(R.id.mapBtn);
        map.setVisibility(View.GONE);
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    gMap = map;
                    gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    LatLng myPosition;


                    map.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, true);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    final Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        myPosition = new LatLng(latitude, longitude);


                        LatLng coordinate = new LatLng(latitude, longitude);
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
                        gMap.animateCamera(yourLocation);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, (float) zoomLevel));
                    }

                    gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            gMap.clear();
                            gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            gMap.addMarker(markerOptions);
                            String latlong= Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude);
                            Log.d("message","latandlong "+latlong);

                            Intent intent = new Intent(getActivity(), PostFragment.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString("LATLONG", latlong);
                            intent.putExtras(mBundle);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                            FragmentManager fm =getActivity().getSupportFragmentManager();
                            fm.popBackStack();
                            //postFragment.latandlong.setText(latlong);

                           }
                    });
                }

            });
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), // Activity
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


    }


    private void handleNewLocation(Location location) {
        Log.d("MAps", location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        gMap.addMarker(options).setDraggable(true);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onResume() {
        super.onResume();

        mGoogleApiClient.connect();

    }

    @Override
    public void onPause() {
        super.onPause();

        gMap.clear();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 123);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
        /*
         * If no resolution is available, display a dialog to the
         * user with the error.
         */
            Log.i("MAPS", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }



}

