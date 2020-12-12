package com.example.seana.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;


    public void centreMapOnLocation(Location location, String title) {

        // so if a location is available
        if (location != null) {
            // we will get the location
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            // claer any previous markers that are available
            mMap.clear();
            // and add a marker for this current location
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        }

    }


    // so once the permission is granted we will set the location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // so first the location is requested
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
                // then we get the last set location
                Location getLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // and we call the function as per the location
                centreMapOnLocation(getLastKnownLocation, "You are here!");
            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        // so first we will get the intent
        Intent intent = getIntent();

        // now if the extra is 0 that means user has picked the first item meaning that we should just zoom in on the user's current location
        if (intent.getIntExtra("List Number", 0) == 0) {
            // so on the map we get the liocations
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // and when the location is change we set the function so that the marker is changed
                    centreMapOnLocation(location, "Your location");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            // and here we are checking whether or not we have permission if we do not we are going to once again ask for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            // otherwise we will signify the location to the user
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
                Location getLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(getLastKnownLocation, "You are here!");

            }
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            // so if another location (not the first one) is clicked we get the latitude and longitude as per the list item chpsen
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("List Number", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("List Number", 0)).longitude);
            // and set the location as such
            centreMapOnLocation(placeLocation,"this is");
        }


    }

    // so on a long click we will set a new location marker and save it to the list so it will show in the main activity
    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";
        try {
            // so we get the location and its infofrom the geocodert
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            // if the above var was successfully initialised we get all the relevant info
            if (listAddresses.size() > 0 && listAddresses != null) {



                // all this info is only saved if it is available as per google
                if (listAddresses.get(0).getThoroughfare() != null) {
                    address=listAddresses.get(0).getThoroughfare() + " ";
                } else {
                    address = "";
                }

                if (listAddresses.get(0).getLocality() != null) {
                    address+=listAddresses.get(0).getLocality();
                }


                if (listAddresses.get(0).getPostalCode() != null) {
                    address+=listAddresses.get(0).getPostalCode() + " ";
                }

                if (listAddresses.get(0).getAdminArea() != null) {
                    address+=listAddresses.get(0).getAdminArea();
                }

            }



        } catch (IOException e) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm YYYY-MM-dd");
            address+=sdf.format(new Date());
            e.printStackTrace();
        }


        // so we add the marker for the new place that we chose
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        // we save the string details for the listview
        MainActivity.places.add(address);
        // as well as the actual geographical changes so as to send the user to location when the list item is chosen
        MainActivity.locations.add(latLng);

        MainActivity.arrayAdapter.notifyDataSetChanged();



        // and we save everything permanently
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.seana.memorableplaces",Context.MODE_PRIVATE);

        try {


            // so for the latlng we are going to create seperate array lists for the lat nd lon so as to save the details of the location#
            ArrayList<String> latitudes = new ArrayList<String>();
            ArrayList<String> longitude = new ArrayList<String>();

            // we iterate through the seperate lats and lions
            for (LatLng coOrd: MainActivity.locations) {
                // and here is were we save the string values of the longitude and longitude of the chosen area
                latitudes.add(Double.toString(coOrd.longitude));
                longitude.add(Double.toString(coOrd.latitude));
            }

            // so here we are editing the permanent info as per the ObjectSerializer.java which helps us serialize and de-serialize the info
            sharedPreferences.edit().putString("Index",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("Lat",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("Lon",ObjectSerializer.serialize(longitude)).apply();



        } catch (IOException e) {
            e.printStackTrace();
        }


        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();


    }
}
