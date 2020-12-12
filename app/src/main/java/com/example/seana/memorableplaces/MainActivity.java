package com.example.seana.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // so here the places will save strings representing the details of the address
    static ArrayList<String> places = new ArrayList<String>();
    // while locations has the details of the  coordinates
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // so we get access to the shared preferences were the permanent storage is
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.seana.memorableplaces",Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> longitude = new ArrayList<String>();


        // we want to work with a fresh array list each time so that we do not get any duplicated in the listview
        places.clear();
        locations.clear();
        latitudes.clear();
        longitude.clear();

        // so here we will try and pull the data from places, lats and lons from the shared preferences
        try {
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Index",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Lat",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Lon",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // so if all the details are present
        if (places.size() > 0 && latitudes.size() > 0 && longitude.size() > 0) {
            // and that we have the lat nd lon for each of our desire to be listed places
            if (places.size() == latitudes.size() && places.size() == longitude.size()) {
                for(int i = 0; i < places.size(); i++) {
                    // so now we will create the coordinates for the loction by adding the lat nd lon
                    // so now when the user presses the coordinates to mark the place will be present
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitude.get(i))));
                }
            }
        } else {
            // so if nothing has been specified yet we just create the firt ones ourselves
            places.add("Add a new place....");
            locations.add(new LatLng(0,0));
        }





        // so we create a listview to store all the different locations
        ListView listView = (ListView) findViewById(R.id.listView);

        // and then  we create and set the adapter
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);

        // and when clicked we send the user to the maps activity (utilising google maps) that allows user to store location
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("List Number",i);
                startActivity(intent);
            }
        });




    }
}
