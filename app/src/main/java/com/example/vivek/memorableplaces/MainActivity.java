package com.example.vivek.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> placesArrayList;
    static ArrayList<LatLng> locations;
    static ArrayAdapter placesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.vivek.memorableplaces", Context.MODE_PRIVATE);

        ListView listView = (ListView)findViewById(R.id.listView);

        placesArrayList = new ArrayList<>();
        locations = new ArrayList<>();
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        placesArrayList.clear();
        longitudes.clear();
        latitudes.clear();
        locations.clear();

//        sharedPreferences.edit().remove("latitudes").commit();
//        sharedPreferences.edit().remove("longitudes").commit();
//        sharedPreferences.edit().remove("places").commit();


        try {

            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));


            for (int i = 0; i < latitudes.size(); i++){
                locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
            }
            Log.i("latitude",latitudes.toString());
            Log.i("longitude",longitudes.toString());

            placesArrayList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (placesArrayList.isEmpty()) {

            placesArrayList.add("Add you memorable place...");
            locations.add(new LatLng(0, 0));

        }

        placesArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesArrayList);
        listView.setAdapter(placesArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

                intent.putExtra("index",position);

                startActivity(intent);

            }
        });


    }
}
