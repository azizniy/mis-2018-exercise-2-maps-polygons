package com.example.mis.polygons;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
// shared preferences
//http://www.androidtrainee.com/adding-multiple-marker-locations-in-google-maps-android-api-v2-and-save-it-in-shared-preferences/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private EditText etText;
    private Button cleanMap;

    SharedPreferences sharedPreferences;
    int locationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        etText = findViewById(R.id.etText);
        cleanMap = findViewById(R.id.cleanMap);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Clean Map Button
        cleanMap.setOnClickListener(this);

        // Toast.makeText(this,savedInstanceState.getBundle(),Toast.LENGTH_SHORT).show();
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //long press -> create a marker
        mMap.setOnMapLongClickListener(this);
        //load markers
        loadMarkers();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        String title = etText.getText().toString();
        etText.setText("");
        locationCount++;
        // Drawing marker on the map
        drawMarker(latLng,title);
        saveMarker(latLng,title);
        Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
    }



    public void saveMarker(LatLng latLng,String title) {
        /** Opening the editor object to write data to sharedPreferences */
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Storing the latitude for the i-th location
        editor.putString("lat" + Integer.toString((locationCount - 1)), Double.toString(latLng.latitude));
        // Storing the longitude for the i-th location
        editor.putString("lng" + Integer.toString((locationCount - 1)), Double.toString(latLng.longitude));
        // Storing the longitude for the i-th location
        editor.putString("title" + Integer.toString((locationCount - 1)), title);
        // Storing the count of locations or marker count
        editor.putInt("locationCount", locationCount);
        /** Storing the zoom level to the shared preferences */
        editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));
        /** Saving the values stored in the shared preferences */
        editor.commit();
        Toast.makeText(getBaseContext(), "Marker is Saved", Toast.LENGTH_SHORT).show();
    }

    private void loadMarkers() {
        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);
        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);
        // Getting stored zoom level if exists else return 0
        String zoom = sharedPreferences.getString("zoom", "0");
        // If locations are already saved
        if (locationCount != 0) {
            String lat = "";
            String lng = "";
            String title = "";
            // Iterating through all the locations stored
            for (int i = 0; i < locationCount; i++) {
                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat" + i, "0");
                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng" + i, "0");
                //Getting title
                title = sharedPreferences.getString("title" + i, "0");
                // Drawing marker on the map
                drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),title );
            }
            // Moving CameraPosition to last clicked position
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
            // Setting the zoom level in the map on last position is clicked
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }

        Toast.makeText(this, "Markers are Loaded", Toast.LENGTH_SHORT).show();
        // printSavedMarkers();

    }

    private void drawMarker(LatLng point, String text) {
// Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
// Setting latitude and longitude for the marker
        markerOptions.position(point).title(text);
// Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cleanMap:
                // Removing the marker and circle from the Google Map
                mMap.clear();
                // Opening the editor object to delete data from sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing the editor
                editor.clear();
                // Committing the changes
                editor.commit();
                // Setting locationCount to zero
                locationCount=0;
                break;
            //case R.id.xxxxx:
        }
    }
}
