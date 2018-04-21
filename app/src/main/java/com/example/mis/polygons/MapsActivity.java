package com.example.mis.polygons;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;
// shared preferences
//http://www.androidtrainee.com/adding-multiple-marker-locations-in-google-maps-android-api-v2-and-save-it-in-shared-preferences/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private EditText etText;
    private Button cleanMap, polygon;
    //private HashMap<String, List<String>> markersHashMap = new HashMap<String, List<String>>();
    private List<Double> coordinatesX = new ArrayList<Double>();
    private List<Double> coordinatesY = new ArrayList<Double>();
    private List<LatLng> pointsList;
    private LatLng[] pointsArr;

    SharedPreferences sharedPreferences;
    int locationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        etText = findViewById(R.id.etText);
        cleanMap = findViewById(R.id.cleanMap);
        polygon = findViewById(R.id.polygon);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Clean Map Button
        cleanMap.setOnClickListener(this);
        polygon.setOnClickListener(this);

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

                //Save markers into HashMap
    //            coordinates.clear();
               // markersHashMap.clear();
//                coordinates.add(title);
//                coordinates.add(lat);
//                coordinates.add(lng);
                //markersHashMap.put(title,coordinates);
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

        //save to HashMap
       // coordinates.add(text);
        coordinatesX.add(point.latitude);
        coordinatesY.add(point.longitude);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
       // markersHashMap.put(text,coordinates);
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
                //empty coordinates list
                coordinatesX.clear();
                coordinatesY.clear();
                break;
            case R.id.polygon:
                //changing text of a button
                changeText();
                //computing area between markers
                drawPolygon();
                break;
        }
    }

    private void drawPolygon() {
        pointsList = new ArrayList<LatLng>();
        //Adding coordinates of markers to draw polygon
        for(int i = 0; i<coordinatesX.size(); i++) {
            pointsList.add(new LatLng(coordinatesX.get(i), coordinatesY.get(i)));
        }
        // Convert arrayList to array
        //https://stackoverflow.com/questions/5374311/convert-arrayliststring-to-string-array
        pointsArr  = new LatLng[pointsList.size()];
        pointsArr = pointsList.toArray(pointsArr);

        //Draw polygon
        if(!pointsList.isEmpty()){
            mMap.addPolygon(new PolygonOptions()
                    .add(pointsArr)
                    .strokeColor(Color.RED).strokeWidth(1)
                    .fillColor(0x5500ff00)).setTag("FFF");
        }

        //Calculating area
        double area = computeArea();

        //Calculate Centroid
        //https://stackoverflow.com/questions/18440823/how-do-i-calculate-the-center-of-a-polygon-in-google-maps-android-api-v2
        LatLng centroid = calculateCentroid(pointsList);
        //Adding Marker in the center of the polygon
        if(!pointsList.isEmpty()){
            mMap.addMarker(new MarkerOptions()
                    .position(centroid)
                    .title("CENTROID "+ area)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
    }

    private LatLng calculateCentroid(List<LatLng> pointsList) {
        double x = 0;
        double y = 0;

        for (int i = 0; i < pointsList.size(); i++) {
            x += coordinatesX.get(i);
            y += coordinatesY.get(i);
        }

        int totalPoints = pointsList.size();
        x = x / totalPoints;
        y = y / totalPoints;

        LatLng centroid = new LatLng(x,y);
        return centroid;
    }

    private double computeArea() {
       // https://www.mathopenref.com/coordpolygonarea2.html
        double area = 0;
        Log.d("TAG1", "sdhgfdj" + String.valueOf(coordinatesX.size()));
        int numOfPoints = coordinatesX.size(); //or coordiantesY.size()
       // Toast.makeText(this,  coordinatesX.size(), Toast.LENGTH_SHORT).show();
        for(int i = 0; i<numOfPoints && coordinatesX.size()>0; i++){
            if(i==numOfPoints-1){//i/num==1
                area = area + (coordinatesX.get(i)*coordinatesY.get(0)) - (coordinatesX.get(0)*coordinatesY.get(i));
                Log.d("TAG1", "check " + i);
                Log.d("TAG1", "coord " + coordinatesX.get(i));
            }else{
                Log.d("TAG1", "coord " + coordinatesX.get(i));
                area = area + (coordinatesX.get(i)*coordinatesY.get(i+1)) - (coordinatesX.get(i+1)*coordinatesY.get(i));
            }
        }
        return area/2;
    }

    private void changeText() {
        if( polygon.getText().equals("Start Polygon")){
            polygon.setText("End Polygon");
        }else{
            polygon.setText("Start Polygon");
            pointsList.clear();
            pointsArr = null;
            coordinatesX.clear();
            coordinatesY.clear();
        }
    }
}
