package com.example.vivek.memorableplaces;

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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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

import static com.example.vivek.memorableplaces.MainActivity.locations;
import static com.example.vivek.memorableplaces.MainActivity.placesArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;

    public void askLocationPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    public void centreOnMap(Location location,String title){

        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());


        if (title != "Your location") {

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,14));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,1,locationListener );

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

        sharedPreferences = getSharedPreferences("com.example.vivek.memorableplaces", Context.MODE_PRIVATE);

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

        Intent intent = getIntent();

        if (intent.getIntExtra("index",0) == 0) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centreOnMap(location,"Your location");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(MapsActivity.this, "Enable GPS!", Toast.LENGTH_SHORT).show();
                }
            };

            /*// Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            */

            if (Build.VERSION.SDK_INT < 23) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askLocationPermission();

                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,1,locationListener);

                }
            } else {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                        askLocationPermission();

                    } else {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, locationListener);
                    }

                }

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null)
                    centreOnMap(lastKnownLocation,"Your location");
        } else {

            int index = intent.getIntExtra("index",0);

            Location memorableLocation = new Location(LocationManager.GPS_PROVIDER);

            memorableLocation.setLatitude(MainActivity.locations.get(index).latitude);
            memorableLocation.setLongitude(MainActivity.locations.get(index).longitude);

            centreOnMap(memorableLocation, placesArrayList.get(index));

        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        String address = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            //address += addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare();
            String result[] = addresses.get(0).getAddressLine(0).toString().split(", ");

            for(int i = 0; i < 4; i++){
                if(result.length > i){
                    address += result[i] + " ";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == ""){

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yy-MM-dd");
            address = sdf.format(new Date());

        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        placesArrayList.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.placesArrayAdapter.notifyDataSetChanged();


        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        for (LatLng coordinates : locations){

            latitudes.add(Double.toString(coordinates.latitude));
            longitudes.add(Double.toString(coordinates.longitude));

        }
        try{

            sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(placesArrayList)).apply();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}

